package com.jjkay03.nationsevent

import com.jjkay03.nationsevent.utils.LogsManager
import net.luckperms.api.model.group.Group
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.enchantments.Enchantment
import java.io.File

class Saves() {
    companion object {
        // Rank perms nations event
        const val PERM_ADMIN: String = "nationsevent.admin"
        const val PERM_PROD: String = "nationsevent.production"
        const val PERM_STAFF: String = "nationsevent.staff"
        const val PERM_SPECTATOR: String = "nationsevent.spectator"

        // Permissions
        const val PERM_USE_CHAT: String = "nationsevent.usechat"
        const val PERM_SIMPLE_VOICECHAT_SPEAK: String = "voicechat.speak"

        // Luckperms groups
        val LP_GROUP_ADMIN: Group? = NationsEvent.LP_GROUP_MANAGER.getGroup("admin")
        val LP_GROUP_PROD: Group? = NationsEvent.LP_GROUP_MANAGER.getGroup("prod")
        val LP_GROUP_STAFF: Group? = NationsEvent.LP_GROUP_MANAGER.getGroup("staff")
        val LP_GROUP_DEFAULT: Group? = NationsEvent.LP_GROUP_MANAGER.getGroup("default")

        // Directories
        val DIR_MAIN_PLUGIN = File("plugins/NationsEvent")
        val DIR_ECONOMY = File(DIR_MAIN_PLUGIN, "economy")
        val DIR_ECONOMY_BALANCES = File(DIR_ECONOMY, "balances")
        val DIR_ECONOMY_LOGS = File(DIR_ECONOMY, "economy_logs")
        val DIR_EVENT_IGNS = File(DIR_MAIN_PLUGIN, "event_igns")
        val DIR_EXPORTED_VOTES = File(DIR_MAIN_PLUGIN, "exported_votes")

        // Files
        const val FILE_NAME_CONFIG = "config.yml"; val FILE_CONFIG = File(DIR_MAIN_PLUGIN, FILE_NAME_CONFIG)
        const val FILE_NAME_WEBHOOKS = "webhooks.yml"; val FILE_WEBHOOKS = File(DIR_MAIN_PLUGIN, FILE_NAME_WEBHOOKS)
        // Log files
        val LOG_FILE_NAME_ECONOMY = LogsManager.generateLogFileName(); val LOG_FILE_ECONOMY = File(DIR_ECONOMY_LOGS, LOG_FILE_NAME_ECONOMY)

        // Event variables
        lateinit var EVENT_CODENAME: String
        var SESSION_STARTED: Boolean = false
        var SESSION_START_TIME: Long = 0

        // Webhooks links
        lateinit var CONFIG_WEBHOOK: FileConfiguration
        lateinit var WEBHOOK_ADMIN: String
        lateinit var WEBHOOK_PLAYER: String

        // Items with disabled crafts
        val DISABLED_CRAFT_ITEMS = setOf(
            // General disabled
            Material.ENDER_CHEST,
            Material.END_CRYSTAL,
            Material.RESPAWN_ANCHOR,
            Material.TNT_MINECART,
            Material.JUKEBOX,
            //Material.FIREWORK_ROCKET,
            //Material.GOLDEN_HELMET,

            // Boats
            Material.OAK_BOAT, Material.SPRUCE_BOAT, Material.BIRCH_BOAT, Material.JUNGLE_BOAT, Material.ACACIA_BOAT, Material.DARK_OAK_BOAT, Material.MANGROVE_BOAT, Material.CHERRY_BOAT, Material.BAMBOO_RAFT, Material.PALE_OAK_BOAT,
            Material.OAK_CHEST_BOAT, Material.SPRUCE_CHEST_BOAT, Material.BIRCH_CHEST_BOAT, Material.JUNGLE_CHEST_BOAT, Material.ACACIA_CHEST_BOAT, Material.DARK_OAK_CHEST_BOAT, Material.MANGROVE_CHEST_BOAT, Material.CHERRY_CHEST_BOAT, Material.BAMBOO_CHEST_RAFT, Material.PALE_OAK_CHEST_BOAT,
        )

        // Limited and disabled enchant (0 to disable enchant)
        val LIMITED_ENCHANTMENTS = mapOf(
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
            Enchantment.RIPTIDE to 0
        )
    }

    init {
        // Get variables from config
        EVENT_CODENAME = NationsEvent.INSTANCE.config.getString("event-codename").toString()

        // Get webhooks links from webhooks.yml
        CONFIG_WEBHOOK = YamlConfiguration.loadConfiguration(FILE_WEBHOOKS)
        WEBHOOK_ADMIN = CONFIG_WEBHOOK.getString("webhook-admin", "") ?: ""
        WEBHOOK_PLAYER = CONFIG_WEBHOOK.getString("webhook-player", "") ?: ""

    }
}
