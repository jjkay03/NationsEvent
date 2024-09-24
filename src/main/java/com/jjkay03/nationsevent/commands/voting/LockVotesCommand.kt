package com.jjkay03.nationsevent.commands.voting

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class LockVotesCommand : CommandExecutor {

    // Command
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        VoteCommand.LOCKED_VOTES = !VoteCommand.LOCKED_VOTES // Flip LOCKED_VOTES bool
        sender.sendMessage(
            if (VoteCommand.LOCKED_VOTES) "§7Votes are now §aOPENNED §7to everyone"
            else "§7Votes are now §cLOCKED §7for everyone"
        )
        return true
    }
}