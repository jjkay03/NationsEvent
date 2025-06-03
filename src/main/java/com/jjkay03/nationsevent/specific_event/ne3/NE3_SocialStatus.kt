package com.jjkay03.nationsevent.specific_event.ne3

import org.bukkit.entity.Player

enum class NE3_SocialStatus (
    val permission: String,
    val icon: String,
    val priority: Int
) {

    LEADER (
        "nationsevent.ne3.social-status.leader",
        "\uD83D\uDC51",
        1
    ),

    COUNCIL (
        "nationsevent.ne3.social-status.council",
        "⚜",
        2
    ),

    TIER_1 (
        "nationsevent.ne3.social-status.tier-1",
        "✵",
        3
    ),

    TIER_2 (
        "nationsevent.ne3.social-status.tier-2",
        "✯",
        4
    ),

    TIER_3 (
        "nationsevent.ne3.social-status.tier-2",
        "⭐",
        5
    ),

    SOLDIER (
        "nationsevent.ne3.social-status.soldier",
        "🎖",
        6
    );

    companion object {
        const val PERM_RECEIVE_STATUS = "nationsevent.ne3.receive-status"

        val ALL_SOCIAL_STATUS_PERMS = NE3_SocialStatus.entries.map { it.permission }

        // Function that returns social status of a player if they have one
        fun getPlayerSocialStatusIcon(player: Player): String {
            if (!player.hasPermission(PERM_RECEIVE_STATUS)) return ""
            NE3_SocialStatus.entries.firstOrNull { player.hasPermission(it.permission) }?.let {
                return if (it.icon.isEmpty()) "" else "${it.icon} "
            }
            return ""
        }

        // Function that returns social status priority of a player if they have one
        fun getPlayerSocialStatusPriority(player: Player): Int {
            if (!player.hasPermission(PERM_RECEIVE_STATUS)) return 999
            return NE3_SocialStatus.entries.firstOrNull { player.hasPermission(it.permission) }?.priority ?: 999
        }

    }

}