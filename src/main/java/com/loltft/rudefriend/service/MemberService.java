package com.loltft.rudefriend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.loltft.rudefriend.dto.enums.DateOption;
import com.loltft.rudefriend.dto.enums.FilterMode;
import com.loltft.rudefriend.dto.enums.GameType;
import com.loltft.rudefriend.dto.member.MemberRequest;
import com.loltft.rudefriend.dto.member.MemberResponse;
import com.loltft.rudefriend.entity.Member;
import com.loltft.rudefriend.entity.enums.Role;
import com.loltft.rudefriend.entity.enums.Tier;
import com.loltft.rudefriend.entity.game.GameAccountInfo;
import com.loltft.rudefriend.repository.member.MemberRepository;
import com.loltft.rudefriend.utils.ConvertDateToDateTime;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  private static final String FROM = "from";
  private static final String TO = "to";

  ConvertDateToDateTime convertDateToDateTime = new ConvertDateToDateTime();

  /**
   * DB에 저장 된 RefreshToken으로부터 회원 정보 조회
   *
   * @param refreshToken 해싱 된 refreshToken
   * @return 조회 한 회원 엔티티
   */
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

    Member member = Member.fromRequest(
        memberRequest, passwordEncoder.encode(memberRequest.getPassword()), gameAccountInfo);

    memberRepository.save(member);

    return MemberResponse.from(member);
  }

  /**
   * 회원 정보 수정
   *
   * @param id            수정하려는 회원 PK
   * @param memberRequest 회원 수정 요청 객체
   * @param userDetails   로그인 한 사용자 인증 객체
   * @return 수정 된 회원 응답 객체
   * @throws AccessDeniedException                      회원 권한이 ADMIN, SUPER가 아닐 경우, 본인이 아닐 경우
   * @throws AuthenticationCredentialsNotFoundException 로그인 ID가 없을 경우
   */
  @Transactional
  public MemberResponse updateMember(
      UUID id, MemberRequest memberRequest, UserDetails userDetails) {
    Member member = memberRepository
        .findById(id)
        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원 ID : " + id));
    String loginUsername = userDetails.getUsername();
    String role = userDetails.getAuthorities().iterator().next().getAuthority();

    if (!StringUtils.hasText(loginUsername)) {
      throw new AuthenticationCredentialsNotFoundException("로그인 정보가 없습니다.");
    }
    if (!member.getMemberId().equals(loginUsername)
        && !Role.ADMIN.name().equals(role)
        && !Role.SUPER.name().equals(role)) {
      throw new AccessDeniedException("회원 정보 수정 권한이 없습니다.");
    }

    GameAccountInfo gameInfo = null;
    if (memberRequest.getGameInfo() != null) {
      gameInfo = GameAccountInfo.fromRequest(memberRequest.getGameInfo());
    }
    String encodedPassword = passwordEncoder.encode(memberRequest.getPassword());
    member.updateMember(memberRequest, encodedPassword, gameInfo);

    return MemberResponse.from(member);
  }

  /**
   * @param id 수정하려는 회원 PK
   * @return 수정 된 회원 응답 객체
   */
  @Transactional
  public MemberResponse updateStatusMember(UUID id) {
    Member member = memberRepository
        .findById(id)
        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원 ID : " + id));
    member.updateStatus();

    return MemberResponse.from(member);
  }

  /**
   * 회원 상세 조회
   *
   * @param id 조회 하려는 회원 PK
   * @return 회원 응답 객체
   */
  public MemberResponse getMemberDetail(UUID id) {
    Member member = memberRepository
        .findById(id)
        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원 ID : " + id));

    return MemberResponse.from(member);
  }

  /**
   * 검색 조건에 맞는 회원 목록 조회
   *
   * @param search      검색어 - 닉네임, 로그인 ID, 게임 이름
   * @param option      롤/롤체 선택 옵션 - LOL, TFT
   * @param tier        티어 선택
   * @param filterMode  티어의 같음/이상/이하 조회 옵션
   * @param status      회원 사용 상태 - true, false
   * @param role        권한 - USER, ADMIN, SUPER, ANONYMOUS
   * @param dateFrom    등록일/수정일 시작일
   * @param dateTo      등록일/수정일 종료일
   * @param dateOption  등록일/수정일 선택 옵션
   * @param hasGameInfo 게임 계정 연동 여부
   * @param pageNo      현재 페이지
   * @return 20개 회원 목록
   */
  public List<MemberResponse> getMemberList(
      String search,
      GameType option,
      Tier tier,
      FilterMode filterMode,
      Boolean status,
      Role role,
      LocalDate dateFrom,
      LocalDate dateTo,
      DateOption dateOption,
      Boolean hasGameInfo,
      Integer pageNo) {
    Map<String, LocalDateTime> dateTimeMap = convertDateToDateTime.convertMap(dateFrom, dateTo);

    return memberRepository.findAllByOption(
        search,
        option,
        tier,
        filterMode,
        status,
        role,
        dateTimeMap.get(FROM),
        dateTimeMap.get(TO),
        dateOption,
        hasGameInfo,
        pageNo);
  }

  /**
   * 검색 조건에 맞는 회원 전체 개수
   *
   * @param search      검색어 - 닉네임, 로그인 ID, 게임 이름
   * @param option      롤/롤체 선택 옵션 - LOL, TFT
   * @param tier        티어 선택
   * @param filterMode  티어의 같음/이상/이하 조회 옵션
   * @param status      회원 사용 상태 - true, false
   * @param role        권한 - USER, ADMIN, SUPER, ANONYMOUS
   * @param dateFrom    등록일/수정일 시작일
   * @param dateTo      등록일/수정일 종료일
   * @param dateOption  등록일/수정일 선택 옵션
   * @param hasGameInfo 게임 계정 연동 여부
   * @return 회원 개수
   */
  public Integer getMemberListCount(
      String search,
      GameType option,
      Tier tier,
      FilterMode filterMode,
      Boolean status,
      Role role,
      LocalDate dateFrom,
      LocalDate dateTo,
      DateOption dateOption,
      Boolean hasGameInfo) {
    Map<String, LocalDateTime> dateTimeMap = convertDateToDateTime.convertMap(dateFrom, dateTo);

    return Math.toIntExact(
        memberRepository.countAllByOption(
            search,
            option,
            tier,
            filterMode,
            status,
            role,
            dateTimeMap.get(FROM),
            dateTimeMap.get(TO),
            dateOption,
            hasGameInfo));
  }

  public Member findByMemberId(String memberId) {
    return memberRepository.findByMemberId(memberId).orElse(null);
  }
}
