package com.jjkay03.nationsevent

import net.luckperms.api.model.group.Group
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

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

        // Event variables
        lateinit var EVENT_CODENAME: String
        var SESSION_STARTED: Boolean = false
        var SESSION_START_TIME: Long = 0

        // Items with disabled crafts
        val DISABLED_CRAFT_ITEMS = setOf(
            // General disabled
            Material.ENDER_CHEST,
            Material.JUKEBOX,
            //Material.FIREWORK_ROCKET,
            //Material.GOLDEN_HELMET,
            //Material.DIAMOND_SWORD,
            //Material.DIAMOND_AXE,

            // Boats
            Material.OAK_BOAT, Material.SPRUCE_BOAT, Material.BIRCH_BOAT, Material.JUNGLE_BOAT, Material.ACACIA_BOAT, Material.DARK_OAK_BOAT, Material.MANGROVE_BOAT, Material.CHERRY_BOAT, Material.BAMBOO_RAFT,
            Material.OAK_CHEST_BOAT, Material.SPRUCE_CHEST_BOAT, Material.BIRCH_CHEST_BOAT, Material.JUNGLE_CHEST_BOAT, Material.ACACIA_CHEST_BOAT, Material.DARK_OAK_CHEST_BOAT, Material.MANGROVE_CHEST_BOAT, Material.CHERRY_CHEST_BOAT, Material.BAMBOO_CHEST_RAFT,
        )

        // Limited and disabled enchant (0 to disable enchant)
        val LIMITED_ENCHANTMENTS = mapOf(
            Enchantment.PROTECTION to 2,
            Enchantment.FIRE_PROTECTION to 0,
            Enchantment.BLAST_PROTECTION to 0,
            Enchantment.PROJECTILE_PROTECTION to 0,
            Enchantment.THORNS to 0,
            Enchantment.SHARPNESS to 0,
            Enchantment.FIRE_ASPECT to 0,
            Enchantment.POWER to 0,
            Enchantment.PUNCH to 0,
            Enchantment.FLAME to 0,
            Enchantment.PIERCING to 1,
            Enchantment.QUICK_CHARGE to 0
        )
    }

    init {
        // Variables from config
        EVENT_CODENAME = NationsEvent.INSTANCE.config.getString("event-codename").toString()
    }
}
