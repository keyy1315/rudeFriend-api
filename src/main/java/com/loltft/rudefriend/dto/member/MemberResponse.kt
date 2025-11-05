package com.loltft.rudefriend.dto.member

import com.fasterxml.jackson.annotation.JsonInclude
import com.loltft.rudefriend.entity.Member
import com.loltft.rudefriend.entity.enums.Role
import com.loltft.rudefriend.entity.game.GameAccountInfo
import io.swagger.v3.oas.annotations.media.Schema
import lombok.*
import java.time.LocalDateTime
import java.util.*

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "회원 응답 DTO")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class MemberResponse(
    @Schema(description = "로그인 한 회원 PK")
    var id: UUID? = null,

    @Schema(description = "로그인 ID")
    var memberId: String? = null,

    @Schema(description = "로그인 한 회원 이름 nullable")
    var name: String? = null,

    @Schema(description = "회원 상태")
    var status: Boolean? = null,

    @Schema(description = "회원 권한")
    var role: Role? = null,

    @Schema(description = "계정 생성 일시")
    var createdAt: LocalDateTime? = null,

    @Schema(description = "계정 수정 일시")
    var updatedAt: LocalDateTime? = null,

    var gameInfo: GameAccountInfo? = null
) {

    companion object {
        @JvmStatic
        fun from(member: Member): MemberResponse? {
            return MemberResponse().apply {
                id = member.id
                memberId = member.memberId
                name = member.name
                gameInfo = member.gameAccountInfo?.let { GameAccountInfo.from(it) }
                status = member.status
                role = member.role
            }
        }
    }
}
