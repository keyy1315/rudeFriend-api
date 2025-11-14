package com.loltft.rudefriend.entity

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.JdbcTypeCode
import java.sql.Types
import java.util.*

@Entity
@Table(
    name = "vote",
    uniqueConstraints = [UniqueConstraint(columnNames = ["board_id", "member_id"]), UniqueConstraint(
        columnNames = ["board_id", "ip_address"]
    )]
)
@Schema(description = "투표 엔티티, 한 게시글에 한 번의 투표 가능")
class Vote(
    @Id
    @JdbcTypeCode(Types.BINARY)
    @Column(columnDefinition = "BINARY(16)")
    @Schema(description = "Vote PK")
    var id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_id")
    @Schema(description = "투표 게시글")
    var board: Board? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @Schema(description = "로그인한 사용자")
    var member: Member? = null,

    @Column
    @Schema(description = "익명 사용자 IP 주소")
    var ipAddress: String? = null,

    @Column(name = "vote_item", length = 100)
    @Schema(description = "선택한 투표 항목")
    var voteItem: String? = null
)
