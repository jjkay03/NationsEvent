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
        "§7An ordinary citizen with no special abilities. Your goal is to uncover and eliminate the mafia while surviving their nightly attacks.",
        "",
        Sound.ENTITY_VILLAGER_CELEBRATE,
        NG6_TeamsEnum.CIVILIANS
    ),

    MAFIA (
        "§cMAFIA \uD83D\uDDE1",
        "§7A member of the mafia, working in secret to eliminate the civilians. Collaborate with your team to manipulate the game and eliminate anyone in your way. §cDO NOT DO ANYTHING BEFORE CONSULTING THE ADMINS!",
        "nationsevent.ng6.role.mafia",
        Sound.ENTITY_RAVAGER_CELEBRATE,
        NG6_TeamsEnum.MAFIA
    ),

    HITMAN (
        "§5HITMAN \uD83D\uDDE1",
        "§7A professional killer, your mission is to eliminate your assigned target (admins will let you know). Fail, and you pay the price with your life. You’re not aligned with civilians but can indirectly aid the mafia. §cDO NOT DO ANYTHING BEFORE CONSULTING THE ADMINS!",
        "nationsevent.ng6.role.hitman",
        Sound.ITEM_CROSSBOW_SHOOT,
        NG6_TeamsEnum.SOLITARIES
    ),

    DOCTOR (
        "§eDOCTOR ⚗",
        "§7A skilled healer who can save one player from death each night. Strategically protect key allies to help the civilians survive and defeat the mafia.",
        "nationsevent.ng6.role.doctor",
        Sound.BLOCK_BEACON_ACTIVATE,
        NG6_TeamsEnum.CIVILIANS
    ),

    DETECTIVE (
        "§bDETECTIVE \uD83D\uDD0E",
        "§7A sharp investigator who can discover the true identity of one player each night (admins will provide this information in private). Use your insights to guide the civilians.",
        "nationsevent.ng6.role.detective",
        Sound.BLOCK_END_PORTAL_FRAME_FILL,
        NG6_TeamsEnum.CIVILIANS
    ),

    AGENT (
        "§9AGENT ✎",
        "§7A covert operative who can send one anonymous message to any role each night. Use this power to sow confusion, deliver warnings, or share insights without revealing your identity.",
        "nationsevent.ng6.role.agent",
        Sound.ENTITY_WITCH_CELEBRATE,
        NG6_TeamsEnum.CIVILIANS
    ),

    MATCHMAKER (
        "§dMATCHMAKER \uD83C\uDFF9",
        "§7You choose two players to fall in love. If one dies, the other will follow. The lovers win together, regardless of their allegiance.",
        "nationsevent.ng6.role.matchmaker",
        Sound.ENTITY_ALLAY_ITEM_GIVEN,
        NG6_TeamsEnum.CIVILIANS
    ),

    LOVER(
        "§dLOVER ❤",
        "§7You are bound to another player. If your lover dies, you will also die of heartbreak. Stay alive together!",
        "nationsevent.ng6.role.lover",
        Sound.ENTITY_ALLAY_ITEM_GIVEN,
        NG6_TeamsEnum.NEUTRAL
    ),

    // NOT BEING USED!
    MAYOR(
    "§6MAYOR \uD83D\uDD14",
    "§7The elected leader of the town. You lead the town and have the final say if two players have the same amount of votes during a voting session.",
    "nationsevent.ng6.role.mayor",
    Sound.BLOCK_BELL_RESONATE,
    NG6_TeamsEnum.NEUTRAL
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