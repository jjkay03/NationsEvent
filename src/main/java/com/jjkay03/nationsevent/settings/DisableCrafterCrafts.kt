package com.jjkay03.nationsevent.settings

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.utils.ServerType
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.CrafterCraftEvent

// Separated crafters disabled crafts from regular DisabledCrafts for 1.20 support

class DisableCrafterCrafts : Listener {

    // REGISTER EVENTS
    init {
        // Only enable if Minecraft version 1.21+
        if (ServerType.MINECRAFT_VERSION == ServerType.MinecraftVersion.V1_21) {
            Bukkit.getPluginManager().registerEvents(this, NationsEvent.INSTANCE)
            NationsEvent.INSTANCE.logger.info("- Loading setting: ${this::class.simpleName}")
        }
    }

    // Cancel craft from crafter
    @EventHandler
    fun onCrafterCraft(event: CrafterCraftEvent) {
        val result = event.result
        if (result.type !in Saves.DISABLED_CRAFT_ITEMS) return
        event.isCancelled = true
    }

}