package com.loltft.rudefriend.entity

import com.loltft.rudefriend.dto.enums.GameType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.experimental.SuperBuilder
import org.hibernate.annotations.JdbcTypeCode
import org.springframework.data.annotation.CreatedBy
import java.sql.Types
import java.util.*

@Entity
@Table(name = "board")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
class Board : BaseEntity() {
    @Id
    @JdbcTypeCode(Types.BINARY)
    @Column(columnDefinition = "BINARY(16)")
    @Schema(description = "Board PK")
    private var id: UUID? = null

    @Column(length = 100)
    @Schema(description = "게시글 제목")
    private var title: String? = null

    @Column(length = 1000)
    @Schema(description = "게시글 내용")
    private var content: String? = null

    @Enumerated(EnumType.STRING)
    @Column
    @Schema(description = "게시글 게임 타입 (LOL/TFT)")
    private var gameType: GameType? = null

    @ElementCollection(fetch = FetchType.EAGER)
    @Schema(description = "게시글 태그")
    private var tags: MutableSet<String?>? = null

    @Column(columnDefinition = "text[]")
    @Schema(description = "S3에 업로드 된 파일 URL 배열 (이미지/동영상")
    private var fileUrls: MutableList<String?>? = null

    @OneToMany(mappedBy = "board", cascade = [CascadeType.ALL], orphanRemoval = true)
    private var votes: MutableSet<Vote?>? = null

    @Schema(description = "등록자 ID/IP 주소")
    @CreatedBy
    private var createdBy: String? = null
}
