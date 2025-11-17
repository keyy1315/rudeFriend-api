package com.loltft.rudefriend.service

import com.loltft.rudefriend.dto.board.BoardRequest
import com.loltft.rudefriend.dto.board.BoardResponse
import com.loltft.rudefriend.dto.enums.DateOption
import com.loltft.rudefriend.dto.enums.GameType
import com.loltft.rudefriend.dto.vote.VoteResultResponse
import com.loltft.rudefriend.entity.Board
import com.loltft.rudefriend.entity.Member
import com.loltft.rudefriend.entity.Vote
import com.loltft.rudefriend.entity.VoteSummary
import com.loltft.rudefriend.entity.VoteSummaryId
import com.loltft.rudefriend.repository.board.BoardRepository
import com.loltft.rudefriend.repository.vote.VoteRepository
import com.loltft.rudefriend.utils.ConvertDateToDateTime
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate
import java.util.*

@Service
@Transactional
class BoardService(
    private val boardRepository: BoardRepository,
    private val voteRepository: VoteRepository,
    private val passwordEncoder: PasswordEncoder,
    private val fileService: SaveFileService,
    private val memberService: MemberService
) {
    companion object {
        private const val FROM_KEY = "from"
        private const val TO_KEY = "to"
    }

    private val convertDateToDateTime = ConvertDateToDateTime()

    /**
     * 게시글 PK로 엔티티를 조회한다.
     *
     * @param id 조회할 게시글 ID
     * @return 존재하는 게시글 엔티티
     * @throws NoSuchElementException 조회 결과가 없을 때
     */
    fun findById(id: UUID): Board {
        val board: Board? = boardRepository.findById(id)
            .orElseThrow { NoSuchElementException("존재하지 않는 게시글 ID : $id") }
        return board!!
    }

    /**
     * 게시글을 생성하고 파일을 업로드한다.
     *
     * @param files         업로드할 이미지/동영상 리스트
     * @param boardRequest  게시글 생성 요청 DTO
     * @param authUsername  인증된 작성자의 사용자 이름
     * @return 생성된 게시글 응답 DTO
     */
    fun createBoard(
        files: List<MultipartFile>, boardRequest: BoardRequest, authUsername: String, isAnonymous: Boolean
    ): BoardResponse {
        if (isAnonymous && boardRequest.password.isNullOrBlank()) throw IllegalArgumentException("게시글 비밀번호는 필수값 입니다.")

        val encodedPassword =
            boardRequest.password?.takeIf { it.isNotBlank() }?.let { passwordEncoder.encode(it) }
        val voteItems = resolveVoteItems(boardRequest)

        val board = Board(
            id = UUID.randomUUID(),
            title = boardRequest.title,
            content = boardRequest.content,
            gameType = boardRequest.gameType,
            tags = boardRequest.tags,
            createdBy = authUsername,
            password = encodedPassword,
        ).apply {
            voteEnabled = boardRequest.voteEnabled
            this.voteItems = voteItems.toMutableList()
        }
        boardRepository.save(board)
        if (board.voteEnabled) {
            initializeVoteSummaries(board)
        }

        val fullFileUrls = try {
            val uploadedFiles = fileService.uploadFiles(
                boardRequest.gameType.name, files, board.id
            )
            uploadedFiles.map { it.fullUrl }.toMutableList()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

        return BoardResponse.of(board, fullFileUrls)
    }

    /**
     * 게시글을 수정하고 필요 시 첨부 파일을 갱신한다.
     *
     * @param id            수정할 게시글 ID
     * @param files         새로 업로드할 파일 리스트 (없으면 빈 리스트)
     * @param boardRequest  수정된 게시글 정보
     * @return 수정 결과 응답 DTO
     */
    fun updateBoard(
        id: UUID, files: List<MultipartFile>, boardRequest: BoardRequest
    ): BoardResponse {
        val board = findById(id)
        val previousVoteItems = if (board.voteEnabled) board.voteItems.toList() else emptyList()
        val wasVoteEnabled = board.voteEnabled

        if (files.isNotEmpty()) {
            fileService.uploadFiles(
                boardRequest.gameType.name, files, board.id
            )
        }

        if (!boardRequest.shouldDeleteFileUrls.isNullOrEmpty()) {
            fileService.deleteFilesByFullUrls(boardRequest.shouldDeleteFileUrls!!)
        }

        if (!boardRequest.password.isNullOrBlank()) {
            boardRequest.password = passwordEncoder.encode(boardRequest.password)
        }
        val voteItems = resolveVoteItems(boardRequest)
        board.updateBoard(boardRequest, voteItems)
        synchronizeVoteSummaries(board, previousVoteItems, wasVoteEnabled)

        val saveFiles = fileService.findByBoardId(board.id).map { it.fullUrl }.toMutableList()

        return BoardResponse.of(board, saveFiles)
    }

    /**
     * 익명 사용자의 게시글 비밀번호를 검증한다.
     *
     * @param id         게시글 ID
     * @param password   사용자가 입력한 비밀번호
     * @return 저장된 비밀번호와 일치 여부
     */
    @Transactional(readOnly = true)
    fun checkBoardPassword(id: UUID, password: String): Boolean {
        val board = findById(id)

        return passwordEncoder.matches(password, board.password)
    }

    /**
     * 게시글을 삭제한다.
     *
     * @param id         삭제할 게시글 ID
     * @param username   삭제 요청자 사용자 이름
     * @throws AccessDeniedException 작성자가 아닐 경우
     */
    fun deleteBoard(id: UUID, username: String) {
        val board = findById(id)

        if (board.createdBy != username) {
            throw AccessDeniedException("작성자만 게시글을 삭제할 수 있습니다.")
        }
        try {
            fileService.deleteFilesByBoardId(board.id)
        } catch (e: Exception) {
            throw IllegalStateException("파일 삭제 중 오류가 발생하였습니다.", e)
        }

        boardRepository.delete(board)
    }

    /**
     * 게시글을 단건 조회한다.
     *
     * @param id 조회할 게시글 ID
     * @return 게시글 응답 DTO
     */
    @Transactional(readOnly = true)
    fun getBoard(id: UUID): BoardResponse {
        val board = findById(id)

        val files = fileService.findByBoardId(board.id).map { it.fullUrl }.toMutableList()
        return BoardResponse.of(board, files)
    }

    /**
     * 게시글 목록을 조건에 맞게 페이징 조회한다.
     *
     * @param dateFrom    조회 시작일
     * @param dateTo      조회 종료일
     * @param dateOption  등록/수정 기준
     * @param search      검색어
     * @param gameType    게임 타입
     * @param pageNo      페이지 번호
     * @param tags        태그 리스트
     * @param author      작성자 ID/IP
     * @return 게시글 목록과 전체 건수
     */
    @Transactional(readOnly = true)
    fun getBoards(
        dateFrom: LocalDate?,
        dateTo: LocalDate?,
        dateOption: DateOption?,
        search: String?,
        gameType: GameType?,
        pageNo: Int,
        tags: List<String>?,
        author: String?
    ): Pair<List<BoardResponse>, Long> {
        val dateTimeMap = convertDateToDateTime.convertMap(dateFrom, dateTo)

        val (entities, total) = boardRepository.findPageWithTotal(
            dateTimeMap[FROM_KEY],
            dateTimeMap[TO_KEY],
            dateOption,
            search,
            gameType,
            pageNo,
            tags,
            author
        )

        return Pair(entities, total)
    }

    /**
     * 투표 기능이 활성화된 게시글에 대해 사용자의 선택을 반영한다.
     *
     * @param boardId        투표 대상 게시글 ID
     * @param voteItem       사용자가 선택한 항목
     * @param userInfo       로그인 사용자의 ID (익명일 경우 IP 주소)
     * @param isAnonymous      익명 사용자 여부
     * @return 현재 집계된 투표 요약 정보
     */
    fun voteOnBoard(
        boardId: UUID,
        voteItem: String?,
        userInfo: String?,
        isAnonymous: Boolean
    ): VoteResultResponse {
        val board = findById(boardId)

        require(board.voteEnabled) {
            "투표 기능이 비활성화된 게시글입니다."
        }

        val sanitizedVoteItem = voteItem?.trim()?.takeIf { it.isNotBlank() }
            ?: throw IllegalArgumentException("투표 항목은 필수입니다.")

        val matchedItem = board.voteItems
            .firstOrNull { it.equals(sanitizedVoteItem, ignoreCase = true) }
            ?: throw IllegalArgumentException("존재하지 않는 투표 항목입니다.")

        val member = if (isAnonymous) null else resolveMember(userInfo)

        val vote = when {
            member != null -> voteRepository.findByBoardAndMember(board, member)
            else -> voteRepository.findByBoardIdAndIpAddress(boardId, userInfo.toString())
        }

        val previousVoteItem = normalizeVoteItem(board, vote?.voteItem)

        if (vote != null) {
            val changedSelection = previousVoteItem?.equals(matchedItem, ignoreCase = true) != true
            vote.voteItem = matchedItem
            vote.ipAddress = userInfo
            if (changedSelection) {
                previousVoteItem?.let { applyVoteDelta(boardId, it, -1L) }
                applyVoteDelta(boardId, matchedItem, 1L)
            }
        } else {
            voteRepository.save(
                Vote(
                    id = UUID.randomUUID(),
                    board = board,
                    member = member,
                    ipAddress = if (member == null) userInfo else null,
                    voteItem = matchedItem
                )
            )
            applyVoteDelta(boardId, matchedItem, 1L)
        }

        val summary = buildVoteSummary(board, boardId)
        val totalVotes = summary.values.sum()

        return VoteResultResponse(
            boardId = board.id,
            selectedItem = matchedItem,
            voteCounts = summary,
            totalVotes = totalVotes
        )
    }

    /**
     * 투표 기능이 켜진 게시글 요청에서 유효한 투표 항목 리스트를 생성한다.
     *
     * @param boardRequest 투표 설정이 포함된 게시글 요청 본문
     * @return 트리밍/중복 제거된 투표 항목 리스트
     * @throws IllegalArgumentException 항목 수가 2개 미만일 때
     */
    private fun resolveVoteItems(boardRequest: BoardRequest): List<String> {
        if (!boardRequest.voteEnabled) {
            return emptyList()
        }

        val normalized = boardRequest.voteItems
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() }
            ?.distinct()
            ?: emptyList()

        require(normalized.size >= 2) {
            "투표 시스템을 사용하려면 최소 2개 이상의 투표 항목이 필요합니다."
        }

        return normalized
    }

    /**
     * 요청한 사용자명이 존재하면 회원 엔티티를 조회한다.
     *
     * @param memberUsername 인증 사용자 ID 또는 null
     * @return 조회된 회원 엔티티
     */
    private fun resolveMember(memberUsername: String?): Member? {
        if (memberUsername.isNullOrBlank()) {
            return null
        }

        return memberService.findByMemberId(memberUsername)
    }

    /**
     * 게시글의 투표 항목 집계를 초기화하거나 갱신한다.
     *
     * @param board                    대상 게시글
     * @param seedFromExistingVotes    기존 투표 내역을 기반으로 집계를 복원할지 여부
     */
    private fun initializeVoteSummaries(
        board: Board,
        seedFromExistingVotes: Boolean = false
    ) {
        if (!board.voteEnabled || board.voteItems.isEmpty()) {
            voteRepository.deleteAllSummariesByBoardId(board.id)
            return
        }

        val existingCounts = if (seedFromExistingVotes) {
            voteRepository.countVoteGroupByItem(board.id)
                .associate { it.voteItem.lowercase() to it.count }
        } else {
            emptyMap()
        }

        voteRepository.deleteAllSummariesByBoardId(board.id)
        val summaries = board.voteItems.map { item ->
            VoteSummary(
                id = VoteSummaryId(board.id, item),
                voteCount = existingCounts[item.lowercase()] ?: 0L
            )
        }
        if (summaries.isNotEmpty()) {
            voteRepository.saveAllVoteSummaries(summaries)
        }
    }

    /**
     * 게시글 수정 시 투표 항목과 집계 테이블을 동기화한다.
     *
     * @param board             수정된 게시글
     * @param previousVoteItems 수정 전 투표 항목
     * @param wasVoteEnabled    수정 전 투표 사용 여부
     */
    private fun synchronizeVoteSummaries(
        board: Board,
        previousVoteItems: List<String>,
        wasVoteEnabled: Boolean
    ) {
        if (!board.voteEnabled) {
            voteRepository.deleteAllSummariesByBoardId(board.id)
            return
        }

        if (!wasVoteEnabled) {
            initializeVoteSummaries(board, seedFromExistingVotes = true)
            return
        }

        val removed = previousVoteItems.filterNot { board.voteItems.contains(it) }
        if (removed.isNotEmpty()) {
            voteRepository.deleteSummariesByBoardIdAndVoteItemIn(board.id, removed)
        }

        val existingItems = voteRepository.findAllSummariesByBoardId(board.id)
            .map { it.id.voteItem }
            .toSet()
        val newItems = board.voteItems.filterNot { existingItems.contains(it) }
        if (newItems.isNotEmpty()) {
            val summaries = newItems.map { VoteSummary(VoteSummaryId(board.id, it), 0L) }
            voteRepository.saveAllVoteSummaries(summaries)
        }
    }

    /**
     * 투표 항목의 집계를 증분 갱신한다.
     *
     * @param boardId  게시글 ID
     * @param voteItem 투표 항목
     * @param delta    증분 값(양수/음수)
     */
    private fun applyVoteDelta(boardId: UUID, voteItem: String, delta: Long) {
        val updated = voteRepository.applySummaryDelta(boardId, voteItem, delta)
        if (updated == 0L) {
            require(delta >= 0) {
                "투표 집계 정보가 존재하지 않아 음수 증분을 적용할 수 없습니다."
            }
            voteRepository.saveVoteSummary(
                VoteSummary(
                    id = VoteSummaryId(boardId, voteItem),
                    voteCount = delta
                )
            )
        }
    }

    /**
     * 저장된 투표 항목 문자열을 게시글 정의와 동일한 케이스로 정규화한다.
     *
     * @param board    투표 항목을 보유한 게시글
     * @param voteItem 정규화할 투표 항목
     * @return 게시글에 정의된 항목 문자열 또는 null
     */
    private fun normalizeVoteItem(board: Board, voteItem: String?): String? {
        if (voteItem.isNullOrBlank()) {
            return null
        }
        return board.voteItems.firstOrNull { it.equals(voteItem, ignoreCase = true) } ?: voteItem
    }

    /**
     * 게시글의 전체 투표 현황을 항목별 득표수로 구성한다.
     *
     * @param board     투표 항목 정보를 가진 게시글 엔티티
     * @param boardId   투표 내역을 조회할 게시글 ID
     * @return 항목명과 누적 득표수를 매핑한 결과
     */
    private fun buildVoteSummary(board: Board, boardId: UUID): Map<String, Long> {
        var summaries = voteRepository.findAllSummariesByBoardId(boardId)

        if (summaries.isEmpty() && board.voteEnabled && board.voteItems.isNotEmpty()) {
            initializeVoteSummaries(board, seedFromExistingVotes = true)
            summaries = voteRepository.findAllSummariesByBoardId(boardId)
        }

        val counts = summaries
            .associate { it.id.voteItem to it.voteCount }
            .toMutableMap()

        board.voteItems.forEach { item ->
            counts.putIfAbsent(item, 0L)
        }

        return board.voteItems
            .associateWith { counts[it] ?: 0L }
    }
}
