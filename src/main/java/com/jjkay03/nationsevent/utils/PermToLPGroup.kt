package com.jjkay03.nationsevent.utils

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.utils.LuckPermsUtils.isPlayerInGroup
import com.jjkay03.nationsevent.utils.LuckPermsUtils.playerRemoveGroup
import com.jjkay03.nationsevent.utils.LuckPermsUtils.playerSetGroup
import net.luckperms.api.model.group.Group
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

class PermToLPGroup(private val plugin: JavaPlugin) : Listener {

    val enabled = true
    val syncIntervalMinutes = 1

    val permissionGroupPairs = listOf(
        "nationsevent.discord-role.ne3-capitalist" to NationsEvent.LP_GROUP_MANAGER.getGroup("ne3-capitalist"),
        "nationsevent.discord-role.ne3-communist" to NationsEvent.LP_GROUP_MANAGER.getGroup("ne3-communist"),
    )

    // Load on class initialisation
    init { load() }

    // Function to load
    fun load() {
        // End if not enabled
        if (!enabled) return

        // Display in console
        plugin.logger.info("Loading PermToLPGroup temp patch for NE3")

        // Register listeners
        plugin.server.pluginManager.registerEvents(this, plugin)

        // Start periodic sync
        startPeriodicSync()
    }

    // Sync groups for the joining player after 5s
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            if (!player.isOnline) return@Runnable
            permissionGroupPairs.forEach { (permission, group) -> syncPlayerGroup(player, permission, group) }
        }, 100L)
    }

    // Function to start periodic sync
    private fun startPeriodicSync() {
        val ticksInterval = (syncIntervalMinutes * 60 * 20).toLong() // Convert minutes to ticks
        Bukkit.getScheduler().runTaskTimer(plugin, Runnable { syncAllPlayersGroups() }, ticksInterval, ticksInterval)
    }

    // Function to sync all groups for all online players
    fun syncAllPlayersGroups() {
        for (player in Bukkit.getOnlinePlayers()) {
            for ((permission, group) in permissionGroupPairs) {
                syncPlayerGroup(player, permission, group)
            }
        }
    }

    // Function to sync a specific player's group based on permission
    private fun syncPlayerGroup(player: Player, permission: String, group: Group?) {
        if (group == null) return
        val hasPermission = player.hasPermission(permission)
        val isInGroup = isPlayerInGroup(player, group)

        // Skip player if OP or staff
        if (player.isOp || player.hasPermission(Saves.PERM_STAFF)) return

        when {
            // Player has permission but not in group - add them
            hasPermission && !isInGroup -> { playerSetGroup(player, group) }

            // Player doesn't have permission but is in group - remove them
            !hasPermission && isInGroup -> { playerRemoveGroup(player, group) }
        }
    }

}