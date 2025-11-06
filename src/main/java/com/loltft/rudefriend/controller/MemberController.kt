package com.loltft.rudefriend.controller

import com.loltft.rudefriend.dto.ApiCommonResponse
import com.loltft.rudefriend.dto.ApiCommonResponse.Companion.ok
import com.loltft.rudefriend.dto.enums.DateOption
import com.loltft.rudefriend.dto.enums.FilterMode
import com.loltft.rudefriend.dto.enums.GameType
import com.loltft.rudefriend.dto.member.MemberRequest
import com.loltft.rudefriend.dto.member.MemberResponse
import com.loltft.rudefriend.entity.enums.Role
import com.loltft.rudefriend.entity.enums.Tier
import com.loltft.rudefriend.service.MemberService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Min
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.*

@Tag(name = "회원 기능 API", description = "회원 CRUD 기능 API")
@RestController
@Validated
@RequestMapping("/api/member")
@PreAuthorize("isAuthenticated() and !hasRole('ANONYMOUS')")
class MemberController(private val memberService: MemberService) {


    @Operation(summary = "회원 생성", description = "새로운 회원을 생성합니다.")
    @PostMapping
    fun createMember(
        @RequestBody @Validated memberRequest: MemberRequest
    ): ResponseEntity<ApiCommonResponse<MemberResponse?>?> {
        val result = memberService.createMember(memberRequest)
        return ResponseEntity.status(HttpStatus.CREATED).body<ApiCommonResponse<MemberResponse?>?>(
            ok<MemberResponse?>("회원가입 성공", result)
        )
    }

    @Operation(summary = "회원 수정", description = "회원 정보를 수정합니다.")
    @PutMapping("/{id}")
    fun updateMember(
        @PathVariable id: UUID,
        @RequestBody @Validated memberRequest: MemberRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiCommonResponse<MemberResponse?>?> {
        val result = memberService.updateMember(id, memberRequest, userDetails)
        return ResponseEntity.ok<ApiCommonResponse<MemberResponse?>?>(
            ok<MemberResponse?>(
                "회원 수정 성공",
                result
            )
        )
    }

    @Operation(summary = "회원 활성화/비활성화", description = "회원 사용 상태를 변경합니다.")
    @PatchMapping("/{id}")
    fun updateStatusMember(
        @PathVariable id: UUID
    ): ResponseEntity<ApiCommonResponse<MemberResponse?>?> {
        val result = memberService.updateStatusMember(id)
        return ResponseEntity.ok<ApiCommonResponse<MemberResponse?>?>(
            ok<MemberResponse?>(
                "회원 수정 성공",
                result
            )
        )
    }

    @Operation(summary = "회원 상세 조회", description = "회원 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    fun getMemberDetail(@PathVariable id: UUID): ResponseEntity<ApiCommonResponse<MemberResponse?>?> {
        val result = memberService.getMemberDetail(id)
        return ResponseEntity.ok<ApiCommonResponse<MemberResponse?>?>(
            ok<MemberResponse?>(
                "회원 수정 성공",
                result
            )
        )
    }

    @Operation(summary = "회원 목록 조회", description = "검색 조건에 해당하는 회원 목록을 조회합니다. PAGE_SIZE = 20")
    @GetMapping
    fun getMemberList(
        @RequestParam(required = false) @Schema(description = "검색어 - 닉네임, 로그인 ID, 게임 이름, 익명 회원 IP 주소") search: String?,
        @RequestParam(required = false) @Schema(description = "롤(솔랭, 자랭)/롤체(솔랭, 깐부) 선택 옵션") option: GameType?,
        @RequestParam(required = false) @Schema(description = "티어 선택 옵션") tier: Tier?,
        @RequestParam(required = false) @Schema(description = "해당하는 티어의 이상/이하/같음 조회 선택 옵션") filterMode: FilterMode?,
        @RequestParam(required = false) @Schema(description = "계정 사용 상태") status: Boolean?,
        @RequestParam(required = false) @Schema(description = "회원 권한") role: Role?,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") @Schema(description = "등록일/수정일 시작일") dateFrom: LocalDate?,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") @Schema(description = "등록일/수정일 종료일") dateTo: LocalDate?,
        @RequestParam(required = false) @Schema(description = "등록일/수정일 선택 옵션 (create = 등록일 | update = 수정일)") dateOption: DateOption?,
        @RequestParam(required = false) @Schema(description = "계정 연동 여부") hasGameInfo: Boolean?,
        @RequestParam(defaultValue = "1") @Schema(description = "현재 페이지") pageNo: @Min(1) Int?
    ): ResponseEntity<ApiCommonResponse<MutableList<MemberResponse?>?>?> {
        val result = memberService.getMemberList(
            search,
            option,
            tier,
            filterMode,
            status,
            role,
            dateFrom,
            dateTo,
            dateOption,
            hasGameInfo,
            pageNo
        )
        return ResponseEntity.ok<ApiCommonResponse<MutableList<MemberResponse?>?>?>(
            ok<MutableList<MemberResponse?>?>(
                "회원 목록 조회 성공",
                result
            )
        )
    }

    @Operation(summary = "회원 전체 개수 조회", description = "검색 조건에 해당하는 회원의 전체 개수를 조회합니다.")
    @GetMapping("/total")
    fun getMemberListTotal(
        @RequestParam(required = false) @Schema(description = "검색어 - 닉네임, 로그인 ID, 게임 이름, 익명 회원 IP 주소") search: String?,
        @RequestParam(required = false) @Schema(description = "롤(솔랭, 자랭)/롤체(솔랭, 깐부) 선택 옵션") option: GameType?,
        @RequestParam(required = false) @Schema(description = "티어 선택 옵션") tier: Tier?,
        @RequestParam(required = false) @Schema(description = "해당하는 티어의 이상/이하/같음 조회 선택 옵션") filterMode: FilterMode?,
        @RequestParam(required = false) @Schema(description = "계정 사용 상태") status: Boolean?,
        @RequestParam(required = false) @Schema(description = "회원 권한") role: Role?,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") @Schema(description = "등록일/수정일 시작일") dateFrom: LocalDate?,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") @Schema(description = "등록일/수정일 종료일") dateTo: LocalDate?,
        @RequestParam(required = false) @Schema(description = "등록일/수정일 선택 옵션 (create = 등록일 | update = 수정일)") dateOption: DateOption?,
        @RequestParam(required = false) @Schema(description = "계정 연동 여부") hasGameInfo: Boolean?
    ): ResponseEntity<ApiCommonResponse<Int?>?> {
        val result = memberService.getMemberListCount(
            search,
            option,
            tier,
            filterMode,
            status,
            role,
            dateFrom,
            dateTo,
            dateOption,
            hasGameInfo
        )
        return ResponseEntity.ok<ApiCommonResponse<Int?>?>(ok<Int?>("회원 목록 조회 성공", result))
    }
}
