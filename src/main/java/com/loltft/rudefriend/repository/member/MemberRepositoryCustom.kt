package com.loltft.rudefriend.repository.member

import com.loltft.rudefriend.dto.enums.DateOption
import com.loltft.rudefriend.dto.enums.FilterMode
import com.loltft.rudefriend.dto.enums.GameType
import com.loltft.rudefriend.dto.member.MemberResponse
import com.loltft.rudefriend.entity.enums.Role
import com.loltft.rudefriend.entity.enums.Tier
import java.time.LocalDateTime

interface MemberRepositoryCustom {
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
     * @param pageNo      현재 페이지
     * @param hasGameInfo 게임 계정 연동 여부
     * @return 20개 회원 목록
     */
    fun findAllByOption(
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
    ): MutableList<MemberResponse?>?

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
    fun countAllByOption(
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
    ): Long?
}
