package com.loltft.rudefriend.repository.member;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.loltft.rudefriend.dto.enums.DateOption;
import com.loltft.rudefriend.dto.enums.FilterMode;
import com.loltft.rudefriend.dto.enums.GameSelectOption;
import com.loltft.rudefriend.dto.member.MemberResponse;
import com.loltft.rudefriend.entity.QMember;
import com.loltft.rudefriend.entity.enums.Role;
import com.loltft.rudefriend.entity.enums.Tier;
import com.loltft.rudefriend.entity.game.QGameAccountInfo;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@Repository
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

  private static final int PAGE_SIZE = 20;

  private final JPAQueryFactory queryFactory;

  private final QMember member = QMember.member;
  private final QGameAccountInfo gameInfo = QGameAccountInfo.gameAccountInfo;

  public MemberRepositoryCustomImpl(EntityManager em) {
    this.queryFactory = new JPAQueryFactory(em);
  }

  /**
   * 검색 조건 쿼리 생성
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
   * @return JPAQuery
   */
  private JPAQuery<MemberResponse> setSearchFilter(
      String search,
      GameSelectOption option,
      Tier tier,
      FilterMode filterMode,
      Boolean status,
      Role role,
      LocalDateTime dateFrom,
      LocalDateTime dateTo,
      DateOption dateOption,
      Boolean hasGameInfo) {

    var query = queryFactory
        .select(
            Projections.fields(
                MemberResponse.class,
                member.id,
                member.memberId,
                member.name,
                member.status,
                member.role,
                member.createdAt,
                member.updatedAt,
                gameInfo))
        .from(member)
        .leftJoin(gameInfo)
        .on(member.gameAccountInfo.id.eq(gameInfo.id));

    // 검색어 필터링
    if (StringUtils.hasText(search)) {
      query.where(
          member.name
              .containsIgnoreCase(search)
              .or(
                  member.memberId
                      .containsIgnoreCase(search)
                      .or(
                          member.gameAccountInfo.gameName
                              .append("#")
                              .append(member.gameAccountInfo.tagLine)
                              .containsIgnoreCase(search))));

    }

    // 상태 필터링
    if (status != null) {
      query.where(member.status.eq(status));
    }

    // 권한 필터링
    if (role != null) {
      query.where(member.role.eq(role));
    }

    // 등록일/수정일 필터링
    if (dateOption != null) {
      if (dateOption == DateOption.CREATE) {
        // 등록일 필터링
        if (dateFrom != null && dateTo != null) {
          query.where(member.createdAt.between(dateFrom, dateTo));
        } else if (dateFrom != null) {
          query.where(member.createdAt.goe(dateFrom));
        } else if (dateTo != null) {
          query.where(member.createdAt.loe(dateTo));
        }
      } else if (dateOption == DateOption.UPDATE) {
        // 수정일 필터링
        if (dateFrom != null && dateTo != null) {
          query.where(member.updatedAt.between(dateFrom, dateTo));
        } else if (dateFrom != null) {
          query.where(member.updatedAt.goe(dateFrom));
        } else if (dateTo != null) {
          query.where(member.updatedAt.loe(dateTo));
        }
      }
    }

    if (hasGameInfo != null) {
      if (hasGameInfo) {
        query.where(member.gameAccountInfo.isNotNull());
      } else {
        query.where(member.gameAccountInfo.isNull());
      }
    }

    // 티어 필터링
    if (option != null) {
      EnumPath<Tier> tierPath = switch (option) {
        case LOL -> gameInfo.lolTier;
        case DOUBLE_UP -> gameInfo.doubleUpTier;
        case FLEX -> gameInfo.flexTier;
        case TFT -> gameInfo.tftTier;
      };

      Set<Tier> filteredTiers = filteringTier(tier, filterMode);
      if (filteredTiers != null) {
        query.where(tierPath.in(filteredTiers));
      }
    }

    return query;
  }

  /**
   * 티어 필터링 메소드
   *
   * @param tier       입력 받은 티어
   * @param filterMode 이상, 이하, 동일 조건 모드
   * @return Tier 컬렉션
   */
  private Set<Tier> filteringTier(Tier tier, FilterMode filterMode) {
    if (tier == null) {
      return null;
    }

    int baseValue = tier.getValue();

    // mode가 null일 경우 UNDER 옵션과 동일하게 처리
    return Arrays.stream(Tier.values())
        .filter(t -> switch (filterMode) {
          case EQUAL -> t.getValue() == baseValue;
          case OVER -> t.getValue() <= baseValue;
          case UNDER -> t.getValue() >= baseValue;
          case null -> t.getValue() >= baseValue;
        })
        .collect(Collectors.toSet());
  }

  @Override
  public List<MemberResponse> findAllByOption(
      String search,
      GameSelectOption option,
      Tier tier,
      FilterMode filterMode,
      Boolean status,
      Role role,
      LocalDateTime dateFrom,
      LocalDateTime dateTo,
      DateOption dateOption,
      Boolean hasGameInfo,
      Integer pageNo) {
    return setSearchFilter(search, option, tier, filterMode, status, role, dateFrom, dateTo,
        dateOption, hasGameInfo)
        .orderBy(member.createdAt.desc())
        .offset((pageNo - 1L) * PAGE_SIZE)
        .limit(PAGE_SIZE)
        .fetch();
  }

  @Override
  public Long countAllByOption(
      String search,
      GameSelectOption option,
      Tier tier,
      FilterMode filterMode,
      Boolean status,
      Role role,
      LocalDateTime dateFrom,
      LocalDateTime dateTo,
      DateOption dateOption,
      Boolean hasGameInfo) {
    return setSearchFilter(search, option, tier, filterMode, status, role, dateFrom, dateTo,
        dateOption, hasGameInfo)
        .select(member.id.count())
        .fetchOne();
  }
}
