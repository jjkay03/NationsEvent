package com.jjkay03.nationsevent.commands.voting

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

class VoteCommand : CommandExecutor {
    companion object {
        // Store the votes (who voted for whom)
        val PLAYERS_VOTES = mutableMapOf<UUID, UUID>()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {

        // Only players can use this command
        if (sender !is Player) {
            sender.sendMessage("§cOnly players can use this command!")
            return true
        }

        val voter = sender

        // Ensure the right amount of arguments
        if (args.size != 1) {
            voter.sendMessage("§cUsage: /vote <player>")
            return true
        }

        // Find the player who is being voted for
        val votedFor = Bukkit.getPlayer(args[0])

        // Check if the player exists
        if (votedFor == null) {
            voter.sendMessage("§cPlayer ${args[0]} is not online or doesn't exist!")
            return true
        }

        // Prevent voting for oneself
        if (voter.uniqueId == votedFor.uniqueId) {
            voter.sendMessage("§cYou can't vote for yourself!")
            return true
        }

        // Check if the player has already voted
        if (PLAYERS_VOTES.containsKey(voter.uniqueId)) {
            // Player is changing their vote
            voter.sendMessage("§eYou have updated your vote to ${votedFor.name}.")
        } else {
            // First time voting
            voter.sendMessage("§aYou have voted for ${votedFor.name}!")
        }

        // Record or update the vote
        PLAYERS_VOTES[voter.uniqueId] = votedFor.uniqueId

        return true
    }
}
