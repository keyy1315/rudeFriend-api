package com.loltft.rudefriend.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class BaseEntity {

  @CreatedDate
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Schema(description = "생성 일시")
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Schema(description = "수정 일시")
  @Column(insertable = false)
  private LocalDateTime updatedAt;
}
