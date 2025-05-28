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
import java.util.concurrent.TimeUnit

class PayCommand : CommandExecutor, TabCompleter {

    // Cooldown tracking map
    private val cooldownMap = mutableMapOf<String, Long>()
    private val cooldownTime = TimeUnit.SECONDS.toMillis(5)

    // COMMAND
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // End if sender not player
        if (sender !is Player) { sender.sendMessage("§cOnly players can use this command!"); return true }

        // End if invalid args
        if (args.size < 2) { sender.sendMessage("§cUsage: /pay <player> <amount>"); return true }

        // End if player is on cooldown and doesn't have bypass perm
        if (!sender.hasPermission(Saves.PERM_STAFF) && (System.currentTimeMillis() - (cooldownMap[sender.name] ?: 0L)) < cooldownTime) { sender.sendMessage("§cYou have to wait before using this command again!"); return true }

        // End if player not found
        val target = Bukkit.getPlayer(args[0])
        if (target == null || !target.isOnline) { sender.sendMessage("§cPlayer not found or not online!"); return true }

        // End if target is self
        if (target == sender) { sender.sendMessage("§cYou cannot pay yourself!"); return true }

        // End if target doesn't have perm PERM_ECONOMY_RECEIVE_PAYMENTS
        if (!target.hasPermission(Saves.PERM_ECONOMY_RECEIVE_MONEY)) { sender.sendMessage("§c${target.name} doesn't have permission to receive money!"); return true }

        // End if amount is invalid
        val amountLong = args[1].toLongOrNull()
        if (amountLong == null || amountLong <= 0) { sender.sendMessage("§cInvalid amount! You must enter a positive whole number."); return true }

        // End if balance is under paid amount
        val senderBalance = EconomyUtils.getPlayerBalance(sender)
        if (amountLong > senderBalance) { sender.sendMessage("§cYou do not have enough money to pay $amountLong${Economy.MONEY_SYMBOL}${if (senderBalance < 0) " ${Economy.TXT_IN_DEBT}" else ""}"); return true }

        // Perform the transaction
        val senderUpdatedBalance = senderBalance - amountLong
        val targetBalance = EconomyUtils.getPlayerBalance(target)
        val targetUpdatedBalance = targetBalance + amountLong
        EconomyUtils.setPlayerBalance(sender, senderUpdatedBalance)
        EconomyUtils.setPlayerBalance(target, targetUpdatedBalance)

        // Alert players
        sender.sendMessage("§c[${Economy.MONEY_SYMBOL}➖] §7You paid §f${target.name} §7a total of ${EconomyUtils.formatMoney(amountLong)} §7(new balance ${EconomyUtils.formatMoney(senderUpdatedBalance)}§7)")
        target.sendMessage("§a[${Economy.MONEY_SYMBOL}➕] §7You received ${EconomyUtils.formatMoney(amountLong)} §7from §f${sender.name} §7(new balance ${EconomyUtils.formatMoney(targetUpdatedBalance)}§7)")

        // Update players balances files stats
        EconomyUtils.updatePlayerBalanceFileKeyLong(sender, 1, setOf(Economy.KEY_PAYMENT_SENT, Economy.KEY_PAYMENT_SENT_SLT))
        EconomyUtils.updatePlayerBalanceFileKeyLong(target, 1, setOf(Economy.KEY_PAYMENT_RECEIVED, Economy.KEY_PAYMENT_RECEIVED_SLT))
        EconomyUtils.updatePlayerBalanceFileKeyLong(target, amountLong, setOf(Economy.KEY_PROFIT_SLT))

        // Log
        LogsManager.log(Saves.LOG_FILE_ECONOMY, "Economy", "[Pay Command - ${sender.name}] ${sender.name} ([-] $senderBalance -> $senderUpdatedBalance) paid ${target.name} ([+] $targetBalance -> $targetUpdatedBalance) an amount of $amountLong")

        // Update cooldown map
        cooldownMap[sender.name] = System.currentTimeMillis()

        return true
    }

    // TAB COMPLETE
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        return when (args.size) {
            1 -> Bukkit.getOnlinePlayers().map { it.name }.filter { it.startsWith(args[0], true) }
            2 -> listOf("<amount>")
            else -> emptyList()
        }
    }
}
