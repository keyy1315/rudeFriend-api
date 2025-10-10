package com.loltft.rudefriend.entity;

import java.sql.Types;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "anonymous_member")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AnonymousMember {

  @Id
  @JdbcTypeCode(Types.BINARY)
  @Column(columnDefinition = "BINARY(16)")
  @Schema(description = "익명 회원 PK")
  private UUID id;

  @Column
  @Schema(description = "익명 회원 IP 주소")
  private String ipAddress;
}
