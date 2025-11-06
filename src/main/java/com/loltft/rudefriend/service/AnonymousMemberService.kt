package com.loltft.rudefriend.service

import com.loltft.rudefriend.entity.AnonymousMember
import com.loltft.rudefriend.repository.member.AnonymousMemberRepository
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class AnonymousMemberService {
    private val anonymousMemberRepository: AnonymousMemberRepository? = null

    @Transactional
    fun saveAnonymousMember(ipAddress: String?) {
        val anonymousMember: AnonymousMember = AnonymousMember.builder()
            .id(UUID.randomUUID())
            .ipAddress(ipAddress)
            .build()

        anonymousMemberRepository!!.save<AnonymousMember?>(anonymousMember)
    }
}
