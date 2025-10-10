package com.loltft.rudefriend.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.loltft.rudefriend.dto.ApiCommonResponse;
import com.loltft.rudefriend.dto.enums.DateOption;
import com.loltft.rudefriend.dto.enums.FilterMode;
import com.loltft.rudefriend.dto.enums.GameSelectOption;
import com.loltft.rudefriend.dto.member.MemberRequest;
import com.loltft.rudefriend.dto.member.MemberResponse;
import com.loltft.rudefriend.entity.enums.Role;
import com.loltft.rudefriend.entity.enums.Tier;
import com.loltft.rudefriend.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@Tag(name = "회원 기능 API", description = "회원 CRUD 기능 API")
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/member")
@PreAuthorize("isAuthenticated() and !hasRole('ANONYMOUS')")
public class MemberController {

  private final MemberService memberService;

  @Operation(summary = "회원 생성", description = "새로운 회원을 생성합니다.")
  @PostMapping
  public ResponseEntity<ApiCommonResponse<MemberResponse>> createMember(
      @RequestBody @Validated MemberRequest memberRequest) {
    MemberResponse result = memberService.createMember(memberRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiCommonResponse.ok("회원가입 성공", result));
  }

  @Operation(summary = "회원 수정", description = "회원 정보를 수정합니다.")
  @PutMapping("/{id}")
  public ResponseEntity<ApiCommonResponse<MemberResponse>> updateMember(
      @PathVariable UUID id,
      @RequestBody @Validated MemberRequest memberRequest,
      @AuthenticationPrincipal UserDetails userDetails) {
    if (userDetails == null) {
      throw new AuthenticationCredentialsNotFoundException("로그인 정보가 없습니다.");
    }
    MemberResponse result = memberService.updateMember(id, memberRequest, userDetails);
    return ResponseEntity.ok(ApiCommonResponse.ok("회원 수정 성공", result));
  }

  @Operation(summary = "회원 활성화/비활성화", description = "회원 사용 상태를 변경합니다.")
  @PatchMapping("/{id}")
  public ResponseEntity<ApiCommonResponse<MemberResponse>> updateStatusMember(
      @PathVariable UUID id) {
    MemberResponse result = memberService.updateStatusMember(id);
    return ResponseEntity.ok(ApiCommonResponse.ok("회원 수정 성공", result));
  }

  @Operation(summary = "회원 상세 조회", description = "회원 상세 정보를 조회합니다.")
  @GetMapping("/{id}")
  public ResponseEntity<ApiCommonResponse<MemberResponse>> getMemberDetail(@PathVariable UUID id) {
    MemberResponse result = memberService.getMemberDetail(id);
    return ResponseEntity.ok(ApiCommonResponse.ok("회원 수정 성공", result));
  }

  @Operation(summary = "회원 목록 조회", description = "검색 조건에 해당하는 회원 목록을 조회합니다. PAGE_SIZE = 20")
  @GetMapping
  public ResponseEntity<ApiCommonResponse<List<MemberResponse>>> getMemberList(
      @RequestParam(required = false) @Schema(description = "검색어 - 닉네임, 로그인 ID, 게임 이름, 익명 회원 IP 주소") String search,
      @RequestParam(required = false) @Schema(
          description = "롤(솔랭, 자랭)/롤체(솔랭, 깐부) 선택 옵션") GameSelectOption option,
      @RequestParam(required = false) @Schema(
          description = "티어 선택 옵션") Tier tier,
      @RequestParam(required = false) @Schema(
          description = "해당하는 티어의 이상/이하/같음 조회 선택 옵션") FilterMode filterMode,
      @RequestParam(required = false) @Schema(description = "계정 사용 상태") Boolean status,
      @RequestParam(required = false) @Schema(
          description = "회원 권한") Role role,
      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") @Schema(description = "등록일/수정일 시작일") LocalDate dateFrom,
      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") @Schema(description = "등록일/수정일 종료일") LocalDate dateTo,
      @RequestParam(required = false) @Schema(
          description = "등록일/수정일 선택 옵션 (create = 등록일 | update = 수정일)") DateOption dateOption,
      @RequestParam(required = false) @Schema(description = "계정 연동 여부") Boolean hasGameInfo,
      @RequestParam(defaultValue = "1") @Schema(description = "현재 페이지") @Min(1) Integer pageNo) {
    List<MemberResponse> result = memberService.getMemberList(
        search, option, tier, filterMode, status, role, dateFrom, dateTo, dateOption, hasGameInfo,
        pageNo);
    return ResponseEntity.ok(ApiCommonResponse.ok("회원 목록 조회 성공", result));
  }

  @Operation(summary = "회원 전체 개수 조회", description = "검색 조건에 해당하는 회원의 전체 개수를 조회합니다.")
  @GetMapping("/total")
  public ResponseEntity<ApiCommonResponse<Integer>> getMemberListTotal(
      @RequestParam(required = false) @Schema(description = "검색어 - 닉네임, 로그인 ID, 게임 이름, 익명 회원 IP 주소") String search,
      @RequestParam(required = false) @Schema(
          description = "롤(솔랭, 자랭)/롤체(솔랭, 깐부) 선택 옵션") GameSelectOption option,
      @RequestParam(required = false) @Schema(
          description = "티어 선택 옵션") Tier tier,
      @RequestParam(required = false) @Schema(
          description = "해당하는 티어의 이상/이하/같음 조회 선택 옵션") FilterMode filterMode,
      @RequestParam(required = false) @Schema(description = "계정 사용 상태") Boolean status,
      @RequestParam(required = false) @Schema(
          description = "회원 권한") Role role,
      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") @Schema(description = "등록일/수정일 시작일") LocalDate dateFrom,
      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") @Schema(description = "등록일/수정일 종료일") LocalDate dateTo,
      @RequestParam(required = false) @Schema(
          description = "등록일/수정일 선택 옵션 (create = 등록일 | update = 수정일)") DateOption dateOption,
      @RequestParam(required = false) @Schema(description = "계정 연동 여부") Boolean hasGameInfo) {
    Integer result = memberService.getMemberListCount(
        search, option, tier, filterMode, status, role, dateFrom, dateTo, dateOption, hasGameInfo);
    return ResponseEntity.ok(ApiCommonResponse.ok("회원 목록 조회 성공", result));
  }
}
