package com.jjkay03.nationsevent.commands.voting

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class TopVotesCommand : CommandExecutor {

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

        // Start the async task to process votes and prevent blocking the main thread
        object : BukkitRunnable() {
            override fun run() {
                // Count how many votes each player has received
                val voteCounts = mutableMapOf<UUID, Int>()
                VoteCommand.PLAYERS_VOTES.values.forEach { votedFor ->
                    voteCounts[votedFor] = voteCounts.getOrDefault(votedFor, 0) + 1
                }

                // Determine whether to show top 10 or all votes based on the argument
                val isAllVotes = args.isNotEmpty() && args[0].equals("all", ignoreCase = true)

                // Sort players by number of votes, descending
                val sortedPlayers = voteCounts.entries
                    .sortedByDescending { it.value }

                // Show top 10 or all votes based on the argument
                val playersToDisplay = if (isAllVotes) sortedPlayers else sortedPlayers.take(10)

                // Cache player names to avoid repeated slow lookups
                val cachedPlayerNames = mutableMapOf<UUID, String?>()

                // Fetch player names asynchronously (doesn't block the main thread)
                playersToDisplay.forEach { (uuid, _) ->
                    cachedPlayerNames[uuid] = Bukkit.getOfflinePlayer(uuid).name
                }

                // Switch back to the main thread to send messages to the player
                object : BukkitRunnable() {
                    override fun run() {
                        // Total number of votes
                        val totalVotes = VoteCommand.PLAYERS_VOTES.size

                        // Send the appropriate header
                        if (isAllVotes) {
                            sender.sendMessage("§e§l==== ALL VOTES ====")
                        } else {
                            sender.sendMessage("§e§l==== TOP 10 VOTES ====")
                        }

                        // Display total number of votes
                        sender.sendMessage("§7Number of votes: $totalVotes")

                        // Display the players and their vote counts
                        playersToDisplay.forEach { (uuid, count) ->
                            val playerName = cachedPlayerNames[uuid] ?: "Unknown Player"
                            sender.sendMessage("§a$count §f- $playerName")
                        }

                        // Send footer
                        sender.sendMessage("§e§l=====================")
                    }
                }.runTask(NationsEvent.INSTANCE)
            }
        }.runTaskAsynchronously(NationsEvent.INSTANCE)

        return true
    }
}
