package com.jjkay03.nationsevent.commands.voting

import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.Utils
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

    // COMMAND
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        // Check if sender is player and if votes have been casted
        if (sender !is Player) { sender.sendMessage("§cOnly players can use this command!"); return true }
        if (VoteCommand.PLAYERS_VOTES.isEmpty()) { sender.sendMessage("§cNo votes have been cast yet!"); return true }

        sender.sendMessage("§eStarted exporting votes...")

        val note: String? = if (args.isNotEmpty()) args.joinToString(" ") else null
        val dateFormat = SimpleDateFormat("dd-MM-yyyy--HH-mm-ss")
        val currentTime = Date()

        val filename = if (note != null) { "VOTES - ${dateFormat.format(currentTime)} ($note).txt" }
        else { "VOTES - ${dateFormat.format(currentTime)}.txt" }

        val voteFile = File(Saves.DIR_EXPORTED_VOTES, filename)

        try {
            FileWriter(voteFile).use { writer ->
                writer.write("EXPORTED VOTES - ${SimpleDateFormat("dd/MM/yyyy 'at' HH:mm:ss").format(currentTime)}\n")
                writer.write("\nEVENT CODENAME: ${Saves.EVENT_CODENAME}\n")
                writer.write("\nEXPORT NOTE: $note\n")
                writer.write("\n== VOTES ====================\n")

                val voteCounts = mutableMapOf<UUID, Int>()
                VoteCommand.PLAYERS_VOTES.values.forEach { votedFor ->
                    voteCounts[votedFor] = voteCounts.getOrDefault(votedFor, 0) + 1
                }

                val sortedVotes = voteCounts.entries.sortedByDescending { it.value }
                sortedVotes.forEach { (uuid, count) ->
                    val playerName = Bukkit.getOfflinePlayer(uuid).name ?: "Unknown Player"
                    writer.write("$count - $playerName\n")
                }
                writer.write("=============================\n")

                writer.write("\n== PLAYER VOTES =============\n")
                VoteCommand.PLAYERS_VOTES.forEach { (voterUUID, votedForUUID) ->
                    val voterName = Bukkit.getOfflinePlayer(voterUUID).name ?: "Unknown Player"
                    val votedForName = Bukkit.getOfflinePlayer(votedForUUID).name ?: "Unknown Player"
                    writer.write("$voterName -> $votedForName\n")
                }
                writer.write("=============================\n")
            }

            sender.sendMessage("§2Votes have been exported successfully to ${voteFile.name}!")
        } catch (e: Exception) {
            sender.sendMessage("§cFailed to export votes!")
            e.printStackTrace()
        }

        return true
    }
}
