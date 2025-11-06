package com.loltft.rudefriend.service

import com.loltft.rudefriend.dto.board.BoardRequest
import com.loltft.rudefriend.dto.board.BoardResponse
import com.loltft.rudefriend.entity.Board
import com.loltft.rudefriend.repository.board.BoardRepository
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
@RequiredArgsConstructor
@Transactional
class BoardService(
    private val boardRepository: BoardRepository? = null,
    private val s3Service: S3Service? = null
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
        files: MutableList<MultipartFile>, boardRequest: BoardRequest,
        authUsername: String?
    ): BoardResponse? {
        val fileUrls: MutableList<String?> = ArrayList<String?>()

        try {
            for (file in files) {
                val uploadUrl = s3Service!!.uploadFile(file)
                fileUrls.add(uploadUrl)
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

        val board: Board = Board(
            id = UUID.randomUUID(),
            title = boardRequest.title,
            content = boardRequest.content,
            gameType = boardRequest.gameType,
            tags = boardRequest.tags,
            createdBy = authUsername,
            fileUrls = fileUrls,
        );

        boardRepository!!.save<Board?>(board)

        return null
    }
}
