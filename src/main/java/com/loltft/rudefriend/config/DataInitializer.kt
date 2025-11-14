package com.loltft.rudefriend.config

import com.loltft.rudefriend.entity.Member
import com.loltft.rudefriend.entity.enums.Role
import com.loltft.rudefriend.repository.member.MemberRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.util.*

@Component
class DataInitializer(
    private val passwordEncoder: PasswordEncoder,
    private val memberRepository: MemberRepository
) : CommandLineRunner {
    private val log = LoggerFactory.getLogger(javaClass)

    @Throws(Exception::class)
    override fun run(vararg args: String?) {
        val memberId = "super"
        val password = "1234"

        val member = memberRepository.findByMemberId(memberId)?.orElseGet {
            val newMember = Member(
                id = UUID.randomUUID(),
                memberId = memberId,
                name = "super",
                password = passwordEncoder.encode(password),
                status = true,
                role = Role.SUPER
            )

            runCatching {
                memberRepository.save(newMember)
            }.onFailure { e ->
                log.error("Super Member already exists!", e)
            }.getOrElse {
                memberRepository.findByMemberId(memberId)?.orElse(newMember)
            }
        }

        member?.let { log.info("Super Member created successfully: {}", it.memberId) }
    }
}
