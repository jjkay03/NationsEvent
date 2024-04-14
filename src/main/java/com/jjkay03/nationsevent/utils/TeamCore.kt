package com.jjkay03.nationsevent.utils

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class TeamCore() : Listener {
    private val config = NationsEvent.INSTANCE.config

    private val coreBlockMaterial = Material.RED_WOOL
    private val teamCoreEnabled: Boolean = config.getBoolean("team-core-enable")
    private val teamCoreWorld: String = config.getString("team-core-world") ?: "world"

    // Core Red
    private val coreRedName: String = config.getString("team-cores.core-red.name") ?: "Red Core"
    private val coreRedPermission: String = config.getString("team-cores.core-red.permission") ?: "nationsevent.team.red"
    private val coreRedLocation: Map<String, Any>? = config.getConfigurationSection("team-cores.core-red.location")?.getValues(false)

    // Core Blue
    private val coreBlueName: String = config.getString("team-cores.core-blue.name") ?: "Blue Core"
    private val coreBluePermission: String = config.getString("team-cores.core-blue.permission") ?: "nationsevent.team.blue"
    private val coreBlueLocation: Map<String, Any>? = config.getConfigurationSection("team-cores.core-blue.location")?.getValues(false)


    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        if (!teamCoreEnabled) return // End if team core is false (disabled)
        if (event.block.world.name != teamCoreWorld) return // End if wrong world
        if (event.block.type != coreBlockMaterial) return // End if wrong block

        // End if player is not in survival mode - Creative mode accidental break protection
        if (event.player.gameMode != GameMode.SURVIVAL) { event.player.sendMessage("§4⚠ Core destruction canceled because you are not in survival mode!"); return }

        event.player.sendMessage("§aCORE BLOCK DESTROYED!")
        coreBlockDestroyed(event)
    }


    // Function that handles core destruction
    private fun coreBlockDestroyed(event: BlockBreakEvent) {
        val blockLocation = event.block.location
        val blockX = blockLocation.blockX
        val blockY = blockLocation.blockY
        val blockZ = blockLocation.blockZ

        // Core Red
        val redCoreLocation = coreRedLocation ?: return
        val redCoreX = redCoreLocation["x"] as Int
        val redCoreY = redCoreLocation["y"] as Int
        val redCoreZ = redCoreLocation["z"] as Int

        // Core Blue
        val blueCoreLocation = coreBlueLocation ?: return
        val blueCoreX = blueCoreLocation["x"] as Int
        val blueCoreY = blueCoreLocation["y"] as Int
        val blueCoreZ = blueCoreLocation["z"] as Int

        // Red core destroyed
        if (blockX == redCoreX && blockY == redCoreY && blockZ == redCoreZ) {
            event.player.sendMessage("§cRED CORE DESTROYED!")
        }

        // Blue core destroyed
        else if (blockX == blueCoreX && blockY == blueCoreY && blockZ == blueCoreZ) {
            event.player.sendMessage("§9BLUE CORE DESTROYED!")
        }


    }
}
