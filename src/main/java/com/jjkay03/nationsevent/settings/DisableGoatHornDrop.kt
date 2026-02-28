package com.jjkay03.nationsevent.settings

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.utils.Config
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDropItemEvent
import org.bukkit.entity.Goat

class DisableGoatHornDrop: Listener {

    // INITIALIZATION
    init {
        load(Config.SETTINGS_DISABLE_GOAT_HORN_DROP)
    }

    // LOAD (If enabled in config)
    fun load(enabled: Boolean) {
        if (!enabled) return
        Bukkit.getPluginManager().registerEvents(this, NationsEvent.INSTANCE)
        NationsEvent.INSTANCE.logger.info("- Loading setting: ${this::class.simpleName}")
    }

    // Prevent goat horn drops
    @EventHandler
    fun onGoatDropHorn(event: EntityDropItemEvent) {
        if (event.entity !is Goat) return
        if (event.itemDrop.itemStack.type == Material.GOAT_HORN) event.isCancelled = true
    }
}
