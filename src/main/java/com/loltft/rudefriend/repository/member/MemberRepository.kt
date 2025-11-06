package com.loltft.rudefriend.repository.member;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loltft.rudefriend.entity.Member;

public interface MemberRepository extends JpaRepository<Member, UUID>, MemberRepositoryCustom {

  Optional<Member> findByMemberId(String memberId);

  Optional<Member> findByRefreshToken(String refreshToken);
}
