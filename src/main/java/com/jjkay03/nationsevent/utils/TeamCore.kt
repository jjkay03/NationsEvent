package com.jjkay03.nationsevent.utils

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.plugin.java.JavaPlugin

class TeamCore(private val plugin: JavaPlugin) : Listener {
    private val config = plugin.config

    private val coreBlock = Material.REINFORCED_DEEPSLATE
    private val teamCoreEnabled: Boolean = config.getBoolean("team-core-enable")
    private val teamCoreWorld: String = config.getString("team-core-world") ?: "world"

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        if (!teamCoreEnabled) return // End if team core is false (disabled)
        if (event.block.world.name != teamCoreWorld) return // End if wrong world
        if (event.block.type != coreBlock) return // End if wrong block

        event.player.sendMessage("You mined the core block!")
    }
}
