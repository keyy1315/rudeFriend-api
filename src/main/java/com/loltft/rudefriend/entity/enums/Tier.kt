package com.loltft.rudefriend.entity.enums

import lombok.Getter

@Getter
enum class Tier(val value: Int) {
    IRON(9), SILVER(8), GOLD(7), BRONZE(6), PLATINUM(5), EMERALD(4), DIAMOND(3), MASTER(
        2
    ),
    GRANDMASTER(1), CHALLENGER(0)
}
