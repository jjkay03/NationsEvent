package com.jjkay03.nationsevent.utils

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.scheduler.BukkitRunnable
import java.security.AllPermission

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

        // Deal with destroyed core
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
            // Cancel if player is trying to break his own team core
            if (event.player.hasPermission(coreRedPermission)) {
                event.player.sendMessage("§cYou can not destroy your team's core!")
                event.isCancelled = true
                return
            }

            // Broken core actions
            announceDestroyedCore(coreRedName, event.player)
            killPlayers(coreRedPermission)
        }

        // Blue core destroyed
        else if (blockX == blueCoreX && blockY == blueCoreY && blockZ == blueCoreZ) {
            // Cancel if player is trying to break his own team core
            if (event.player.hasPermission(coreBluePermission)) {
                event.player.sendMessage("§cYou can not destroy your team's core!")
                event.isCancelled = true
                return
            }

            // Broken core actions
            announceDestroyedCore(coreBlueName,  event.player)
            killPlayers(coreBluePermission)
        }
    }

    // Function that announce destroyed core to all players
    private fun announceDestroyedCore(coreName: String, coreMinerPlayer: Player) {
        Bukkit.getServer().onlinePlayers.forEach { player ->
            player.sendTitle("§e⚑", "$coreName §eHAS BEEN DESTROYED!", 10, 200, 10)
            player.sendMessage("§7⚑ $coreName §7has been destroyed by ${coreMinerPlayer.name}!")
            player.playSound(player.location, Sound.BLOCK_END_PORTAL_SPAWN, 1f, 1f)
            player.playSound(player.location, Sound.ENTITY_ENDER_DRAGON_DEATH, 1f, 1f)
        }
    }

    // Function that kills all players on the destroyed core team
    private fun killPlayers(corePermission: String) {
        // Countdown and final title
        object : BukkitRunnable() {
            var count = 20

            override fun run() {
                // 10s count down
                if (count > 0) {
                    if (count <= 10) {
                        Bukkit.getServer().onlinePlayers.forEach { player ->
                            player.sendTitle("§e$count", "", 0, 20, 0)
                            player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1f, 1f)
                        }
                    }
                    count--
                }
                // Your fight is over
                else {
                    Bukkit.getServer().onlinePlayers.forEach { player ->
                        player.sendTitle("§6Your fight is over.", "", 0, 100, 20)
                        player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1f, 1f)
                    }
                    killPlayersLogic(corePermission) // Kill players
                    this.cancel() // Stop the countdown
                }
            }

        }.runTaskTimer(NationsEvent.INSTANCE, 0L, 20L)
    }

    // Function that kills the correct players
    private fun killPlayersLogic(corePermission: String) {
        Bukkit.getServer().onlinePlayers.forEach { player ->
            if (!player.hasPermission(corePermission)) return // End if player not on broken core team
            if (player.hasPermission(NationsEvent.PERM_STAFF)) return // End if staff
            player.health = 0.0 // Kill player
        }
    }

}
