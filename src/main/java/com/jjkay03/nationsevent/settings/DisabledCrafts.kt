package com.jjkay03.nationsevent.settings

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.utils.Config
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent

class DisabledCrafts : Listener {

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

    // Replace item result with air
    @EventHandler
    fun onPrepareItemCraft(event: PrepareItemCraftEvent) {
        val result = event.inventory.result ?: return
        if (result.type !in Saves.DISABLED_CRAFT_ITEMS) return
        event.inventory.result = null
    }

    // Cancel craft
    @EventHandler
    fun onCraftItem(event: CraftItemEvent) {
        val result = event.currentItem ?: return
        if (result.type !in Saves.DISABLED_CRAFT_ITEMS) return
        event.isCancelled = true
        val player = event.whoClicked
        player.sendMessage("§8✖ Item ${result.type.name} craft is disabled!")
    }

}