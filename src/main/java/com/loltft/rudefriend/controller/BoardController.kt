package com.loltft.rudefriend.controller

import com.loltft.rudefriend.dto.ApiCommonResponse
import com.loltft.rudefriend.dto.ApiCommonResponse.Companion.ok
import com.loltft.rudefriend.dto.board.BoardRequest
import com.loltft.rudefriend.dto.board.BoardResponse
import com.loltft.rudefriend.dto.enums.DateOption
import com.loltft.rudefriend.dto.enums.GameType
import com.loltft.rudefriend.entity.enums.Role
import com.loltft.rudefriend.service.BoardService
import com.loltft.rudefriend.utils.SwaggerBody
import com.loltft.rudefriend.utils.ValidationGroup
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Encoding
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Min
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate
import java.util.*

@Tag(name = "게시글 기능 API", description = "회원 CRUD 기능 API")
@RestController
@Validated
@RequestMapping("/api/board")
@PreAuthorize("isAuthenticated() or hasRole('ANONYMOUS')")
class BoardController(private val boardService: BoardService) {

    @Operation(summary = "게시글 생성", description = "게시글을 업로드합니다.")
    @SwaggerBody(
        description = "게시글을 업로드합니다.", content = [Content(
            encoding = [Encoding(
                name = "boardDto", contentType = MediaType.APPLICATION_JSON_VALUE
            )]
        )], required = true
    )
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
        /**
         * 게시글 생성 요청을 처리한다.
         *
         * @param files           업로드할 파일 목록 (선택값)
         * @param boardRequest    게시글 생성 DTO
         * @param authentication  인증 객체 (작성자 정보)
         * @return 생성된 게시글 응답 본문
         */
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

    @Operation(summary = "게시글 수정", description = "게시글을 수정합니다.")
    @SwaggerBody(
        description = "게시글을 수정합니다. 작성자만 수정 가능합니다.", content = [Content(
            encoding = [Encoding(
                name = "boardDto", contentType = MediaType.APPLICATION_JSON_VALUE
            )]
        )], required = true
    )
    @PutMapping(value = ["/{id}"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
        /**
         * 게시글 수정 요청을 처리한다.
         *
         * @param id             수정할 게시글 ID
         * @param files          새로 업로드할 파일 목록 (선택값)
         * @param boardRequest   수정된 게시글 DTO
         * @return 수정 결과 응답 본문
         */
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

    @Operation(summary = "게시글 비밀번호 검증", description = "익명 사용자의 게시글 비밀번호를 검증합니다.")
    @PostMapping("/{id}/password")
        /**
         * 익명 게시글 비밀번호 검증 요청을 처리한다.
         *
         * @param id         검증 대상 게시글 ID
         * @param password   사용자가 입력한 비밀번호
         * @return 비밀번호 일치 여부
         */
    fun checkBoardPassword(
        @PathVariable id: UUID, @RequestBody password: String
    ): ResponseEntity<ApiCommonResponse<Boolean?>?> {
        val result = boardService.checkBoardPassword(id, password);
        return ResponseEntity.ok(ok("비밀번호 검증 결과", result))
    }

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @DeleteMapping("/{id}")
        /**
         * 게시글 삭제 요청을 처리한다.
         *
         * @param id             삭제할 게시글 ID
         * @param authentication 인증 객체 (요청자 정보)
         * @return 삭제 성공 응답
         */
    fun deleteBoard(
        @PathVariable id: UUID,
        authentication: Authentication
    ): ResponseEntity<ApiCommonResponse<Boolean?>?> {
        boardService.deleteBoard(id, authentication.name)
        return ResponseEntity.ok(ok("게시글 삭제 성공"))
    }

    @Operation(summary = "게시글 상세 조회", description = "게시글을 상세 조회합니다.")
    @GetMapping("/{id}")
        /**
         * 단일 게시글 상세 조회를 처리한다.
         *
         * @param id 조회할 게시글 ID
         * @return 조회된 게시글 응답
         */
    fun getBoard(@PathVariable id: UUID): ResponseEntity<ApiCommonResponse<BoardResponse?>?> {
        val boardResponse = boardService.getBoard(id)
        return ResponseEntity.ok(ok("게시글 상세 조회 ID : $id", boardResponse))
    }

    @Operation(summary = "게시글 목록 조회", description = "게시글 목록을 조회합니다.")
    @GetMapping
        /**
         * 게시글 목록 조회를 처리한다.
         *
         * @param dateOption  조회 기준(등록/수정)
         * @param dateFrom    조회 시작일
         * @param dateTo      조회 종료일
         * @param tags        태그 필터
         * @param author      작성자 필터
         * @param search      제목/내용 검색어
         * @param gameType    게임 타입 필터
         * @param pageNo      조회 페이지 번호
         * @return 조회된 게시글 리스트와 전체 건수
         */
    fun getBoards(
        @RequestParam(required = false) dateOption: DateOption?,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") @Schema(description = "등록일/수정일 시작일") dateFrom: LocalDate?,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") @Schema(description = "등록일/수정일 종료일") dateTo: LocalDate?,
        @RequestParam(required = false) tags: List<String>?,
        @RequestParam(required = false) author: String?,
        @RequestParam(required = false) search: String?,
        @RequestParam(required = false) gameType: GameType?,
        @RequestParam(defaultValue = "1") @Schema(description = "현재 페이지") pageNo: @Min(1) Int
    ): ResponseEntity<ApiCommonResponse<List<BoardResponse?>?>?> {
        val (boards, total) = boardService.getBoards(
            dateFrom,
            dateTo,
            dateOption,
            search,
            gameType,
            pageNo,
            tags,
            author
        )

        return ResponseEntity.ok(ok("게시글 목록 조회 성공", boards, total))
    }
}
