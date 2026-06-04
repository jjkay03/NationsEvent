package com.jjkay03.nationsevent.features

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.utils.Config
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

class DeathDropHead : Listener {

    // INITIALIZATION
    init {
        load(Config.FEATURES_DEATH_DROP_HEAD_ENABLE)
    }

    // LOAD (If enabled in config)
    fun load(enabled: Boolean) {
        if (!enabled) return
        Bukkit.getPluginManager().registerEvents(this, NationsEvent.INSTANCE)
        NationsEvent.INSTANCE.logger.info("- Loading feature: ${this::class.simpleName}")
    }

    // Drop player head on death
    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.entity

        // End if player has bypass perm (staff)
        if (player.hasPermission(Saves.PERM_STAFF)) return

        // If only-kills is enabled, only drop head when killed by a player
        if (Config.FEATURES_DEATH_DROP_HEAD_ONLY_KILLS && player.killer == null) return

        // Create item
        val head = ItemStack(Material.PLAYER_HEAD)
        val meta = head.itemMeta as SkullMeta
        meta.owningPlayer = player
        head.itemMeta = meta

        // Drop item
        player.world.dropItemNaturally(player.location, head)
    }
}