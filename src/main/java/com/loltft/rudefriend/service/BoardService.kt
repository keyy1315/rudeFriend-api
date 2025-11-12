package com.loltft.rudefriend.service

import com.loltft.rudefriend.dto.board.BoardRequest
import com.loltft.rudefriend.dto.board.BoardResponse
import com.loltft.rudefriend.entity.Board
import com.loltft.rudefriend.repository.board.BoardRepository
import org.slf4j.LoggerFactory
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class BoardService(
    private val boardRepository: BoardRepository,
    private val s3Service: S3Service,
    private val passwordEncoder: PasswordEncoder
) {
    fun findById(id: UUID): Board {
        val board: Board? = boardRepository.findById(id)
            .orElseThrow { NoSuchElementException("존재하지 않는 게시글 ID : $id") }
        return board!!
    }

    /**
     * 게시글 생성
     *
     * @param files        업로드 할 video/image []
     * @param boardRequest 게시글 생성 요청 DTO
     * @param authUsername Authentication 객체에 저장된 username
     * @return 생성 된 게시글
     */
    fun createBoard(
        files: List<MultipartFile>, boardRequest: BoardRequest, authUsername: String
    ): BoardResponse {
        var fileUrls: MutableList<String>
        try {
            fileUrls = s3Service.uploadFiles(boardRequest.gameType.name, files)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

        val board = Board(
            id = UUID.randomUUID(),
            title = boardRequest.title,
            content = boardRequest.content,
            gameType = boardRequest.gameType,
            tags = boardRequest.tags,
            createdBy = authUsername,
            fileUrls = fileUrls,
        )

        boardRepository.save(board)

        return BoardResponse.of(board)
    }

    fun updateBoard(
        id: UUID, files: List<MultipartFile>, boardRequest: BoardRequest
    ): BoardResponse {
        val board = findById(id)
        var newFileUrls: MutableList<String> = mutableListOf()

        if (files.isNotEmpty()) {
            try {
                board.fileUrls?.takeIf { it.isNotEmpty() }?.let {
                    s3Service.deleteFiles(it)
                }
                newFileUrls = s3Service.uploadFiles(boardRequest.gameType.name, files)
            } catch (e: Exception) {
                throw IllegalStateException("파일 수정 중 오류가 발생했습니다.", e)
            }
        }

        board.updateBoard(boardRequest, newFileUrls)

        return BoardResponse()
    }

    @Transactional(readOnly = true)
    fun checkBoardPassword(id: UUID, password: String): Boolean {
        val board = findById(id)

        return board.password == passwordEncoder.encode(password)
    }

    fun deleteBoard(id: UUID, username: String) {
        val board = findById(id)

        if (board.createdBy != username) {
            throw AccessDeniedException("작성자만 게시글을 삭제할 수 있습니다.")
        }

        boardRepository.delete(board)
    }
}
