package com.jjkay03.nationsevent.commands.voting

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ClearVotesCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        // Only allow players to execute the command
        if (sender !is Player) {
            sender.sendMessage("§cOnly players can use this command!")
            return true
        }

        val player = sender

        // Check if the player has typed 'CONFIRM'
        if (args.size != 1 || args[0] != "CONFIRM") {
            player.sendMessage("§cTo clear votes, use /clearvotes CONFIRM")
            return true
        }

        // Clear all votes
        VoteCommand.PLAYERS_VOTES.clear()

        player.sendMessage("§aAll votes have been cleared successfully!")

        return true
    }
}
