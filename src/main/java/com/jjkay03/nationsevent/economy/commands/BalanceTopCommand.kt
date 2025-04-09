package com.jjkay03.nationsevent.economy.commands

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.economy.Economy
import com.jjkay03.nationsevent.economy.EconomyUtils
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class BalanceTopCommand : CommandExecutor, TabCompleter {

    private val sortedPlayerBalancesMap: MutableMap<OfflinePlayer, Long> = mutableMapOf()

    // COMMAND
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        // Get all players balances and sort them
        EconomyUtils.getAllPlayersBalancesAsync()
        sortPlayerBalancesAsync(sortedPlayerBalancesMap)

        sender.sendMessage("§7Sorting top balances...")

        // Schedule this to run after balances are loaded
        Bukkit.getScheduler().runTaskLater(NationsEvent.INSTANCE, Runnable {
            // Args
            val displayCount = when {
                args.isEmpty() -> 10                                            // Default to 10 if no args
                args[0].equals("all", ignoreCase = true) -> Int.MAX_VALUE // Display all entries
                args[0].toIntOrNull() != null -> args[0].toInt()                // Custom number
                else -> 10                                                      // Default to 10 for invalid input
            }
            val isAll = args.isNotEmpty() && args[0].equals("all", ignoreCase = true)

            // Display top balances
            val playersToDisplay = sortedPlayerBalancesMap.entries.take(displayCount)
            val totalMoney = sortedPlayerBalancesMap.values.filter { it > 0 }.sum()

            // Send header
            sender.sendMessage(" ")
            if (isAll) sender.sendMessage("§e§l==== ALL BALANCES ====") else sender.sendMessage("§e§l==== TOP $displayCount BALANCES ====")
            sender.sendMessage("§8Total: §7Players: §f${sortedPlayerBalancesMap.size} §8| §7Money: ${EconomyUtils.formatMoney(totalMoney)}")

            // Display the players and their balances
            var position = 1
            playersToDisplay.forEach { (player, balance) ->
                // Get the player name correctly
                val playerName = when {
                    player.name != null -> player.name
                    player.isOnline -> player.player?.name
                    else -> Bukkit.getOfflinePlayer(player.uniqueId).name ?: player.uniqueId.toString()
                }
                sender.sendMessage("§6#$position §f$playerName : §a${EconomyUtils.formatMoney(balance)}")
                position++
            }

            // Send footer
            if (isAll) sender.sendMessage("§e§l=====================") else sender.sendMessage("§e§l========================")
            sender.sendMessage(" ")

        }, 20L) // Wait a second to ensure the async operations complete

        return true
    }

    // TAB COMPLETE
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String> {
        val completions = mutableListOf<String>()
        if (args.size == 1) {
            val input = args[0].toLowerCase()
            if ("all".startsWith(input)) completions.add("all")
            val numbers = listOf("5", "10", "20", "50", "100")
            completions.addAll(numbers.filter { it.startsWith(input) })
        }
        return completions
    }

    // Helper function that fills a target map with offline players and sort it by balances from Economy.PLAYERS_BALANCES_MAP
    private fun sortPlayerBalancesAsync(map: MutableMap<OfflinePlayer, Long>) {
        Bukkit.getScheduler().runTaskAsynchronously(NationsEvent.INSTANCE, Runnable {
            map.clear()
            Economy.PLAYERS_BALANCES_MAP
                .map { Bukkit.getOfflinePlayer(it.key) to it.value }
                .sortedByDescending { it.second }
                .forEach { (player, balance) -> map[player] = balance }
        })
    }

}