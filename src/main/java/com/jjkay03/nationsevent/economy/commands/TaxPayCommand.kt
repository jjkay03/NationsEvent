package com.jjkay03.nationsevent.economy.commands

import com.jjkay03.nationsevent.economy.Economy
import com.jjkay03.nationsevent.economy.EconomyTax
import com.jjkay03.nationsevent.economy.EconomyUtils
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class TaxPayCommand : CommandExecutor, TabCompleter {

    // COMMAND
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // End if sender not player
        if (sender !is Player) { sender.sendMessage("§cOnly players can use this command!"); return true }

        // End if tax collection is closed
        if (!EconomyTax.TAX_PAYMENTS_OPEN) { sender.sendMessage("§cTax collection is currently closed!"); return true }

        // End if invalid amount
        val amountLong = args.getOrNull(0)?.toLongOrNull()
        if (amountLong == null || amountLong <= 0) { sender.sendMessage("§cInvalid amount! You must enter a positive whole number."); return true }

        // End if balance is under amount
        val senderBalance = EconomyUtils.getPlayerBalance(sender)
        if (amountLong > senderBalance) { sender.sendMessage("§cYou do not have enough money to pay $amountLong${Economy.MONEY_SYMBOL}${if (senderBalance < 0) " ${Economy.TXT_IN_DEBT}" else ""}"); return true }

        // Save paid amount in tax map
        EconomyTax.setPlayerPaidTaxAmount(sender, amountLong)
        sender.sendMessage("§7[${Economy.MONEY_SYMBOL}➖] You will be paying ${EconomyUtils.formatMoney(amountLong)}§7 on tax collection")

        return true
    }

    // TAB COMPLETE
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        return if (args.size == 1) listOf("<amount>") else emptyList()
    }

}