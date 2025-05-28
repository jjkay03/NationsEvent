package com.jjkay03.nationsevent.economy.commands

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.economy.Economy
import com.jjkay03.nationsevent.economy.EconomyUtils
import com.jjkay03.nationsevent.utils.LogsManager
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class RollMoneyCommand : CommandExecutor, TabCompleter {

    private var lastRoll: List<Pair<Player, Long>>? = null
    private var lastMin: Long = 0
    private var lastMax: Long = 0

    // COMMAND
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // End if no args
        if (args.isEmpty()) { sender.sendMessage("§cUsage: /rollmoney <min> <max>, /rollmoney confirm, /rollmoney view, or /rollmoney set <player> <amount>"); return true }

        // ARGUMENT: confirm
        if (args[0].equals("confirm", ignoreCase = true)) {
            if (lastRoll == null) {
                sender.sendMessage("§cNo roll to confirm. Use /rollmoney <min> <max> first.")
                return true
            }
            rollMoney(sender)
            sender.sendMessage("§6Money roll CONFIRMED sending money to all players!")
            lastRoll = null
            return true
        }

        // ARGUMENT: view
        if (args[0].equals("view", ignoreCase = true)) { displayRollResults(sender); return true }

        // ARGUMENT: set
        if (args[0].equals("set", ignoreCase = true)) {
            // End if: Invalid, No roll, Player not found, Invalid amount
            if (args.size < 3) { sender.sendMessage("§cUsage: /rollmoney set <player> <amount>"); return true }
            if (lastRoll == null) { sender.sendMessage("§cNo roll to modify, use /rollmoney <min> <max> first!"); return true }
            val targetPlayer = Bukkit.getPlayerExact(args[1])
            if (targetPlayer == null) { sender.sendMessage("§cPlayer '${args[1]}' not found or not online."); return true }
            val amount = args[2].toLongOrNull()
            if (amount == null) { sender.sendMessage("§cInvalid amount!"); return true }
            // Modify the lastRoll value for this player
            lastRoll = lastRoll!!.map { (player, value) -> if (player == targetPlayer) player to amount else player to value }
            sender.sendMessage("§aUpdated roll amount for §f${targetPlayer.name}§a to ${EconomyUtils.formatMoney(amount)}")
            return true
        }

        // ARGUMENT: <min> <max>
        val min = args.getOrNull(0)?.toLongOrNull()
        val max = args.getOrNull(1)?.toLongOrNull()

        // End if min/max invalid or no players online.
        if (min == null || max == null || max < min) { sender.sendMessage("§cInvalid min/max input!"); return true }
        val eligiblePlayers = Bukkit.getOnlinePlayers().filter { it.hasPermission(Saves.PERM_ECONOMY_RECEIVE_MONEY) }
        if (eligiblePlayers.isEmpty()) { sender.sendMessage("§cNo players online to roll money for!"); return true }

        lastMin = min
        lastMax = max
        lastRoll = eligiblePlayers.map { player -> player to generateRandomMultipleOfFive(min, max) }

        displayRollResults(sender)
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        return when (args.size) {
            1 -> listOf("CONFIRM", "view", "set", "<min>").filter { it.startsWith(args[0], ignoreCase = true) }
            2 -> if (args[0].equals("set", ignoreCase = true)) {
                Bukkit.getOnlinePlayers().map { it.name }.filter { it.startsWith(args[1], ignoreCase = true) }
            } else {
                listOf("<max>").filter { it.startsWith(args[1], ignoreCase = true) }
            }
            else -> emptyList()
        }
    }

    // Function to roll money to players
    private fun rollMoney(commandSender: CommandSender) {
        lastRoll?.forEach { (player, finalAmount) ->
            object : BukkitRunnable() {
                var ticks = 0
                val totalDurationTicks = 10 * 20 // 10 seconds
                override fun run() {
                    if (ticks >= totalDurationTicks) {
                        // Final title with the actual rolled amount
                        val playerBalance = EconomyUtils.getPlayerBalance(player)
                        val updatedPlayerBalance = playerBalance + finalAmount
                        EconomyUtils.setPlayerBalance(player, updatedPlayerBalance)
                        player.sendMessage("§6You received an amount of ${EconomyUtils.formatMoney(finalAmount)} §6from money roll!")
                        player.sendTitle(("§6"+finalAmount+Economy.MONEY_SYMBOL), "", 10, 80, 10)
                        player.playSound(player.location, Sound.BLOCK_BEACON_POWER_SELECT, 1f, 1f)
                        LogsManager.log(Saves.LOG_FILE_ECONOMY, "Economy", "[Moneyroll Command - ${commandSender.name}] ${player.name} ($playerBalance -> $updatedPlayerBalance) received $finalAmount from moneyroll")
                        cancel()
                        return
                    }
                    val randomDisplayAmount = generateRandomMultipleOfFive(lastMin, lastMax)
                    player.sendTitle(EconomyUtils.formatMoney(randomDisplayAmount), "", 0, 20, 0)
                    player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1f, 1f)
                    ticks += 6 // every 6 ticks = ~0.3s
                }
            }.runTaskTimer(NationsEvent.INSTANCE, 0L, 6L)
        }
    }

    // Utility function to generate random multiples of 5 between min and max
    private fun generateRandomMultipleOfFive(min: Long, max: Long): Long {
        val multiples = (min..max).filter { it % 5 == 0L }
        return if (multiples.isNotEmpty()) multiples.random() else min
    }

    // Utility function to display roll in chat
    private fun displayRollResults(sender: CommandSender) {
        val roll = lastRoll ?: run { sender.sendMessage("§cNo roll has been made yet!"); return }
        sender.sendMessage("")
        sender.sendMessage("§6§l§nMONEY ROLL RESULTS:")
        sender.sendMessage("")
        val rollLine = roll
            .sortedBy { it.first.name.lowercase() }
            .joinToString(" §8§l|§r ") { (player, amount) ->
                "§f${player.name}§7:${EconomyUtils.formatMoney(amount)}"
            }
        sender.sendMessage(rollLine)
        sender.sendMessage("")
        sender.sendMessage("§fType §e/rollmoney confirm §fto apply this roll.")
        sender.sendMessage("")
    }

}
