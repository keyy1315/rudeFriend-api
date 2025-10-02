package com.loltft.rudefriend.service;

import com.loltft.rudefriend.entity.Member;
import com.loltft.rudefriend.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

  private final MemberRepository memberRepository;

  /**
   * 회원 로그인 ID로 Member 객체를 조회하여 인증에 필요한 UserDetails 객체 반환
   *
   * @param memberId 회원 로그인 ID
   * @return UserDetails
   * @throws UsernameNotFoundException 회원 정보를 찾을 수 없을 경우
   */
  @Override
  public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
    Member member = memberRepository
        .findByMemberId(memberId)
        .orElseThrow(() -> new UsernameNotFoundException(memberId));

    return User.builder()
        .username(member.getMemberId())
        .password(member.getPassword())
        .authorities(String.valueOf(member.getRole()))
        .accountExpired(false)
        .accountLocked(false)
        .credentialsExpired(false)
        .disabled(!member.getStatus())
        .build();
  }
}
