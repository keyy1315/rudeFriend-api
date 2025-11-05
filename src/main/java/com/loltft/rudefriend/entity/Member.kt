package com.loltft.rudefriend.entity

import com.fasterxml.jackson.annotation.JsonProperty
import com.loltft.rudefriend.dto.member.MemberRequest
import com.loltft.rudefriend.entity.enums.Role
import com.loltft.rudefriend.entity.game.GameAccountInfo
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.experimental.SuperBuilder
import org.hibernate.annotations.JdbcTypeCode
import org.springframework.data.annotation.LastModifiedBy
import java.sql.Types
import java.util.*

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
class Member : BaseEntity() {
    @Id
    @JdbcTypeCode(Types.BINARY)
    @Column(columnDefinition = "BINARY(16)")
    @Schema(description = "회원 PK")
    private var id: UUID? = null

    @Column(nullable = false, unique = true)
    @Schema(description = "회원 로그인 ID")
    private var memberId: String? = null

    @Column(nullable = false)
    @Schema(description = "회원 비밀번호", accessMode = Schema.AccessMode.WRITE_ONLY)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private var password: String? = null

    @Column(nullable = false)
    @Schema(description = "사용 상태")
    private var status: Boolean? = null

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "계정 권한 (USER, ADMIN, SUPER)")
    private var role: Role? = null

    @Column(unique = true)
    @Schema(description = "닉네임")
    private var name: String? = null

    @Column
    @Schema(description = "Refresh 토큰 정보")
    private var refreshToken: String? = null

    @Schema(description = "게임 계정 정보")
    @OneToOne(fetch = FetchType.LAZY)
    private var gameAccountInfo: GameAccountInfo? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @LastModifiedBy
    @Schema(description = "마지막 수정자 ID")
    private var updatedBy: Member? = null

    fun updateRefreshToken(hashedRefreshToken: String?) {
        this.refreshToken = hashedRefreshToken
    }

    fun updateMember(
        memberRequest: MemberRequest, encodedPassword: String?, gameAccountInfo: GameAccountInfo?
    ) {
        this.memberId = memberRequest.getMemberId()
        this.password = encodedPassword
        this.name = memberRequest.getName()
        this.gameAccountInfo = gameAccountInfo
    }

    fun updateStatus() {
        this.status = !this.status!!
    }

    companion object {
        @JvmStatic
        fun fromRequest(
            memberRequest: MemberRequest,
            encodedPassword: String?,
            gameAccountInfoData: GameAccountInfo?
        ): Member {
            return Member().apply {
                id = UUID.randomUUID()
                memberId = memberRequest.getMemberId()
                password = encodedPassword
                name = memberRequest.getName()
                status = true
                role = Role.USER
                gameAccountInfo = gameAccountInfoData
            }

        }
    }
}
