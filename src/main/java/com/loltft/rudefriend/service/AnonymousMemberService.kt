package com.loltft.rudefriend.service

import com.loltft.rudefriend.entity.AnonymousMember
import com.loltft.rudefriend.repository.member.AnonymousMemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional(readOnly = true)
class AnonymousMemberService(
    private val anonymousMemberRepository: AnonymousMemberRepository
) {
    @Transactional
    fun findOrCreateAnonymousMember(ipAddress: String?): AnonymousMember {
        return anonymousMemberRepository.findByIpAddress(ipAddress)
            ?: anonymousMemberRepository.save(
                AnonymousMember(
                    id = UUID.randomUUID(),
                    ipAddress = ipAddress,
                )
            )
    }
}
