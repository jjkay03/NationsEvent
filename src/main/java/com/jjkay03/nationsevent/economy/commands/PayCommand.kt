package com.jjkay03.nationsevent.economy.commands

import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.economy.Economy
import com.jjkay03.nationsevent.economy.EconomyUtils
import com.jjkay03.nationsevent.utils.LogsManager
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class PayCommand : CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // End if sender not player
        if (sender !is Player) { sender.sendMessage("§cOnly players can use this command!"); return true }

        // End if invalid args
        if (args.size < 2) { sender.sendMessage("§cUsage: /pay <player> <amount>"); return true }

        // End if player not found
        val target = Bukkit.getPlayer(args[0])
        if (target == null || !target.isOnline) { sender.sendMessage("§cPlayer not found or not online!"); return true }

        // End if target is self
        if (target == sender) { sender.sendMessage("§cYou cannot pay yourself!"); return true }

        // End if amount is negative
        val amount = args[1].toIntOrNull()
        if (amount == null || amount <= 0) { sender.sendMessage("§cInvalid amount!"); return true }

        // End if balance is under paid amount
        val senderBalance = EconomyUtils.getPlayerBalance(sender)
        if (amount > senderBalance) { sender.sendMessage("§cYou do not have enough money to pay $amount${Economy.MONEY_SYMBOL}${if (senderBalance < 0) " ${Economy.TXT_IN_DEBT}" else ""}"); return true }

        // Perform the transaction
        val senderUpdatedBalance = senderBalance - amount
        val targetBalance = EconomyUtils.getPlayerBalance(target)
        val targetUpdatedBalance = targetBalance + amount
        EconomyUtils.setPlayerBalance(sender, senderUpdatedBalance)
        EconomyUtils.setPlayerBalance(target, targetUpdatedBalance)

        // Alert players
        sender.sendMessage("§c[${Economy.MONEY_SYMBOL}-] §7You paid §f${target.name} §7a total of ${EconomyUtils.formatMoney(amount)} §7(new balance ${EconomyUtils.formatMoney(senderUpdatedBalance)})")
        target.sendMessage("§a[${Economy.MONEY_SYMBOL}+] §7You received ${EconomyUtils.formatMoney(amount)} §7from §f${sender.name} §7(new balance ${EconomyUtils.formatMoney(targetUpdatedBalance)})")

        // Log
        LogsManager.log(Saves.LOG_FILE_ECONOMY, "Economy", "[Pay Command - ${sender.name}] ${sender.name} ([-] $senderBalance -> $senderUpdatedBalance) paid ${target.name} ([+] $targetBalance -> $targetUpdatedBalance) an amount of $amount")

        return true
    }

    override fun onTabComplete(
        sender: CommandSender, command: Command, alias: String, args: Array<out String>
    ): List<String> {
        return when (args.size) {
            1 -> Bukkit.getOnlinePlayers().map { it.name }.filter { it.startsWith(args[0], true) }
            2 -> listOf("<amount>")
            else -> emptyList()
        }
    }
}
