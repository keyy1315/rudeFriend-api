package com.loltft.rudefriend.repository.member;

import com.loltft.rudefriend.dto.enums.DateOption;
import com.loltft.rudefriend.dto.enums.GameSelectOption;
import com.loltft.rudefriend.dto.game.GameInfoResponse;
import com.loltft.rudefriend.dto.member.MemberResponse;
import com.loltft.rudefriend.entity.QMember;
import com.loltft.rudefriend.entity.enums.Role;
import com.loltft.rudefriend.entity.enums.Tier;
import com.loltft.rudefriend.entity.game.QGameAccountInfo;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

  private static final int PAGE_SIZE = 20;

  private final JPAQueryFactory queryFactory;

  public MemberRepositoryCustomImpl(EntityManager em) {
    this.queryFactory = new JPAQueryFactory(em);
  }

  private JPAQuery<MemberResponse> setSearchFilter(
      String search,
      GameSelectOption option,
      Tier tier,
      Boolean status,
      Role role,
      LocalDateTime localDateTime,
      LocalDateTime localDateTime1,
      DateOption dateOption) {
    QMember member = QMember.member;
    QGameAccountInfo gameInfo = QGameAccountInfo.gameAccountInfo;

    var query =
        queryFactory
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
                    Projections.fields(
                        GameInfoResponse.class,
                        gameInfo.id,
                        gameInfo.gameName,
                        gameInfo.tagLine,
                        gameInfo.iconUrl,
                        gameInfo.lolTier,
                        gameInfo.flexTier,
                        gameInfo.tftTier,
                        gameInfo.doubleUpTier)))
            .from(member)
            .leftJoin(gameInfo)
            .on(member.gameAccountInfo.id.eq(gameInfo.id));

    if (StringUtils.hasText(search)) {
      query.where(
          member
              .name
              .containsIgnoreCase(search)
              .or(
                  member
                      .memberId
                      .containsIgnoreCase(search)
                      .or(
                          member
                              .gameAccountInfo
                              .gameName
                              .append("#")
                              .append(member.gameAccountInfo.tagLine)
                              .containsIgnoreCase(search))));
    }

    return query;
  }

  @Override
  public List<MemberResponse> findAllByOption(
      String search,
      GameSelectOption option,
      Tier tier,
      Boolean status,
      Role role,
      LocalDateTime localDateTime,
      LocalDateTime localDateTime1,
      DateOption dateOption,
      Integer pageNo) {
    //    return setSearchFilter(
    //        search, option, tier, status, role, localDateTime, localDateTime1, dateOption)
    //        .orderBy(QMember.member.createdAt.desc())
    //        .offset((pageNo - 1L) * PAGE_SIZE)
    //        .limit(PAGE_SIZE)
    //        .fetch();

    QMember member = QMember.member;
    QGameAccountInfo gameInfo = QGameAccountInfo.gameAccountInfo;

    // transform()으로 중첩 DTO 생성
    Map<UUID, MemberResponse> resultMap =
        setSearchFilter(
                search, option, tier, status, role, localDateTime, localDateTime1, dateOption)
            .orderBy(member.createdAt.desc())
            .offset((pageNo - 1L) * PAGE_SIZE)
            .limit(PAGE_SIZE)
            .transform(
                GroupBy.groupBy(member.id)
                    .as(
                        Projections.constructor(
                            MemberResponse.class,
                            member.id,
                            member.memberId,
                            member.name,
                            member.status,
                            member.role,
                            member.createdAt,
                            member.updatedAt,
                            Projections.constructor(
                                GameInfoResponse.class,
                                gameInfo.id,
                                gameInfo.gameName,
                                gameInfo.tagLine,
                                gameInfo.iconUrl,
                                gameInfo.lolTier,
                                gameInfo.flexTier,
                                gameInfo.tftTier,
                                gameInfo.doubleUpTier))));

    // Map -> List 변환
    return new ArrayList<>(resultMap.values());
  }

  @Override
  public Long countAllByOption(
      String search,
      GameSelectOption option,
      Tier tier,
      Boolean status,
      Role role,
      LocalDateTime localDateTime,
      LocalDateTime localDateTime1,
      DateOption dateOption) {
    return setSearchFilter(
            search, option, tier, status, role, localDateTime, localDateTime1, dateOption)
        .select(QMember.member.id.count())
        .fetchOne();
  }
}
