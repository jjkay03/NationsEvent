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

class EconomyCommand : CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.size < 2) { sender.sendMessage("§cUsage: /economy <set/reset/give/take> <player> <amount>"); return true }
        val player = Bukkit.getPlayer(args[1])
        if (player == null) { sender.sendMessage("§cPlayer not found!"); return true }

        when (args[0].lowercase()) {
            // SET
            "set" -> {
                if (args.size < 3 || args[2].toIntOrNull() == null) { sender.sendMessage("§cInvalid amount! Usage: /economy set <player> <amount>"); return true }
                val amount = args[2].toInt()
                val newBalance = EconomyUtils.setPlayerBalance(player, amount)
                if (!Economy.ALLOW_NEGATIVE_BALANCE && amount < 0) sender.sendMessage("§aSet §f${player.name} §abalance to ${EconomyUtils.formatMoney(newBalance)} §7(Negative balance disabled in config)")
                else sender.sendMessage("§aSet §f${player.name} §abalance to ${EconomyUtils.formatMoney(newBalance)}")
                LogsManager.log(Saves.LOG_FILE_ECONOMY, "Economy", "[Economy Command - ${sender.name}] Set ${player.name} balance to $newBalance")
            }

            // RESET
            "reset" -> {
                val newBalance = EconomyUtils.setPlayerBalance(player, 0)
                sender.sendMessage("§aReset §f${player.name} §abalance to ${EconomyUtils.formatMoney(newBalance)}")
                LogsManager.log(Saves.LOG_FILE_ECONOMY, "Economy", "[Economy Command - ${sender.name}] Reset ${player.name} balance to $newBalance")
            }

            // GIVE
            "give" -> {
                if (args.size < 3 || !args[2].toIntOrNull().let { it != null && it > 0 }) { sender.sendMessage("§cInvalid amount! Usage: /economy give <player> <amount>"); return true }
                val amount = args[2].toInt()
                val newBalance = EconomyUtils.setPlayerBalance(player, EconomyUtils.getPlayerBalance(player) + amount)
                sender.sendMessage("§aGave ${EconomyUtils.formatMoney(amount)} §ato §f${player.name} §a(new balance: ${EconomyUtils.formatMoney(newBalance)}§a)")
                LogsManager.log(Saves.LOG_FILE_ECONOMY, "Economy", "[Economy Command - ${sender.name}] Gave $amount to ${player.name} (new balance: $newBalance)")
            }

            // TAKE
            "take" -> {
                if (args.size < 3 || !args[2].toIntOrNull().let { it != null && it > 0 }) { sender.sendMessage("§cInvalid amount! Usage: /economy take <player> <amount>"); return true }
                val amount = args[2].toInt()
                val newBalance = EconomyUtils.setPlayerBalance(player, EconomyUtils.getPlayerBalance(player) - amount)
                sender.sendMessage("§aTook ${EconomyUtils.formatMoney(amount)} §afrom §f${player.name} §a(new balance: ${EconomyUtils.formatMoney(newBalance)}§a)")
                LogsManager.log(Saves.LOG_FILE_ECONOMY, "Economy", "[Economy Command - ${sender.name}] Took $amount from ${player.name} (new balance: $newBalance)")
            }

            else -> sender.sendMessage("§cInvalid subcommand! Use: give, reset, set, take")
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender, command: Command, alias: String, args: Array<out String>
    ): List<String> {
        return when (args.size) {
            1 -> listOf("set", "reset", "give", "take").filter { it.startsWith(args[0], true) }
            2 -> Bukkit.getOnlinePlayers().map { it.name }.filter { it.startsWith(args[1], true) }
            3 -> if (args[0] in listOf("set", "give", "take")) listOf("<amount>") else emptyList()
            else -> emptyList()
        }
    }
}
