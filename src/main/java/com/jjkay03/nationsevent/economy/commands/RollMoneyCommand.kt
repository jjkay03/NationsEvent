package com.jjkay03.nationsevent.economy.commands

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.economy.Economy
import com.jjkay03.nationsevent.economy.EconomyUtils
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import kotlin.random.Random

class RollMoneyCommand : CommandExecutor, TabCompleter {

    private var lastRoll: List<Pair<Player, Long>>? = null
    private var lastMin: Long = 0
    private var lastMax: Long = 0

    // COMMAND
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // End if wrong arg
        if (args.isEmpty()) { sender.sendMessage("§cUsage: /rollmoney <min> <max> or /rollmoney confirm"); return true }

        // ARGUMENT: confirm
        if (args[0].equals("confirm", ignoreCase = true)) {
            // End lastRoll is empty
            if (lastRoll == null) { sender.sendMessage("§cNo roll to confirm. Use /rollmoney <min> <max> first."); return true }

            // Roll money
            rollMoney(lastMin, lastMax, lastRoll!!)
            sender.sendMessage("§6Money roll CONFIRMED sending money to all players!")
            lastRoll = null
            return true
        }

        // ARGUMENT: get min and max
        val min = args.getOrNull(0)?.toLongOrNull()
        val max = args.getOrNull(1)?.toLongOrNull()

        // End if mix & max invalid
        if (min == null || max == null || max < min) { sender.sendMessage("§cInvalid min/max input!"); return true }

        // Get online players or end if no online players
        val onlinePlayers = Bukkit.getOnlinePlayers()
        if (onlinePlayers.isEmpty()) { sender.sendMessage("§cNo players online to roll money for!"); return true }

        // Save last min and last max
        lastMin = min; lastMax = max

        // Roll money - amounts rolled can only be multiples of 5 (to look clean)
        val rolls = onlinePlayers.map { player -> player to generateRandomMultipleOfFive(min, max) }
        lastRoll = rolls

        // Send roll results to command sender before confirmation
        sender.sendMessage("")
        sender.sendMessage("§6§lMONEY ROLL RESULTS:")
        sender.sendMessage("")
        val rollLine = rolls
            .sortedBy { it.first.name.lowercase() }
            .joinToString(" §8§l|§r ") { (player, amount) ->
                "§f${player.name}§7:${EconomyUtils.formatMoney(amount)}"
            }
        sender.sendMessage(rollLine)
        sender.sendMessage("")
        sender.sendMessage("§fType §e/rollmoney confirm §fto apply this roll.")
        sender.sendMessage("")

        return true
    }

    // TAB COMPLETE
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        return when (args.size) {
            1 -> { listOf("CONFIRM", "<min>").filter { it.startsWith(args[0], ignoreCase = true) } }
            2 -> { listOf("<max>").filter { it.startsWith(args[1], ignoreCase = true) } }
            else -> emptyList()
        }
    }

    // Function to roll money to players
    private fun rollMoney(min: Long, max: Long, roll: List<Pair<Player, Long>>) {
        roll.forEach { (player, finalAmount) ->
            object : BukkitRunnable() {
                var ticks = 0
                val totalDurationTicks = 10 * 20 // 10 seconds
                override fun run() {
                    if (ticks >= totalDurationTicks) {
                        // Final title with the actual rolled amount
                        player.sendMessage("§6You received an amount of ${EconomyUtils.formatMoney(finalAmount)} §6from money roll!")
                        player.sendTitle(("§6"+finalAmount+Economy.MONEY_SYMBOL), "", 10, 80, 10)
                        player.playSound(player.location, Sound.BLOCK_BEACON_POWER_SELECT, 1f, 1f)
                        EconomyUtils.setPlayerBalance(player, EconomyUtils.getPlayerBalance(player) + finalAmount)
                        cancel()
                        return
                    }
                    val randomDisplayAmount = generateRandomMultipleOfFive(min, max)
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

}
