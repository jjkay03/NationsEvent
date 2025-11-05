package com.jjkay03.nationsevent.commands.others

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class SudoCommand(private val commandName: String) : CommandExecutor {

    // INITIALIZATION (Register command)
    init {
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
    }

    // COMMAND
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        // End if no permission
        if (!sender.hasPermission("nationsevent.sudo")) { sender.sendMessage("§cYou don't have permission!"); return true }

        // End if no args
        if (args.isEmpty()) { sender.sendMessage("§cUsage: /sudo <player> <command>"); return true }

        // Get target player
        val target = Bukkit.getPlayer(args[0])
        if (target == null) { sender.sendMessage("§cPlayer not found!"); return true }

        // End if target is operator or admin
        if (target.isOp || target.hasPermission(Saves.PERM_ADMIN)) { sender.sendMessage("§cYou cannot sudo this player!"); return true }

        // End if no command provided
        if (args.size < 2) { sender.sendMessage("§cUsage: /sudo <player> <command>"); return true }

        // Execute command as target
        val commandToRun = args.drop(1).joinToString(" ")
        target.performCommand(commandToRun)
        sender.sendMessage("§6Forced ${target.name} to run: §e/$commandToRun")

        return true
    }
}
