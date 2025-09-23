package com.loltft.rudefriend.entity;

import com.loltft.rudefriend.entity.game.GameAccountInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

  @Column(nullable = false)
  @Schema(description = "사용 상태")
  private boolean status;

  @Column
  @Schema(description = "닉네임")
  private String name;

  @Schema(description = "게임 계정 정보")
  @OneToOne(fetch = FetchType.LAZY)
  private GameAccountInfo gameAccountInfo;
}
