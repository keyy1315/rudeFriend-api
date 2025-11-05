package com.loltft.rudefriend.entity

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.experimental.SuperBuilder
import org.hibernate.annotations.JdbcTypeCode
import java.sql.Types
import java.util.*

@Entity
@Table(name = "anonymous_member")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
class AnonymousMember {
    @Id
    @JdbcTypeCode(Types.BINARY)
    @Column(columnDefinition = "BINARY(16)")
    @Schema(description = "익명 회원 PK")
    private var id: UUID? = null

    @Column
    @Schema(description = "익명 회원 IP 주소")
    private var ipAddress: String? = null
}
