package com.loltft.rudefriend.controller;

import com.loltft.rudefriend.dto.ApiCommonResponse;
import com.loltft.rudefriend.dto.member.MemberRequest;
import com.loltft.rudefriend.dto.member.MemberResponse;
import com.loltft.rudefriend.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
