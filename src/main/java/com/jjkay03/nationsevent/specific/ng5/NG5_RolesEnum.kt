package com.jjkay03.nationsevent.specific.ng5

import org.bukkit.Sound

enum class NG5_RolesEnum(
    val displayName : String,
    val description : String,
    val groupPerm : String,
    val sound: Sound
) {

    VILLAGER(
        "§aVILLAGER ⛏",
        "§7An ordinary villager with no special powers. Your goal is to survive and find the werewolves.",
        "group.default",
        Sound.ENTITY_VILLAGER_CELEBRATE
    ),

    WEREWOLF(
        "§cWEREWOLF 🐺",
        "§7A terrifying predator that hunts the villagers at night. Work with your fellow werewolves to eliminate the villagers and KILL EVERYONE. §cDO NOT DO ANYTHING BEFORE CONSULTING THE ADMINS!",
        "group.werewolf",
        Sound.ENTITY_WOLF_HOWL
    ),

    LONEWOLF(
        "§4LONEWOLF 🐺",
        "§7A lone predator that hunts the villagers at night. Pretend to work with your fellow werewolves to eliminate the villagers but your real goal is to KILL EVERYONE. You win ALONE, you need to kill both villagers and werewolves 💀 §cDO NOT DO ANYTHING BEFORE CONSULTING THE ADMINS!",
        "group.lonewolf",
        Sound.ENTITY_WOLF_HOWL
    ),

    SEER(
        "§bSEER ⚗",
        "§7You possess the ability to see the true identity of one player each day (you will be able to ask the admins in private). Use your knowledge to guide the villagers.",
        "group.seer",
        Sound.BLOCK_END_PORTAL_FRAME_FILL
    ),

    WITCH(
        "§5WITCH \uD83E\uDDEA",
        "§7A powerful figure who can both save and destroy. You have one healing potion and one poison potion to use throughout the game. (You can use them at anytime by telling the admins))",
        "group.witch",
        Sound.ENTITY_WITCH_CELEBRATE
    ),

    HUNTER(
        "§eHUNTER 🪓",
        "§7A skilled marksman. If you are eliminated, you can take one player down with you. Choose wisely.",
        "group.hunter",
        Sound.ITEM_CROSSBOW_SHOOT
    ),

    CUPID(
        "§dCUPID 🏹",
        "§7You are the matchmaker! On the first night, you choose two players to fall in love. If one dies, the other will follow.",
        "group.cupid",
        Sound.ENTITY_ALLAY_ITEM_GIVEN
    ),

    LOVER(
        "§dLOVER ❤",
        "§7You are bound to another player. If your lover dies, you will also die of heartbreak. Stay alive together!",
        "group.lover",
        Sound.ENTITY_ALLAY_ITEM_GIVEN
    ),

    MAYOR(
        "§6MAYOR \uD83D\uDD14",
        "§7The elected leader of the village. You lead the village and have the final say if two players have the same amount of votes during a voting session.",
        "group.mayor",
        Sound.BLOCK_BELL_RESONATE
    );

}