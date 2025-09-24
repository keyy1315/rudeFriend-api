package com.loltft.rudefriend.service;

import com.loltft.rudefriend.entity.Member;
import com.loltft.rudefriend.repository.MemberRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;

  @Transactional(readOnly = true)
  public Member findByRefreshToken(String refreshToken) {
    return memberRepository
        .findByRefreshToken(refreshToken)
        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원 정보"));
  }
}
