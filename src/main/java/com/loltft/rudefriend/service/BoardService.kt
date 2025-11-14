package com.loltft.rudefriend.service

import com.loltft.rudefriend.dto.board.BoardRequest
import com.loltft.rudefriend.dto.board.BoardResponse
import com.loltft.rudefriend.dto.enums.DateOption
import com.loltft.rudefriend.dto.enums.GameType
import com.loltft.rudefriend.dto.vote.VoteResultResponse
import com.loltft.rudefriend.entity.Board
import com.loltft.rudefriend.entity.Member
import com.loltft.rudefriend.entity.Vote
import com.loltft.rudefriend.repository.board.BoardRepository
import com.loltft.rudefriend.repository.member.MemberRepository
import com.loltft.rudefriend.repository.vote.VoteRepository
import com.loltft.rudefriend.utils.ConvertDateToDateTime
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate
import java.util.UUID

@Service
@Transactional
class BoardService(
    private val boardRepository: BoardRepository,
    private val memberRepository: MemberRepository,
    private val voteRepository: VoteRepository,
    private val passwordEncoder: PasswordEncoder,
    private val fileService: SaveFileService
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
        files: List<MultipartFile>, boardRequest: BoardRequest, authUsername: String
    ): BoardResponse {
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

    fun voteOnBoard(
        boardId: UUID,
        voteItem: String?,
        memberUsername: String?,
        ipAddress: String
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

        val member = resolveMember(memberUsername)
        val normalizedIp = ipAddress.ifBlank { "unknown" }

        val vote = when {
            member != null -> voteRepository.findByBoardAndMember(board, member)
            else -> voteRepository.findByBoardIdAndIpAddress(boardId, normalizedIp)
        }

        if (vote != null) {
            vote.voteItem = matchedItem
            vote.ipAddress = normalizedIp
        } else {
            voteRepository.save(
                Vote(
                    id = UUID.randomUUID(),
                    board = board,
                    member = member,
                    ipAddress = normalizedIp,
                    voteItem = matchedItem
                )
            )
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

    private fun resolveMember(memberUsername: String?): Member? {
        if (memberUsername.isNullOrBlank()) {
            return null
        }

        val optional = memberRepository.findByMemberId(memberUsername)
        return optional?.orElse(null)
    }

    private fun buildVoteSummary(board: Board, boardId: UUID): Map<String, Long> {
        val counts = voteRepository.countVoteGroupByItem(boardId)
            .associate { it.voteItem to it.count }
            .toMutableMap()

        board.voteItems.forEach { item ->
            counts.putIfAbsent(item, 0L)
        }

        return board.voteItems
            .associateWith { counts[it] ?: 0L }
    }
}
