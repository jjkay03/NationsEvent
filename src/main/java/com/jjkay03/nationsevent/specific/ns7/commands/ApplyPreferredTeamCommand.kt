package com.jjkay03.nationsevent.specific.ns7.commands

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.specific.EventSpecific
import com.jjkay03.nationsevent.utils.LuckPermsUtils
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
        // Group players by their preferred team
        val teamLists = groupPlayersByTeam()

        // Equalize teams
        equalizeTeams(teamLists)

        // Apply groups to players
        applyGroupsToPlayers(teamLists)

        // Show final results
        sender.sendMessage("§aTeams equalized and applied!")

        return true
    }

    // Helper function to group players by their preferred team
    private fun groupPlayersByTeam(): MutableMap<Group, MutableList<Player>> {
        val teamLists = mutableMapOf<Group, MutableList<Player>>()
        teamLists[EventSpecific.LP_GROUP_NS7_PLAINS!!] = mutableListOf()
        teamLists[EventSpecific.LP_GROUP_NS7_DESERT!!] = mutableListOf()
        teamLists[EventSpecific.LP_GROUP_NS7_SNOW!!] = mutableListOf()
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
                val removed = players.takeLast(excess) // First come first served
                overflow.addAll(removed)
                players.removeAll(removed.toSet())
            }
        }

        return overflow
    }

    // Helper function to distribute overflow players evenly across teams
    private fun distributeOverflow(teamLists: MutableMap<Group, MutableList<Player>>, overflow: MutableList<Player>, targetSize: Int) {
        var overflowIndex = 0

        // First pass: fill teams below target
        while (overflowIndex < overflow.size) {
            val sortedTeams = teamLists.entries.sortedBy { it.value.size }
            var distributed = false

            for (teamEntry in sortedTeams) {
                if (overflowIndex >= overflow.size) break
                if (teamEntry.value.size < targetSize) {
                    teamEntry.value.add(overflow[overflowIndex])
                    overflowIndex++
                    distributed = true
                }
            }

            // Second pass: distribute remaining evenly if all teams at target
            if (!distributed) {
                for (teamEntry in sortedTeams) {
                    if (overflowIndex >= overflow.size) break
                    teamEntry.value.add(overflow[overflowIndex])
                    overflowIndex++
                }
            }
        }
    }

    // Helper function apply groups to all players
    private fun applyGroupsToPlayers(teamLists: Map<Group, List<Player>>) {
        teamLists.forEach { (group, players) ->
            players.forEach { player ->
                LuckPermsUtils.playerSetGroup(player, group)
            }
        }
    }
}
