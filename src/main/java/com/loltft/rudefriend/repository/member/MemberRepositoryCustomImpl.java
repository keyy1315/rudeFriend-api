package com.loltft.rudefriend.repository.member;

import com.loltft.rudefriend.dto.enums.DateOption;
import com.loltft.rudefriend.dto.enums.FilterMode;
import com.loltft.rudefriend.dto.enums.GameSelectOption;
import com.loltft.rudefriend.dto.member.MemberResponse;
import com.loltft.rudefriend.entity.QMember;
import com.loltft.rudefriend.entity.enums.Role;
import com.loltft.rudefriend.entity.enums.Tier;
import com.loltft.rudefriend.entity.game.QGameAccountInfo;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

  private static final int PAGE_SIZE = 20;

  private final JPAQueryFactory queryFactory;

  private final QMember member = QMember.member;
  private final QGameAccountInfo gameInfo = QGameAccountInfo.gameAccountInfo;

  public MemberRepositoryCustomImpl(EntityManager em) {
    this.queryFactory = new JPAQueryFactory(em);
  }

  private JPAQuery<MemberResponse> setSearchFilter(
      String search,
      GameSelectOption option,
      Tier tier,
      FilterMode filterMode,
      Boolean status,
      Role role,
      LocalDateTime dateFrom,
      LocalDateTime dateTo,
      DateOption dateOption) {

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

//    if(tier != null) {
//      BooleanExpression tierCondition;
//
//      switch (tier) {
//        case IRON -> tierCondition = gameInfo.doubleUpTier.in(
//            Arrays.stream(Tier.values())
//                .filter(t -> t.getValue() >= tier.getValue())
//        )
//      }
//    }

    return query;
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
      Integer pageNo) {
    return setSearchFilter(search, option, tier, filterMode, status, role, dateFrom, dateTo,
        dateOption)
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
      DateOption dateOption) {
    return setSearchFilter(search, option, tier, filterMode, status, role, dateFrom, dateTo,
        dateOption)
        .select(member.id.count())
        .fetchOne();
  }
}
