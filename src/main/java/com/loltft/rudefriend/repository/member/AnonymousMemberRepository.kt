package com.loltft.rudefriend.repository.member

import com.loltft.rudefriend.entity.AnonymousMember
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AnonymousMemberRepository : JpaRepository<AnonymousMember?, UUID?>
