package com.loltft.rudefriend.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "save_file")
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class SaveFile {

  @Id
  @Schema(description = "extension 포함 된 저장된 파일명")
  private String fileUuid;

  @Column
  @Schema(description = "원본 파일명")
  private String originalFileName;

  @CreatedDate
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Schema(description = "업로드 일시")
  private LocalDateTime uploadDateTime;

  @CreatedBy
  @Schema(description = "업로더")
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private Member uploader;
}
