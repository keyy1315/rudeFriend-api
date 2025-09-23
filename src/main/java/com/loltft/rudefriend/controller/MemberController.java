package com.loltft.rudefriend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원 기능 API", description = "회원 기능 관련 API")
@RestController
@RequestMapping("/api/member")
public class MemberController {}
