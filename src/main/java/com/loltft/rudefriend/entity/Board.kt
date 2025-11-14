package com.loltft.rudefriend.entity

import com.loltft.rudefriend.dto.board.BoardRequest
import com.loltft.rudefriend.dto.enums.GameType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.CascadeType
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.springframework.data.annotation.CreatedBy
import java.sql.Types
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "board")
class Board(
    @Id
    @JdbcTypeCode(Types.BINARY)
    @Column(columnDefinition = "BINARY(16)")
    @Schema(description = "Board PK")
    var id: UUID,

    @Column(length = 100)
    @Schema(description = "게시글 제목")
    var title: String? = null,

    @Column(length = 1000)
    @Schema(description = "게시글 내용")
    var content: String? = null,

    @Enumerated(EnumType.STRING)
    @Column
    @Schema(description = "게시글 게임 타입 (LOL/TFT)")
    var gameType: GameType,

    @ElementCollection(fetch = FetchType.EAGER)
    @Schema(description = "게시글 태그")
    var tags: MutableSet<String>? = null,

    @Column(name = "vote_enabled", nullable = false)
    @Schema(description = "투표 시스템 사용 여부")
    var voteEnabled: Boolean = false,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "board_vote_item", joinColumns = [JoinColumn(name = "board_id")])
    @Column(name = "vote_item", length = 100)
    @Schema(description = "투표 항목 목록")
    var voteItems: MutableList<String> = mutableListOf(),

    @OneToMany(mappedBy = "board", cascade = [CascadeType.ALL], orphanRemoval = true)
    var votes: MutableSet<Vote>? = null,

    @Schema(description = "등록자 ID/IP 주소")
    @CreatedBy
    var createdBy: String? = null,

    @Schema(description = "익명 사용자의 게시글 비밀번호")
    var password: String? = null,
) : BaseEntity() {
    /**
     * 요청 DTO 값으로 게시글을 수정한다.
     *
     * @param boardRequest 수정 요청 DTO
     */
    fun updateBoard(boardRequest: BoardRequest, voteItems: List<String>) {
        if (!boardRequest.password.isNullOrBlank()) {
            this.password = boardRequest.password
        }
        this.title = boardRequest.title
        this.content = boardRequest.content
        this.gameType = boardRequest.gameType
        this.tags = boardRequest.tags
        this.voteEnabled = boardRequest.voteEnabled
        this.voteItems.clear()
        if (boardRequest.voteEnabled) {
            this.voteItems.addAll(voteItems)
        }
        super.updatedAt = LocalDateTime.now()
    }
}
