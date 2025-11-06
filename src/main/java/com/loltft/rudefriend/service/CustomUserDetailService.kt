package com.loltft.rudefriend.service

import com.loltft.rudefriend.repository.member.MemberRepository
import lombok.RequiredArgsConstructor
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.function.Supplier

@Service
@RequiredArgsConstructor
class CustomUserDetailService : UserDetailsService {
    private val memberRepository: MemberRepository? = null

    /**
     * 회원 로그인 ID로 Member 객체를 조회하여 인증에 필요한 UserDetails 객체 반환
     *
     * @param memberId 회원 로그인 ID
     * @return UserDetails
     * @throws UsernameNotFoundException 회원 정보를 찾을 수 없을 경우
     */
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(memberId: String?): UserDetails {
        val member = memberRepository!!
            .findByMemberId(memberId)
            .orElseThrow<UsernameNotFoundException?>(Supplier { UsernameNotFoundException(memberId) })

        return User.builder()
            .username(member.memberId)
            .password(member.password)
            .authorities(member.role.toString())
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(!member.status!!)
            .build()
    }
}
