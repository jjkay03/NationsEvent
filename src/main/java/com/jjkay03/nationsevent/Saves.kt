package com.jjkay03.nationsevent

import com.jjkay03.nationsevent.utils.LogsManager
import net.luckperms.api.model.group.Group
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import java.io.File

class Saves {
    companion object {
        // EVENT VARIABLES
        var SESSION_STARTED: Boolean = false
        var SESSION_START_TIME: Long = 0


        // RANK PERMS NATIONS EVENT
        const val PERM_ADMIN: String = "nationsevent.admin"
        const val PERM_PROD: String = "nationsevent.production"
        const val PERM_STAFF: String = "nationsevent.staff"
        const val PERM_SPECTATOR: String = "nationsevent.spectator"


        // LUCKPERMS GROUPS
        val LP_GROUP_ADMIN: Group? = NationsEvent.LP_GROUP_MANAGER.getGroup("admin")
        val LP_GROUP_PROD: Group? = NationsEvent.LP_GROUP_MANAGER.getGroup("prod")
        val LP_GROUP_STAFF: Group? = NationsEvent.LP_GROUP_MANAGER.getGroup("staff")
        val LP_GROUP_DEFAULT: Group? = NationsEvent.LP_GROUP_MANAGER.getGroup("default")


        // PERMISSIONS
        const val PERM_USE_CHAT: String = "nationsevent.use-chat"
        const val PERM_DEATH_BAN_BYPASS = "nationsevent.death-ban.bypass"
        const val PERM_ECONOMY_USE_TRADE_ENTITY: String = "nationsevent.economy.use-trade-entity"
        const val PERM_ECONOMY_RECEIVE_MONEY: String = "nationsevent.economy.receive-money"
        const val PERM_SIMPLE_VOICECHAT_SPEAK: String = "voicechat.speak"


        // DIRECTORIES
        val DIR_MAIN_PLUGIN = File("plugins/NationsEvent")
        val DIR_EVENT_IGNS = File(DIR_MAIN_PLUGIN, "event_igns")
        // DIRS - Economy
        val DIR_ECONOMY = File(DIR_MAIN_PLUGIN, "economy")
        val DIR_ECONOMY_BALANCES = File(DIR_ECONOMY, "balances")
        val DIR_ECONOMY_LOGS = File(DIR_ECONOMY, "economy_logs")
        // DIRS - Player Group Chat
        val DIR_PLAYER_GC = File(DIR_MAIN_PLUGIN, "player_group_chat")
        val DIR_PLAYER_GC_LOGS = File(DIR_PLAYER_GC, "player_group_chat_logs")


        // FILES
        const val FILE_NAME_CONFIG = "config.yml"; val FILE_CONFIG = File(DIR_MAIN_PLUGIN, FILE_NAME_CONFIG)
        const val FILE_NAME_WEBHOOKS = "webhooks.yml"; val FILE_WEBHOOKS = File(DIR_MAIN_PLUGIN, FILE_NAME_WEBHOOKS)
        // FILES - Economy
        val LOG_FILE_NAME_ECONOMY = LogsManager.generateLogFileName(); val LOG_FILE_ECONOMY = File(DIR_ECONOMY_LOGS, LOG_FILE_NAME_ECONOMY) // LOG
        // FILES - Player Group Chat
        const val FILE_NAME_PLAYER_GC = "player_group_chats.json"; val FILE_PLAYER_GROUP_CHATS = File(DIR_PLAYER_GC, FILE_NAME_PLAYER_GC)
        val LOG_FILE_NAME_PLAYER_GC = LogsManager.generateLogFileName(); val LOG_FILE_PLAYER_GC = File(DIR_PLAYER_GC_LOGS, LOG_FILE_NAME_PLAYER_GC) // LOG


        // ITEMS WITH DISABLED CRAFTS
        @JvmStatic
        val DISABLED_CRAFT_ITEMS: Set<Material> = setOf(
            // General disabled
            Material.ENDER_CHEST, Material.END_CRYSTAL, Material.RESPAWN_ANCHOR, Material.TNT_MINECART,
            Material.JUKEBOX, Material.HOPPER, Material.CRAFTER,
            //Material.GOLDEN_HELMET,

            // Boats
            Material.OAK_BOAT, Material.SPRUCE_BOAT, Material.BIRCH_BOAT, Material.JUNGLE_BOAT, Material.ACACIA_BOAT, Material.DARK_OAK_BOAT, Material.MANGROVE_BOAT, Material.CHERRY_BOAT, Material.BAMBOO_RAFT,
            Material.OAK_CHEST_BOAT, Material.SPRUCE_CHEST_BOAT, Material.BIRCH_CHEST_BOAT, Material.JUNGLE_CHEST_BOAT, Material.ACACIA_CHEST_BOAT, Material.DARK_OAK_CHEST_BOAT, Material.MANGROVE_CHEST_BOAT, Material.CHERRY_CHEST_BOAT, Material.BAMBOO_CHEST_RAFT,
            //Material.PALE_OAK_BOAT, Material.PALE_OAK_CHEST_BOAT
        )


        // LIMITED AND DISABLED ENCHANTS (0 to disable enchant)
        @JvmStatic
        val LIMITED_ENCHANTMENTS: Map<Enchantment, Int> = mapOf(
            Enchantment.PROTECTION to 2,
            Enchantment.FIRE_PROTECTION to 0,
            Enchantment.BLAST_PROTECTION to 0,
            Enchantment.PROJECTILE_PROTECTION to 0,
            Enchantment.THORNS to 0,
            Enchantment.SHARPNESS to 1,
            Enchantment.FIRE_ASPECT to 0,
            Enchantment.POWER to 0,
            Enchantment.PUNCH to 0,
            Enchantment.FLAME to 0,
            Enchantment.PIERCING to 1,
            Enchantment.QUICK_CHARGE to 0,
            Enchantment.RIPTIDE to 0,
            Enchantment.FORTUNE to 0
        )

    }
}