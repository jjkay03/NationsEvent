package com.jjkay03.nationsevent.commands.voting

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

class TopVotesCommand() : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        // Only players can use this command
        if (sender !is Player) {
            sender.sendMessage("§cOnly players can use this command!")
            return true
        }

        if (VoteCommand.PLAYERS_VOTES.isEmpty()) {
            sender.sendMessage("§cNo votes have been cast yet!")
            return true
        }

        // Count how many votes each player has received
        val voteCounts = mutableMapOf<UUID, Int>()
        VoteCommand.PLAYERS_VOTES.values.forEach { votedFor ->
            voteCounts[votedFor] = voteCounts.getOrDefault(votedFor, 0) + 1
        }

        // Sort players by number of votes, descending, and take the top 10
        val topVotedPlayers = voteCounts.entries
            .sortedByDescending { it.value }
            .take(10)

        // Display the top 10 players and their vote counts
        sender.sendMessage("§e§l==== TOP 10 VOTES ====")
        topVotedPlayers.forEach { (uuid, count) ->
            val playerName = Bukkit.getOfflinePlayer(uuid).name ?: "Unknown Player"
            sender.sendMessage("§a$count §f- $playerName")
        }
        sender.sendMessage("§e§l====================")

        return true
    }
}
