package com.loltft.rudefriend.entity.game;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.sql.Types;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Table(name = "tft_match")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TFTMatch {
  @Id
  @JdbcTypeCode(Types.BINARY)
  @Column(columnDefinition = "BINARY(16)")
  @Schema(description = "TFTMatch PK")
  private UUID id;

  @JdbcTypeCode(Types.BINARY)
  @Column(columnDefinition = "BINARY(16)")
  @Schema(description = "GameAccountInfo PK")
  private UUID gameInfoId;

  @Column
  @Schema(description = "매치 ID", example = "KR_7821474749")
  private String matchId;
}
