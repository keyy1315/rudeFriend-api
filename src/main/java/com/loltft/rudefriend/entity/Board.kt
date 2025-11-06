package com.loltft.rudefriend.entity

import com.loltft.rudefriend.dto.enums.GameType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.springframework.data.annotation.CreatedBy
import java.sql.Types
import java.util.*

@Entity
@Table(name = "board")
class Board(
    @Id
    @JdbcTypeCode(Types.BINARY)
    @Column(columnDefinition = "BINARY(16)")
    @Schema(description = "Board PK")
    var id: UUID? = null,

    @Column(length = 100)
    @Schema(description = "게시글 제목")
    var title: String? = null,

    @Column(length = 1000)
    @Schema(description = "게시글 내용")
    var content: String? = null,

    @Enumerated(EnumType.STRING)
    @Column
    @Schema(description = "게시글 게임 타입 (LOL/TFT)")
    var gameType: GameType? = null,

    @ElementCollection(fetch = FetchType.EAGER)
    @Schema(description = "게시글 태그")
    var tags: MutableSet<String?>? = null,

    @Column(columnDefinition = "text[]")
    @Schema(description = "S3에 업로드 된 파일 URL 배열 (이미지/동영상")
    var fileUrls: MutableList<String?>? = null,

    @OneToMany(mappedBy = "board", cascade = [CascadeType.ALL], orphanRemoval = true)
    var votes: MutableSet<Vote?>? = null,

    @Schema(description = "등록자 ID/IP 주소")
    @CreatedBy
    var createdBy: String? = null
) : BaseEntity()
