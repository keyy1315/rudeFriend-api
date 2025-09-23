package com.loltft.rudefriend.entity;


import com.loltft.rudefriend.entity.enums.VoteType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.sql.Types;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Table(name = "vote",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"board_id", "member_id"}),
        @UniqueConstraint(columnNames = {"board_id", "ip_address"})
    })
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Schema(description = "투표 엔티티, 한 게시글에 한 번의 투표 가능")
public class Vote {

  @Id
  @JdbcTypeCode(Types.BINARY)
  @Column(columnDefinition = "BINARY(16)")
  @Schema(description = "Vote PK")
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "board_id")
  @Schema(description = "투표 게시글")
  private Board board;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  @Schema(description = "로그인한 사용자")
  private Member member;

  @Column
  @Schema(description = "익명 사용자 IP 주소")
  private String ipAddress;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Schema(description = "투표 타입 (UP, DOWN)")
  private VoteType voteType;
}
