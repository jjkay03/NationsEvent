package com.jjkay03.nationsevent.specific.ne5

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.specific.EventSpecific
import com.jjkay03.nationsevent.integrations.luckperms.LuckPermsUtils
import net.luckperms.api.model.group.Group
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class AssignRandomTeam : Listener {

    companion object  {
        var ENABLED = false
    }

    // List of all teams
    private val teams = listOf(
        EventSpecific.LP_GROUP_NS8_HOT!!,
        EventSpecific.LP_GROUP_NS8_COLD!!
    )

    // INITIALIZATION
    init {
        NationsEvent.INSTANCE.server.pluginManager.registerEvents(this, NationsEvent.INSTANCE)
    }

    // PLAYER JOIN EVENT
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) { assignPlayerToTeam(event.player) }

    // Assign a player to the team with the least players
    private fun assignPlayerToTeam(player: Player) {
        // End if disabled
        if (!ENABLED) return

        // End if player is staff
        if (player.hasPermission(Saves.PERM_STAFF)) return

        // End if player is already in one of the teams
        if (isPlayerInAnyTeam(player)) return

        // Get the team with the least players
        val targetTeam = getTeamWithLeastPlayers()

        // Assign player to that team
        LuckPermsUtils.playerSetGroup(player, targetTeam)
        player.sendMessage("§7\uD83D\uDC65 Applied team ${targetTeam.displayName}")
    }

    // Helper function to check if player is already in any team
    private fun isPlayerInAnyTeam(player: Player): Boolean {
        return teams.any { team -> LuckPermsUtils.isPlayerInGroup(player, team) }
    }

    // Helper function to get the team with the least players (random if tied)
    private fun getTeamWithLeastPlayers(): Group {
        // Count players in each team
        val teamCounts = teams.associateWith { team ->
            LuckPermsUtils.groupGetAllUsers(team).size
        }

        // Find minimum count
        val minCount = teamCounts.values.minOrNull() ?: 0

        // Get all teams with minimum count
        val teamsWithMinCount = teamCounts.filter { it.value == minCount }.keys.toList()

        // Return random team if multiple have same count
        return teamsWithMinCount.random()
    }

}
