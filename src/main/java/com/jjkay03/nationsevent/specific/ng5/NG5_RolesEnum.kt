package com.jjkay03.nationsevent.specific.ng5

import org.bukkit.Sound
import org.bukkit.entity.Player

enum class NG5_RolesEnum(
    val displayName : String,
    val description : String,
    val groupPerm : String,
    val sound: Sound,
    val team: NG5_TeamsEnum
) {

    VILLAGER(
        "§aVILLAGER ⛏",
        "§7An ordinary villager with no special powers. Your goal is to survive and find the werewolves.",
        "",
        Sound.ENTITY_VILLAGER_CELEBRATE,
        NG5_TeamsEnum.VILLAGERS
    ),

    WEREWOLF(
        "§cWEREWOLF 🐺",
        "§7A terrifying predator that hunts the villagers at night. Work with your fellow werewolves to eliminate the villagers and KILL EVERYONE. §cDO NOT DO ANYTHING BEFORE CONSULTING THE ADMINS!",
        "nationsevent.ng5.role.werewolf",
        Sound.ENTITY_WOLF_HOWL,
        NG5_TeamsEnum.WOLVES_KILLERS
    ),

    // NOT BEING USED FOR NG5
    LONEWOLF(
        "§4LONEWOLF 🐺",
        "§7A lone predator that hunts the villagers at night. Pretend to work with your fellow werewolves to eliminate the villagers but your real goal is to KILL EVERYONE. You win ALONE, you need to kill both villagers and werewolves 💀 §cDO NOT DO ANYTHING BEFORE CONSULTING THE ADMINS!",
        "nationsevent.ng5.role.lonewolf",
        Sound.ENTITY_WOLF_HOWL,
        NG5_TeamsEnum.WOLVES_KILLERS
    ),

    ALPHA_WOLF(
        "§4ALPHA WOLF 🐺",
        "§7Lead the wolves in killing the villagers. After a wolf dies, you can turn one player into a wolf (one-time use). Work with your pack to eliminate the villagers.",
        "nationsevent.ng5.role.alphawolf",
        Sound.ENTITY_WOLF_HOWL,
        NG5_TeamsEnum.WOLVES_KILLERS
    ),

    WOLF_CUB(
        "§cWOLF CUB 🐺",
        "§7The baby wolf, unable to kill. If you die, wolves become enraged the next night. Survive and support your pack.",
        "nationsevent.ng5.role.wolfcub",
        Sound.ENTITY_FOX_SCREECH,
        NG5_TeamsEnum.WOLVES
    ),

    TRAITOR(
        "§cTRAITOR \uD83C\uDF83",
        "§7A secret ally to the wolves who can't kill. If you survive with the wolves, you win. The Seer cannot detect your true allegiance.",
        "nationsevent.ng5.role.traitor",
        Sound.ENTITY_PILLAGER_CELEBRATE,
        NG5_TeamsEnum.NEUTRAL
    ),

    BEAR(
        "§5BEAR \uD83D\uDC3B",
        "§7A predator who must kill a certain number of players to survive (admins will let you know). You aren't aligned with wolves or villagers. Hunt wisely to stay alive. §cDO NOT DO ANYTHING BEFORE CONSULTING THE ADMINS!",
        "nationsevent.ng5.role.bear",
        Sound.ENTITY_RAVAGER_CELEBRATE,
        NG5_TeamsEnum.SOLITARIES
    ),

    ASSASSIN(
        "§5ASSASSIN \uD83D\uDDE1",
        "§7Your mission is to kill your assigned target (admins will let you know). Fail, and you die. You're not with the wolves, but your kills may help them. §cDO NOT DO ANYTHING BEFORE CONSULTING THE ADMINS!",
        "nationsevent.ng5.role.assassin",
        Sound.EVENT_MOB_EFFECT_RAID_OMEN,
        NG5_TeamsEnum.SOLITARIES
    ),

    SEER(
        "§bSEER ⚗",
        "§7You possess the ability to see the true identity of one player each day (you will be able to ask the admins in private). Use your knowledge to guide the villagers.",
        "nationsevent.ng5.role.seer",
        Sound.BLOCK_END_PORTAL_FRAME_FILL,
        NG5_TeamsEnum.VILLAGERS
    ),

    KNIGHT(
        "§3KNIGHT \uD83D\uDDE1",
        "§7AYou can kill one player at night, but only once (admins will contact you for this purpose). Use your ability wisely to help the village.",
        "nationsevent.ng5.role.knight",
        Sound.BLOCK_ANVIL_USE,
        NG5_TeamsEnum.VILLAGERS
    ),

    CLERIC(
        "§bCLERIC \uD83D\uDD2E",
        "§7Grant one player immunity from death each night. Protect key villagers and help your side survive.",
        "nationsevent.ng5.role.cleric",
        Sound.BLOCK_BREWING_STAND_BREW,
        NG5_TeamsEnum.VILLAGERS
    ),

    // NOT BEING USED FOR NG5
    WITCH(
        "§5WITCH \uD83E\uDDEA",
        "§7A powerful figure who can both save and destroy. You have one healing potion and one poison potion to use throughout the game. (You can use them at anytime by telling the admins))",
        "nationsevent.ng5.role.witch",
        Sound.ENTITY_WITCH_CELEBRATE,
        NG5_TeamsEnum.VILLAGERS
    ),

    HUNTER(
        "§eHUNTER 🪓",
        "§7A skilled marksman. If you are eliminated, you can take one player down with you. Choose wisely.",
        "nationsevent.ng5.role.hunter",
        Sound.ITEM_CROSSBOW_SHOOT,
        NG5_TeamsEnum.VILLAGERS
    ),

    CUPID(
        "§dCUPID 🏹",
        "§7You are the matchmaker! On the first night, you choose two players to fall in love. If one dies, the other will follow.",
        "nationsevent.ng5.role.cupid",
        Sound.ENTITY_ALLAY_ITEM_GIVEN,
        NG5_TeamsEnum.VILLAGERS
    ),

    GUARD(
        "§9GUARD \uD83C\uDFF0",
        "§7Patrol the village at night with armor, able to fight back if attacked. Your goal is to defend the villagers from wolves (admins will come to you at night for this wait for them before you do anything)",
        "nationsevent.ng5.role.guard",
        Sound.ITEM_SHIELD_BLOCK,
        NG5_TeamsEnum.VILLAGERS
    ),

    LOVER(
        "§dLOVER ❤",
        "§7You are bound to another player. If your lover dies, you will also die of heartbreak. Stay alive together!",
        "nationsevent.ng5.role.lover",
        Sound.ENTITY_ALLAY_ITEM_GIVEN,
        NG5_TeamsEnum.NEUTRAL
    ),

    MAYOR(
        "§6MAYOR \uD83D\uDD14",
        "§7The elected leader of the village. You lead the village and have the final say if two players have the same amount of votes during a voting session.",
        "nationsevent.ng5.role.mayor",
        Sound.BLOCK_BELL_RESONATE,
        NG5_TeamsEnum.NEUTRAL
    );

    companion object {
        // Function to get the team of a role
        fun getTeamRoles(team: NG5_TeamsEnum): List<NG5_RolesEnum> = entries.filter { it.team == team }

        // Function to get role from perm (if non is found it will be VILLAGER)
        fun getRoleFromPerm(perm: String): NG5_RolesEnum = entries.find { it.groupPerm == perm } ?: VILLAGER

        // Function to get player role from perm (if non is found it will be VILLAGER)
        fun getPlayerRole(player: Player): NG5_RolesEnum = entries.find {
            if (it.groupPerm == "") return@find false // For villager role
            player.hasPermission(it.groupPerm)
        } ?: VILLAGER
    }

}