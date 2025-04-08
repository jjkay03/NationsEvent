package com.jjkay03.nationsevent.economy.commands

import com.jjkay03.nationsevent.economy.EconomyUtils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.configuration.file.YamlConfiguration

class BalanceFileCommand : CommandExecutor, TabCompleter {

    // COMMAND
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        // End if no arg is provided
        if (args.isEmpty()) { sender.sendMessage("§cUsage: /command <player>"); return true }

        // Check if player exists
        val targetPlayer = Bukkit.getPlayer(args[0])

        // Try to get player balance file
        val file = targetPlayer?.let { EconomyUtils.getPlayerBalanceFile(it) } ?: run { sender.sendMessage("§cFailed to load balance file data!"); return true }
        val fileContent = YamlConfiguration.loadConfiguration(file).saveToString()

        // Display in chat
        val message = Component.text("§2\uD83D\uDDBF ${targetPlayer.name}'s YML balance file §7(hover)")
            .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("§a${targetPlayer.name}'s YML balance file§r\n\n" + fileContent)))
        sender.sendMessage(message)

        return true
    }

    // TAB COMPLETE
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        return if (args.size == 1) { Bukkit.getOnlinePlayers().map { it.name }.filter { it.startsWith(args[0], true) } }
        else { emptyList() }
    }

}