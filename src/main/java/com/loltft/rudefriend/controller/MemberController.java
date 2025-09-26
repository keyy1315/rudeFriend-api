package com.loltft.rudefriend.controller;

import com.loltft.rudefriend.dto.ApiCommonResponse;
import com.loltft.rudefriend.dto.enums.DateOption;
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
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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

@Tag(name = "회원 기능 API", description = "회원 CRUD 기능 API")
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/member")
public class MemberController {

  private final MemberService memberService;

  @Operation(summary = "회원 생성", description = "새로운 회원을 생성합니다.")
  @PostMapping
  public ResponseEntity<ApiCommonResponse<MemberResponse>> createMember(
      @RequestBody @Validated MemberRequest memberRequest) {
    MemberResponse result = memberService.createMember(memberRequest);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiCommonResponse.success("회원가입 성공", result));
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
    return ResponseEntity.ok(ApiCommonResponse.success("회원 수정 성공", result));
  }

  @Operation(summary = "회원 활성화/비활성화", description = "회원 사용 상태를 변경합니다.")
  @PatchMapping("/{id}")
  @PreAuthorize("isAuthenticated() and hasRole('SUPER') and hasRole('ADMIN')")
  public ResponseEntity<ApiCommonResponse<MemberResponse>> updateStatusMember(
      @PathVariable UUID id) {
    MemberResponse result = memberService.updateStatusMember(id);
    return ResponseEntity.ok(ApiCommonResponse.success("회원 수정 성공", result));
  }

  @Operation(summary = "회원 상세 조회", description = "회원 상세 정보를 조회합니다.")
  @GetMapping("/{id}")
  public ResponseEntity<ApiCommonResponse<MemberResponse>> getMemberDetail(@PathVariable UUID id) {
    MemberResponse result = memberService.getMemberDetail(id);
    return ResponseEntity.ok(ApiCommonResponse.success("회원 수정 성공", result));
  }

}
