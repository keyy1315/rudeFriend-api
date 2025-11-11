package com.loltft.rudefriend.entity

import com.loltft.rudefriend.entity.enums.VoteType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "투표 타입 (UP, DOWN)")
    var voteType: VoteType? = null
)
