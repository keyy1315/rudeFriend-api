package com.loltft.rudefriend.config;

import com.loltft.rudefriend.entity.Member;
import com.loltft.rudefriend.entity.enums.Role;
import com.loltft.rudefriend.repository.member.MemberRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

  private final PasswordEncoder passwordEncoder;
  private final MemberRepository memberRepository;

  @Override
  public void run(String... args) throws Exception {
    String memberId = "super";
    String password = "1234";
    Member member =
        memberRepository
            .findByMemberId(memberId)
            .orElseGet(
                () -> {
                  Member newMember =
                      Member.builder()
                          .id(UUID.randomUUID())
                          .memberId(memberId)
                          .name("super")
                          .password(passwordEncoder.encode(password))
                          .status(true)
                          .role(Role.SUPER)
                          .build();
                  try {
                    return memberRepository.save(newMember);
                  } catch (Exception e) {
                    log.error("Super Member already exists!", e);
                    return memberRepository.findByMemberId(memberId).orElse(newMember);
                  }
                });
    log.info("Super Member created Successfully : {}", member.getMemberId());
  }
}
