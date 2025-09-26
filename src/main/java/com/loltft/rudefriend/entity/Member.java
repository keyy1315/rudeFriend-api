package com.loltft.rudefriend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.loltft.rudefriend.dto.member.MemberRequest;
import com.loltft.rudefriend.entity.enums.Role;
import com.loltft.rudefriend.entity.game.GameAccountInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.sql.Types;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Member extends BaseEntity {

  @Id
  @JdbcTypeCode(Types.BINARY)
  @Column(columnDefinition = "BINARY(16)")
  @Schema(description = "회원 PK")
  private UUID id;

  @Column(nullable = false, unique = true)
  @Schema(description = "회원 로그인 ID")
  private String memberId;

  @Column(nullable = false)
  @Schema(description = "회원 비밀번호", accessMode = AccessMode.WRITE_ONLY)
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;

  @Column(nullable = false)
  @Schema(description = "사용 상태")
  private Boolean status;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Schema(description = "계정 권한 (USER, ADMIN, SUPER)")
  private Role role;

  @Column(unique = true)
  @Schema(description = "닉네임")
  private String name;

  @Column
  @Schema(description = "Refresh 토큰 정보")
  private String refreshToken;

  @Schema(description = "게임 계정 정보")
  @OneToOne(fetch = FetchType.LAZY)
  private GameAccountInfo gameAccountInfo;

  public static Member fromRequest(
      MemberRequest memberRequest, String encodedPassword, GameAccountInfo gameAccountInfo) {
    return Member.builder()
        .id(UUID.randomUUID())
        .memberId(memberRequest.getMemberId())
        .password(encodedPassword)
        .name(memberRequest.getName())
        .status(true)
        .role(Role.USER)
        .gameAccountInfo(gameAccountInfo)
        .build();
  }

  public void updateRefreshToken(String hashedRefreshToken) {
    this.refreshToken = hashedRefreshToken;
  }

  public void updateMember(
      MemberRequest memberRequest, String encodedPassword, GameAccountInfo gameAccountInfo) {
    this.memberId = memberRequest.getMemberId();
    this.password = encodedPassword;
    this.name = memberRequest.getName();
    this.gameAccountInfo = gameAccountInfo;
  }

  public void updateStatus() {
    this.status = !this.status;
  }
}
