package com.jjkay03.nationsevent.settings

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.utils.Config
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.CrafterCraftEvent

class DisableCrafterCrafts : Listener {

    // INITIALIZATION
    init {
        load(Config.SETTINGS_DISABLED_CRAFTS)
    }

    // LOAD (If enabled in config)
    fun load(enabled: Boolean) {
        if (!enabled) return
        Bukkit.getPluginManager().registerEvents(this, NationsEvent.INSTANCE)
        NationsEvent.INSTANCE.logger.info("- Loading setting: ${this::class.simpleName}")
    }

    // Cancel craft from crafter
    @EventHandler
    fun onCrafterCraft(event: CrafterCraftEvent) {
        val result = event.result
        if (result.type !in Saves.DISABLED_CRAFT_ITEMS) return
        event.isCancelled = true
    }

}