package com.loltft.rudefriend.controller

import com.loltft.rudefriend.dto.ApiCommonResponse
import com.loltft.rudefriend.dto.ApiCommonResponse.Companion.ok
import com.loltft.rudefriend.dto.board.BoardRequest
import com.loltft.rudefriend.dto.board.BoardResponse
import com.loltft.rudefriend.service.BoardService
import com.loltft.rudefriend.utils.ValidationGroup
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.tags.Tag
import lombok.RequiredArgsConstructor
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@Tag(name = "게시글 기능 API", description = "회원 CRUD 기능 API")
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/board")
@PreAuthorize("isAuthenticated()")
class BoardController(private val boardService: BoardService) {


    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createBoard(
        @Parameter(
            description = "게시글 이미지/동영상",
            required = false
        ) @RequestPart(value = "files") files: MutableList<MultipartFile>,
        @Parameter(
            description = "게시글 생성 DTO",
            required = true,
            content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE)]
        ) @RequestPart(value = "boardDto") @Validated(ValidationGroup.CREATE::class) boardRequest: BoardRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiCommonResponse<BoardResponse?>?> {
        if (userDetails.username != null) {
            val boardResponse = boardService.createBoard(
                files, boardRequest,
                userDetails.username
            )
            return ResponseEntity.ok<ApiCommonResponse<BoardResponse?>?>(
                ok<BoardResponse?>(
                    "게시글 작성 성공",
                    boardResponse
                )
            )
        } else {
            throw AccessDeniedException("작성자 정보를 찾을 수 없습니다.")
        }
    }
}
