package com.jjkay03.nationsevent.specific.ng6

import org.bukkit.Sound
import org.bukkit.entity.Player

enum class NG6_RolesEnum(
    val displayName : String,
    val description : String,
    val groupPerm : String,
    val sound: Sound,
    val team: NG6_TeamsEnum
) {

    CIVILIAN (
        "§aCIVILIAN \uD83D\uDDE1",
        "§7Description",
        "",
        Sound.ENTITY_VILLAGER_CELEBRATE,
        NG6_TeamsEnum.CIVILIANS
    ),

    MAFIA (
        "§cMAFIA \uD83D\uDDE1",
        "§7Description",
        "nationsevent.ng6.role.mafia",
        Sound.ENTITY_RAVAGER_CELEBRATE,
        NG6_TeamsEnum.MAFIA
    );

    companion object {
        // Function to get the team of a role
        fun getTeamRoles(team: NG6_TeamsEnum): List<NG6_RolesEnum> = NG6_RolesEnum.entries.filter { it.team == team }

        // Function to get role from perm (if non is found it will be VILLAGER)
        fun getRoleFromPerm(perm: String): NG6_RolesEnum = NG6_RolesEnum.entries.find { it.groupPerm == perm } ?: NG6_RolesEnum.CIVILIAN

        // Function to get player role from perm (if non is found it will be VILLAGER)
        fun getPlayerRole(player: Player): NG6_RolesEnum = NG6_RolesEnum.entries.find {
            if (it.groupPerm == "") return@find false // For villager role
            player.hasPermission(it.groupPerm)
        } ?: NG6_RolesEnum.CIVILIAN
    }
}