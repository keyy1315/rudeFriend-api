package com.loltft.rudefriend.service

import com.loltft.rudefriend.dto.board.BoardRequest
import com.loltft.rudefriend.dto.board.BoardResponse
import com.loltft.rudefriend.entity.Board
import com.loltft.rudefriend.repository.board.BoardRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
@Transactional
class BoardService(
    private val boardRepository: BoardRepository,
    private val s3Service: S3Service
) {
    /**
     * 게시글 생성
     *
     * @param files        업로드 할 video/image []
     * @param boardRequest 게시글 생성 요청 DTO
     * @param authUsername Authentication 객체에 저장된 username
     * @return 생성 된 게시글
     */
    fun createBoard(
        files: List<MultipartFile>,
        boardRequest: BoardRequest,
        authUsername: String
    ): BoardResponse {
        val fileUrls: MutableList<String> = mutableListOf()
        try {
            for (file in files) {
                val uploadUrl = s3Service.uploadFile(boardRequest.gameType.name, file)
                fileUrls.add(uploadUrl)
            }
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
}
