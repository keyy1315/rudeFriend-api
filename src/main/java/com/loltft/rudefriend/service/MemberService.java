package com.loltft.rudefriend.service;

import com.loltft.rudefriend.dto.member.MemberRequest;
import com.loltft.rudefriend.dto.member.MemberResponse;
import com.loltft.rudefriend.entity.Member;
import com.loltft.rudefriend.entity.game.GameAccountInfo;
import com.loltft.rudefriend.repository.MemberRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  /**
   * DB에 저장 된 RefreshToken으로부터 회원 정보 조회
   *
   * @param refreshToken 해싱 된 refreshToken
   * @return 조회 한 회원 엔티티
   */
  @Transactional(readOnly = true)
  public Member findByRefreshToken(String refreshToken) {
    return memberRepository
        .findByRefreshToken(refreshToken)
        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원 정보"));
  }

  /**
   * 회원 생성
   *
   * <p>라이엇 게정 정보가 null이 아닐 경우에만 게임 정보 저장
   *
   * @param memberRequest 회원 생성 요청 객체
   * @return 생성 된 회원 응답 객체
   */
  @Transactional
  public MemberResponse createMember(MemberRequest memberRequest) {
    GameAccountInfo gameAccountInfo = null;
    if (memberRequest.getGameInfo() != null) {
      gameAccountInfo = GameAccountInfo.fromRequest(memberRequest.getGameInfo());
    }

    Member member = Member.fromRequest(memberRequest, "", gameAccountInfo);

    memberRepository.save(member);

    return MemberResponse.from(member);
  }
}
