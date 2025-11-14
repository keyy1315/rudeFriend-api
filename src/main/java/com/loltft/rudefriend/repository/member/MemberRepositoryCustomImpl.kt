package com.loltft.rudefriend.repository.member

import com.loltft.rudefriend.dto.enums.DateOption
import com.loltft.rudefriend.dto.enums.FilterMode
import com.loltft.rudefriend.dto.enums.GameType
import com.loltft.rudefriend.dto.member.MemberResponse
import com.loltft.rudefriend.entity.QMember
import com.loltft.rudefriend.entity.enums.Role
import com.loltft.rudefriend.entity.enums.Tier
import com.loltft.rudefriend.entity.game.GameAccountInfo
import com.loltft.rudefriend.entity.game.QGameAccountInfo
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import org.springframework.util.StringUtils
import java.time.LocalDateTime
import java.util.*
import java.util.stream.Collectors

@Repository
class MemberRepositoryCustomImpl(em: EntityManager) : MemberRepositoryCustom {
    private val queryFactory: JPAQueryFactory = JPAQueryFactory(em)

    private val member: QMember = QMember.member
    private val gameInfo: QGameAccountInfo = QGameAccountInfo.gameAccountInfo

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
    private fun setSearchFilter(
        search: String?,
        option: GameType?,
        tier: Tier?,
        filterMode: FilterMode?,
        status: Boolean?,
        role: Role?,
        dateFrom: LocalDateTime?,
        dateTo: LocalDateTime?,
        dateOption: DateOption?,
        hasGameInfo: Boolean?
    ): JPAQuery<MemberResponse?> {
        val query = queryFactory
            .select(
                Projections.fields(
                    MemberResponse::class.java,
                    member.id,
                    member.memberId,
                    member.name,
                    member.status,
                    member.role,
                    member.createdAt,
                    member.updatedAt,
                    gameInfo
                )
            )
            .from(member)
            .leftJoin<GameAccountInfo?>(gameInfo)
            .on(member.gameAccountInfo.id.eq(gameInfo.id))

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
                                    .containsIgnoreCase(search)
                            )
                    )
            )
        }

        // 상태 필터링
        if (status != null) {
            query.where(member.status.eq(status))
        }

        // 권한 필터링
        if (role != null) {
            query.where(member.role.eq(role))
        }

        // 등록일/수정일 필터링
        if (dateOption != null) {
            if (dateOption == DateOption.CREATE) {
                // 등록일 필터링
                if (dateFrom != null && dateTo != null) {
                    query.where(member.createdAt.between(dateFrom, dateTo))
                } else if (dateFrom != null) {
                    query.where(member.createdAt.goe(dateFrom))
                } else if (dateTo != null) {
                    query.where(member.createdAt.loe(dateTo))
                }
            } else if (dateOption == DateOption.UPDATE) {
                // 수정일 필터링
                if (dateFrom != null && dateTo != null) {
                    query.where(member.updatedAt.between(dateFrom, dateTo))
                } else if (dateFrom != null) {
                    query.where(member.updatedAt.goe(dateFrom))
                } else if (dateTo != null) {
                    query.where(member.updatedAt.loe(dateTo))
                }
            }
        }

        if (hasGameInfo != null) {
            if (hasGameInfo) {
                query.where(member.gameAccountInfo.isNotNull())
            } else {
                query.where(member.gameAccountInfo.isNull())
            }
        }

        // 티어 필터링
        if (option != null) {
            val tierPath = when (option) {
                GameType.LOL -> gameInfo.lolTier
                GameType.DOUBLE_UP -> gameInfo.doubleUpTier
                GameType.FLEX -> gameInfo.flexTier
                GameType.TFT -> gameInfo.tftTier
            }

            val filteredTiers = tier?.let { filteringTier(it, filterMode) }
            if (filteredTiers != null) {
                query.where(tierPath.`in`(filteredTiers))
            }
        }

        return query
    }

    /**
     * 티어 필터링 메소드
     *
     * @param tier       입력 받은 티어
     * @param filterMode 이상, 이하, 동일 조건 모드
     * @return Tier 컬렉션
     */
    private fun filteringTier(tier: Tier, filterMode: FilterMode?): MutableSet<Tier?>? {

        val baseValue: Int = tier.value

        // mode가 null일 경우 UNDER 옵션과 동일하게 처리
        return Arrays.stream<Tier>(Tier.entries.toTypedArray())
            .filter { t: Tier ->
                when (filterMode) {
                    FilterMode.EQUAL -> t.value == baseValue
                    FilterMode.OVER -> t.value <= baseValue
                    FilterMode.UNDER -> t.value >= baseValue
                    null -> t.value >= baseValue
                }
            }
            .collect(Collectors.toSet())
    }

    override fun findAllByOption(
        search: String?,
        option: GameType?,
        tier: Tier?,
        filterMode: FilterMode?,
        status: Boolean?,
        role: Role?,
        dateFrom: LocalDateTime?,
        dateTo: LocalDateTime?,
        dateOption: DateOption?,
        hasGameInfo: Boolean?,
        pageNo: Int?
    ): MutableList<MemberResponse?>? {
        return pageNo?.let {
            setSearchFilter(
                search, option, tier, filterMode, status, role, dateFrom, dateTo,
                dateOption, hasGameInfo
            )
                .orderBy(member.createdAt.desc())
                .offset((it - 1L) * PAGE_SIZE)
        }
            ?.limit(PAGE_SIZE.toLong())
            ?.fetch()
    }

    override fun countAllByOption(
        search: String?,
        option: GameType?,
        tier: Tier?,
        filterMode: FilterMode?,
        status: Boolean?,
        role: Role?,
        dateFrom: LocalDateTime?,
        dateTo: LocalDateTime?,
        dateOption: DateOption?,
        hasGameInfo: Boolean?
    ): Long? {
        return setSearchFilter(
            search, option, tier, filterMode, status, role, dateFrom, dateTo,
            dateOption, hasGameInfo
        )
            .select(member.id.count())
            .fetchOne()
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}
