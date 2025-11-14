package com.loltft.rudefriend.repository.member

import com.loltft.rudefriend.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface MemberRepository : JpaRepository<Member?, UUID?>, MemberRepositoryCustom {
    fun findByMemberId(memberId: String?): Optional<Member?>

    fun findByRefreshToken(refreshToken: String?): Optional<Member?>
}
