package com.loltft.rudefriend.entity

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "save_file")
@EntityListeners(AuditingEntityListener::class)
class SaveFile(
    @Id
    @Schema(description = "extension 포함 된 저장된 파일명")
    var fileUuid: String? = null,

    @Column
    @Schema(description = "원본 파일명")
    var originalFileName: String? = null,

    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "업로드 일시")
    var uploadDateTime: LocalDateTime? = null,

    @CreatedBy
    @Schema(description = "업로더")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    var uploader: Member? = null
)
