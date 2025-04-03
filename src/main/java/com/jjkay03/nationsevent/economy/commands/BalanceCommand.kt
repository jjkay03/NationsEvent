package com.jjkay03.nationsevent.economy.commands

import com.jjkay03.nationsevent.economy.EconomyUtils
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class BalanceCommand : CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // Check if the sender is a player
        if (sender !is Player) { sender.sendMessage("§cOnly players can check balances."); return true }

        // If no player is specified show the sender's balance
        if (args.isEmpty()) {
            val balance = EconomyUtils.getPlayerBalance(sender)
            sender.sendMessage("§7Your balance is ${EconomyUtils.formatMoney(balance)}")
            return true
        }

        // Check if player exists
        val targetPlayer = Bukkit.getOfflinePlayer(args[0])
        if (!targetPlayer.hasPlayedBefore()) { sender.sendMessage("§cPlayer not found!"); return true }

        // If a player is specified show their balance
        val balance = targetPlayer.player?.let { EconomyUtils.getPlayerBalance(it) }
        sender.sendMessage("§7Balance of §f${targetPlayer.name} §7is ${balance?.let { EconomyUtils.formatMoney(it) }}")
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        return if (args.size == 1) { Bukkit.getOnlinePlayers().map { it.name }.filter { it.startsWith(args[0], true) } }
        else { emptyList() }
    }
}
