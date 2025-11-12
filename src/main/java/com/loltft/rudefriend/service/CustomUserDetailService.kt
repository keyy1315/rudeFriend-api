package com.loltft.rudefriend.service

import com.loltft.rudefriend.entity.AnonymousMember
import com.loltft.rudefriend.entity.enums.Role
import com.loltft.rudefriend.repository.member.AnonymousMemberRepository
import com.loltft.rudefriend.repository.member.MemberRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.function.Supplier

@Service
class CustomUserDetailService(
    private val memberRepository: MemberRepository,
    private val anonymousMemberRepository: AnonymousMemberRepository
) : UserDetailsService {
    /**
     * 회원 로그인 ID로 Member 객체를 조회하여 인증에 필요한 UserDetails 객체 반환
     *
     * @param memberId 회원 로그인 ID
     * @return UserDetails
     * @throws UsernameNotFoundException 회원 정보를 찾을 수 없을 경우
     */
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(memberId: String?): UserDetails {
        val member = memberRepository
            .findByMemberId(memberId)
            ?.orElseThrow(Supplier { UsernameNotFoundException(memberId) })

        return User.builder()
            .username(member?.memberId)
            .password(member?.password)
            .authorities(member?.role.toString())
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(!member?.status!!)
            .build()
    }

    fun loadUserByIpAddress(ipAddress: String): UserDetails {
        var anonymousMember = anonymousMemberRepository.findByIpAddress(ipAddress)

        if (anonymousMember == null) {
            val newAnonymousMember = AnonymousMember(
                id = UUID.randomUUID(),
                ipAddress = ipAddress,
            )
            anonymousMemberRepository.save(newAnonymousMember)
            anonymousMember = newAnonymousMember
        }

        return User.builder()
            .username(ipAddress)
            .password(anonymousMember.id.toString())
            .authorities(Role.ANONYMOUS.value)
            .build()
    }
}
