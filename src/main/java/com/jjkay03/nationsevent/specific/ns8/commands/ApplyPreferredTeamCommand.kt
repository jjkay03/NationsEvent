package com.jjkay03.nationsevent.specific.ns8.commands

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.specific.EventSpecific
import com.jjkay03.nationsevent.integrations.luckperms.LuckPermsUtils
import net.luckperms.api.model.group.Group
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ApplyPreferredTeamCommand(private val commandName: String) : CommandExecutor {

    // INITIALIZATION
    init {
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        sender.sendMessage("§7Grouping players by preferred team...")

        // Group players by their preferred team
        val teamLists = groupPlayersByTeam()
        sender.sendMessage("§7Before equalization:")
        teamLists.forEach { (group, players) ->
            sender.sendMessage("§7  ${group.name}: ${players.size} players")
        }

        sender.sendMessage("§7Equalizing teams...")

        // Equalize teams
        equalizeTeams(teamLists)
        sender.sendMessage("§7After equalization:")
        teamLists.forEach { (group, players) ->
            sender.sendMessage("§7  ${group.name}: ${players.size} players")
        }

        sender.sendMessage("§7Applying groups to players...")

        // Apply groups to players
        applyGroupsToPlayers(teamLists)

        // Show final results
        sender.sendMessage("§aTeams equalized and applied!")

        // Clear player preferences
        PreferredTeamCommand.PLAYER_PREFERENCES.clear()

        return true
    }

    // Helper function to group players by their preferred team
    private fun groupPlayersByTeam(): MutableMap<Group, MutableList<Player>> {
        val teamLists = mutableMapOf<Group, MutableList<Player>>()
        teamLists[EventSpecific.LP_GROUP_NS8_HOT!!] = mutableListOf()
        teamLists[EventSpecific.LP_GROUP_NS8_COLD!!] = mutableListOf()
        PreferredTeamCommand.PLAYER_PREFERENCES.forEach { (player, group) -> teamLists[group]?.add(player) }
        return teamLists
    }

    // Helper function to equalize teams by redistributing overflow
    private fun equalizeTeams(teamLists: MutableMap<Group, MutableList<Player>>) {
        val totalPlayers = teamLists.values.sumOf { it.size }
        val targetSize = totalPlayers / teamLists.size

        // Collect overflow from teams with too many players
        val overflow = collectOverflow(teamLists, targetSize)

        // Distribute overflow to teams that need players
        distributeOverflow(teamLists, overflow, targetSize)
    }

    // Helper function to collect overflow players from teams above target size
    private fun collectOverflow(teamLists: MutableMap<Group, MutableList<Player>>, targetSize: Int): MutableList<Player> {
        val overflow = mutableListOf<Player>()
        teamLists.forEach { (_, players) ->
            if (players.size > targetSize) {
                val excess = players.size - targetSize
                val removed = players.takeLast(excess)
                overflow.addAll(removed)
                players.removeAll(removed.toSet())
            }
        }
        return overflow
    }

    // Helper function to distribute overflow players evenly across teams
    private fun distributeOverflow(teamLists: MutableMap<Group, MutableList<Player>>, overflow: MutableList<Player>, targetSize: Int) {
        var overflowIndex = 0

        // Keep distributing overflow players to the smallest teams until all are assigned
        while (overflowIndex < overflow.size) {
            val sortedTeams = teamLists.entries.sortedBy { it.value.size }

            for (teamEntry in sortedTeams) {
                if (overflowIndex >= overflow.size) break
                teamEntry.value.add(overflow[overflowIndex])
                overflowIndex++
            }
        }
    }

    // Helper function apply groups to all players
    private fun applyGroupsToPlayers(teamLists: Map<Group, List<Player>>) {
        teamLists.forEach { (group, players) ->
            players.forEach { player ->
                LuckPermsUtils.playerSetGroup(player, group)
                player.sendMessage("§7👥 Applied team ${group.displayName}")
            }
        }
    }
}