package com.loltft.rudefriend.repository.member;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loltft.rudefriend.entity.AnonymousMember;

public interface AnonymousMemberRepository extends JpaRepository<AnonymousMember, UUID> {

}
