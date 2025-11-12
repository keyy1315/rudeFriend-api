package com.loltft.rudefriend.service

import com.loltft.rudefriend.dto.enums.DateOption
import com.loltft.rudefriend.dto.enums.FilterMode
import com.loltft.rudefriend.dto.enums.GameType
import com.loltft.rudefriend.dto.member.MemberRequest
import com.loltft.rudefriend.dto.member.MemberResponse
import com.loltft.rudefriend.dto.member.MemberResponse.Companion.from
import com.loltft.rudefriend.entity.Member
import com.loltft.rudefriend.entity.Member.Companion.fromRequest
import com.loltft.rudefriend.entity.enums.Role
import com.loltft.rudefriend.entity.enums.Tier
import com.loltft.rudefriend.entity.game.GameAccountInfo
import com.loltft.rudefriend.repository.member.MemberRepository
import com.loltft.rudefriend.utils.ConvertDateToDateTime
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils
import java.time.LocalDate
import java.util.*
import java.util.function.Supplier

@Service
@Transactional(readOnly = true)
class MemberService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder
) {
    var convertDateToDateTime: ConvertDateToDateTime = ConvertDateToDateTime()

    /**
     * DB에 저장 된 RefreshToken으로부터 회원 정보 조회
     *
     * @param refreshToken 해싱 된 refreshToken
     * @return 조회 한 회원 엔티티
     */
    fun findByRefreshToken(refreshToken: String?): Member? {
        return memberRepository.findByRefreshToken(refreshToken)
            ?.orElseThrow(Supplier { NoSuchElementException("존재하지 않는 회원 정보") })
    }

    /**
     * 회원 생성
     *
     *
     * 라이엇 게정 정보가 null이 아닐 경우에만 게임 정보 저장
     *
     * @param memberRequest 회원 생성 요청 객체
     * @return 생성 된 회원 응답 객체
     */
    @Transactional
    fun createMember(memberRequest: MemberRequest): MemberResponse? {
        var gameAccountInfo: GameAccountInfo? = null
        if (memberRequest.gameInfo != null) {
            gameAccountInfo = GameAccountInfo.fromRequest(memberRequest.gameInfo)
        }

        val member = fromRequest(
            memberRequest, passwordEncoder.encode(memberRequest.password), gameAccountInfo
        )

        memberRepository.save<Member?>(member)

        return from(member)
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
    fun updateMember(
        id: UUID, memberRequest: MemberRequest, userDetails: UserDetails
    ): MemberResponse? {
        val member = memberRepository.findById(id)
            .orElseThrow(Supplier { NoSuchElementException("존재하지 않는 회원 ID : $id") })
        val loginUsername = userDetails.username
        val role = userDetails.authorities.iterator().next().authority

        if (!StringUtils.hasText(loginUsername)) {
            throw AuthenticationCredentialsNotFoundException("로그인 정보가 없습니다.")
        }
        if ((member?.memberId != loginUsername) && (Role.ADMIN.name != role) && (Role.SUPER.name != role)) {
            throw AccessDeniedException("회원 정보 수정 권한이 없습니다.")
        }

        var gameInfo: GameAccountInfo? = null
        if (memberRequest.gameInfo != null) {
            gameInfo = GameAccountInfo.fromRequest(memberRequest.gameInfo)
        }
        val encodedPassword = passwordEncoder.encode(memberRequest.password)
        member?.updateMember(memberRequest, encodedPassword, gameInfo)

        return member?.let { MemberResponse.from(it) }
    }

    /**
     * @param id 수정하려는 회원 PK
     * @return 수정 된 회원 응답 객체
     */
    @Transactional
    fun updateStatusMember(id: UUID): MemberResponse? {
        val member = memberRepository.findById(id)
            .orElseThrow(Supplier { NoSuchElementException("존재하지 않는 회원 ID : $id") })
        member?.updateStatus()

        return member?.let { MemberResponse.from(it) }
    }

    /**
     * 회원 상세 조회
     *
     * @param id 조회 하려는 회원 PK
     * @return 회원 응답 객체
     */
    fun getMemberDetail(id: UUID): MemberResponse? {
        val member = memberRepository.findById(id)
            .orElseThrow(Supplier { NoSuchElementException("존재하지 않는 회원 ID : $id") })

        return member?.let { from(it) }
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
    fun getMemberList(
        search: String?,
        option: GameType?,
        tier: Tier?,
        filterMode: FilterMode?,
        status: Boolean?,
        role: Role?,
        dateFrom: LocalDate?,
        dateTo: LocalDate?,
        dateOption: DateOption?,
        hasGameInfo: Boolean?,
        pageNo: Int?
    ): MutableList<MemberResponse?>? {
        val dateTimeMap = convertDateToDateTime.convertMap(dateFrom, dateTo)

        return memberRepository.findAllByOption(
            search,
            option,
            tier,
            filterMode,
            status,
            role,
            dateTimeMap[FROM],
            dateTimeMap[TO],
            dateOption,
            hasGameInfo,
            pageNo
        )
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
    fun getMemberListCount(
        search: String?,
        option: GameType?,
        tier: Tier?,
        filterMode: FilterMode?,
        status: Boolean?,
        role: Role?,
        dateFrom: LocalDate?,
        dateTo: LocalDate?,
        dateOption: DateOption?,
        hasGameInfo: Boolean?
    ): Int? {
        val dateTimeMap = convertDateToDateTime.convertMap(dateFrom, dateTo)

        return memberRepository.countAllByOption(
            search,
            option,
            tier,
            filterMode,
            status,
            role,
            dateTimeMap[FROM],
            dateTimeMap[TO],
            dateOption,
            hasGameInfo
        )?.let {
            Math.toIntExact(
                it
            )
        }
    }

    fun findByMemberId(memberId: String?): Member? {
        return memberRepository.findByMemberId(memberId)?.orElse(null)
    }

    companion object {
        private const val FROM = "from"
        private const val TO = "to"
    }
}
