package com.jjkay03.nationsevent.commands

import com.jjkay03.nationsevent.utils.ApplyResourcepack
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ApplyServerPackCommand : CommandExecutor {
    // Command
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        // End command if sender is not a player
        if (sender !is Player) { sender.sendMessage("§cOnly players can run this command!"); return true }

        // Apply pack to player
        ApplyResourcepack.applyPack(sender)
        sender.sendMessage("§aApplying server pack...")

        return true
    }
}