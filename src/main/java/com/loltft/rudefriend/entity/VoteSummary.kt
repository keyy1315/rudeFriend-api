package com.loltft.rudefriend.entity

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import java.sql.Types
import java.util.UUID

/**
 * 게시글별 투표 항목의 누적 집계를 저장하는 엔티티.
 *
 * @property id      게시글과 항목으로 구성된 복합 키
 * @property voteCount 집계된 득표 수
 */
@Entity
@Table(name = "board_vote_summary")
@Schema(description = "게시글별 투표 항목 집계 테이블")
class VoteSummary(
    @EmbeddedId
    @Schema(description = "게시글 ID와 투표 항목 복합 키")
    var id: VoteSummaryId = VoteSummaryId(),

    @Column(name = "vote_count", nullable = false)
    @Schema(description = "현재 누적 득표 수")
    var voteCount: Long = 0L
)

/**
 * 게시글 ID와 투표 항목으로 구성된 VoteSummary 복합 키.
 *
 * @property boardId  집계를 소유한 게시글 ID
 * @property voteItem 집계를 산출한 투표 항목
 */
@Embeddable
@Schema(description = "투표 집계 복합 키")
data class VoteSummaryId(
    @JdbcTypeCode(Types.BINARY)
    @Column(name = "board_id", columnDefinition = "BINARY(16)")
    @Schema(description = "게시글 ID")
    var boardId: UUID = UUID(0, 0),

    @Column(name = "vote_item", length = 100)
    @Schema(description = "투표 항목")
    var voteItem: String = ""
)
