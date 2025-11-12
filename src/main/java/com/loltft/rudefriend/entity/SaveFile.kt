package com.loltft.rudefriend.entity

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "save_file")
@EntityListeners(AuditingEntityListener::class)
class SaveFile(
    @Id
    @Schema(description = "extension 포함 된 저장된 파일명")
    var fileUuid: UUID,

    @Column
    @Schema(description = "원본 파일명")
    var originalFileName: String,

    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "업로드 일시")
    var uploadDateTime: LocalDateTime,

    @Column
    @Schema(description = "S3 버킷 내의 폴더명")
    var dirName: String,

    @Column
    @Schema(description = "S3에 업로드 된 파일의 전체 URL")
    var fullUrl: String,

    @Column
    @Schema(description = "저장한 게시글 PK")
    var boardId: UUID
)
