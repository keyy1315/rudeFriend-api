package com.loltft.rudefriend.controller

import com.loltft.rudefriend.dto.ApiCommonResponse
import com.loltft.rudefriend.dto.ApiCommonResponse.Companion.ok
import com.loltft.rudefriend.dto.board.BoardRequest
import com.loltft.rudefriend.dto.board.BoardResponse
import com.loltft.rudefriend.entity.enums.Role
import com.loltft.rudefriend.service.BoardService
import com.loltft.rudefriend.utils.SwaggerBody
import com.loltft.rudefriend.utils.ValidationGroup
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Encoding
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Tag(name = "게시글 기능 API", description = "회원 CRUD 기능 API")
@RestController
@Validated
@RequestMapping("/api/board")
@PreAuthorize("isAuthenticated() or hasRole('ANONYMOUS')")
class BoardController(private val boardService: BoardService) {

    @Operation(description = "게시글을 업로드합니다.")
    @SwaggerBody(
        description = "게시글을 업로드합니다.", content = [Content(
            encoding = [Encoding(
                name = "boardDto", contentType = MediaType.APPLICATION_JSON_VALUE
            )]
        )], required = true
    )
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createBoard(
        @Parameter(
            description = "게시글 이미지/동영상", required = false
        ) @RequestPart(value = "files", required = false) files: MutableList<MultipartFile>?,
        @Parameter(
            description = "게시글 생성 DTO",
            required = true,
            content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE)]
        ) @RequestPart(value = "boardDto") @Validated(ValidationGroup.CREATE::class) boardRequest: BoardRequest,
        authentication: Authentication
    ): ResponseEntity<ApiCommonResponse<BoardResponse?>?> {
        val isAnonymous =
            authentication.authorities?.any { it.authority == Role.ANONYMOUS.value } == true

        require(!(isAnonymous && boardRequest.password.isNullOrBlank())) {
            "익명 사용자는 비밀번호를 반드시 입력해야 합니다."
        }

        val createdBy = when (val principal = authentication.principal) {
            is UserDetails -> principal.username
            else -> null
        } ?: throw AccessDeniedException("작성자 정보를 찾을 수 없습니다.")

        val boardResponse = boardService.createBoard(
            files ?: emptyList(), boardRequest, createdBy
        )
        return ResponseEntity.ok(ok("게시글 작성 성공", boardResponse))
    }

    @Operation(description = "게시글을 수정합니다.")
    @SwaggerBody(
        description = "게시글을 수정합니다. 작성자만 수정 가능합니다.", content = [Content(
            encoding = [Encoding(
                name = "boardDto", contentType = MediaType.APPLICATION_JSON_VALUE
            )]
        )], required = true
    )
    @PutMapping(value = ["/{id}"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateBoard(
        @PathVariable id: UUID,
        @Parameter(
            description = "게시글 이미지/동영상", required = false
        ) @RequestPart(value = "files", required = false) files: MutableList<MultipartFile>?,
        @Parameter(
            description = "게시글 수정 DTO",
            required = true,
            content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE)]
        ) @RequestPart(value = "boardDto") @Validated(ValidationGroup.UPDATE::class) boardRequest: BoardRequest
    ): ResponseEntity<ApiCommonResponse<BoardResponse?>?> {
        val boardResponse = boardService.updateBoard(
            id, files ?: emptyList(), boardRequest
        )
        return ResponseEntity.ok(ok("게시글 수정 성공", boardResponse))
    }

    @Operation(description = "익명 사용자의 게시글 비밀번호를 검증합니다.")
    @PostMapping("/{id}/password")
    fun checkBoardPassword(
        @PathVariable id: UUID, @RequestBody password: String
    ): ResponseEntity<ApiCommonResponse<Boolean?>?> {
        val result = boardService.checkBoardPassword(id, password);
        return ResponseEntity.ok(ok("비밀번호 검증 결과", result))
    }

    @Operation(description = "게시글을 삭제합니다.")
    @DeleteMapping("/{id}")
    fun deleteBoard(
        @PathVariable id: UUID,
        authentication: Authentication
    ): ResponseEntity<ApiCommonResponse<Boolean?>?> {
        boardService.deleteBoard(id, authentication.name)
        return ResponseEntity.ok(ok("게시글 삭제 성공", true))
    }
}
