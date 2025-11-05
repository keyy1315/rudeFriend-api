package com.loltft.rudefriend.entity;

import java.sql.Types;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.springframework.data.annotation.CreatedBy;

import com.loltft.rudefriend.dto.enums.GameType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "board")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Board extends BaseEntity {

  @Id
  @JdbcTypeCode(Types.BINARY)
  @Column(columnDefinition = "BINARY(16)")
  @Schema(description = "Board PK")
  private UUID id;

  @Column(length = 100)
  @Schema(description = "게시글 제목")
  private String title;

  @Column(length = 1000)
  @Schema(description = "게시글 내용")
  private String content;

  @Enumerated(EnumType.STRING)
  @Column
  @Schema(description = "게시글 게임 타입 (LOL/TFT)")
  private GameType gameType;

  @ElementCollection(fetch = FetchType.EAGER)
  @Schema(description = "게시글 태그")
  private Set<String> tags;

  @Column(columnDefinition = "text[]")
  @Schema(description = "S3에 업로드 된 파일 URL 배열 (이미지/동영상")
  private List<String> fileUrls;

  @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Vote> votes;

  @Schema(description = "등록자 ID/IP 주소")
  @CreatedBy
  private String createdBy;
}
