package com.jjkay03.nationsevent.economy.commands

import com.jjkay03.nationsevent.economy.Economy
import com.jjkay03.nationsevent.economy.EconomyTax
import com.jjkay03.nationsevent.economy.EconomyUtils
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class TaxCommand : CommandExecutor, TabCompleter {

    // COMMAND
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        // End if no args
        if (args.isEmpty()) { sender.sendMessage("§cUsage: /tax <calculate/collect>"); return false }

        when (args[0].lowercase()) {
            // Calculate tax
            "calculate" -> {
                sender.sendMessage("§7Calculating tax data for all players online...")
                EconomyTax.calculateTaxes()
                sender.sendMessage("§6Calculated tax data for all players online - ${EconomyTax.PLAYER_TAX_DATA_MAP.size} players due total of ${EconomyUtils.formatMoney(EconomyTax.TOTAL_DUE_TAX)}")
            }

            // Collect tax
            "collect" -> {
                if (EconomyTax.PLAYER_TAX_DATA_MAP.isEmpty()) { sender.sendMessage("§cNo tax data available! Use: /tax calculate first"); return false }
                if (args.getOrNull(1)?.equals("confirm", ignoreCase = true) == true) // TODO Handle confirmation logic
                else sender.sendMessage("Usage: /tax collect CONFIRM")
            }
        }
        return true
    }

    // TAB COMPLETE
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        if (args.size == 1) { return listOf("calculate", "collect").filter { it.startsWith(args[0], ignoreCase = true) } }
        return emptyList()
    }
}
