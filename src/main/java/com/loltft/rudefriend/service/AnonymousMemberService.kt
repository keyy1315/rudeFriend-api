package com.loltft.rudefriend.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.loltft.rudefriend.entity.AnonymousMember;
import com.loltft.rudefriend.repository.member.AnonymousMemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnonymousMemberService {

  private final AnonymousMemberRepository anonymousMemberRepository;

  @Transactional
  public void saveAnonymousMember(String ipAddress) {
    AnonymousMember anonymousMember = AnonymousMember.builder()
        .id(UUID.randomUUID())
        .ipAddress(ipAddress)
        .build();

    anonymousMemberRepository.save(anonymousMember);
  }
}
