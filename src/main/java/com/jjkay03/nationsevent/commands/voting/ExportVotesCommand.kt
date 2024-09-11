package com.jjkay03.nationsevent.commands.voting

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class ExportVotesCommand : CommandExecutor {

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

        // Get the plugin folder and create the "exported votes" folder if it doesn't exist
        val pluginFolder = File("plugins/NationsEvent/exported votes")
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs()
        }

        // Get the current date and time for the filename
        val dateFormat = SimpleDateFormat("dd-MM-yyyy--HH:mm:ss")
        val currentTime = Date()
        val filename = "${dateFormat.format(currentTime)}--VOTES.txt"

        // Create the file in the "exported votes" folder
        val voteFile = File(pluginFolder, filename)

        // File format date
        val fileDateFormat = SimpleDateFormat("dd/MM/yyyy 'at' HH:mm:ss")
        val formattedDate = fileDateFormat.format(currentTime)

        // Use FileWriter to write the content to the file
        FileWriter(voteFile).use { writer ->
            writer.write("EXPORTED VOTES - $formattedDate\n")
            writer.write("\n== VOTES ======================\n")

            // Count how many votes each player has received
            val voteCounts = mutableMapOf<UUID, Int>()
            VoteCommand.PLAYERS_VOTES.values.forEach { votedFor ->
                voteCounts[votedFor] = voteCounts.getOrDefault(votedFor, 0) + 1
            }

            // Sort players by number of votes, descending
            val sortedVotes = voteCounts.entries
                .sortedByDescending { it.value }

            // Write the sorted votes (number of votes - player)
            sortedVotes.forEach { (uuid, count) ->
                val playerName = Bukkit.getOfflinePlayer(uuid).name ?: "Unknown Player"
                writer.write("$count - $playerName\n")
            }
            writer.write("=============================\n")

            // Write the votes each player cast (Player_IGN -> Player IGN)
            writer.write("\n== PLAYER VOTES ================\n")
            VoteCommand.PLAYERS_VOTES.forEach { (voterUUID, votedForUUID) ->
                val voterName = Bukkit.getOfflinePlayer(voterUUID).name ?: "Unknown Player"
                val votedForName = Bukkit.getOfflinePlayer(votedForUUID).name ?: "Unknown Player"
                writer.write("$voterName -> $votedForName\n")
            }
            writer.write("=============================\n")
        }

        // Notify the player
        sender.sendMessage("§2Votes have been exported successfully to ${voteFile.name}!")

        return true
    }
}
