package com.jjkay03.nationsevent.economy

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.utils.LogsManager
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.awt.Color
import java.io.File
import kotlin.math.abs

object EconomyUtils {

    // Function that returns the player balance file of a given player
    private fun getPlayerBalanceFile(player: Player): File { return File(Saves.DIR_ECONOMY_BALANCES,"${player.uniqueId}.yml") }

    // Function that formats money for messages
    fun formatMoney(amount: Long, shorten: Boolean = false): String {
        val absAmount = kotlin.math.abs(amount)
        val formatted = if (shorten) {
            when {
                absAmount >= 1_000_000_000_000_000 -> "${absAmount / 1_000_000_000_000_000}Q" // Quadrillion
                absAmount >= 1_000_000_000_000 -> "${absAmount / 1_000_000_000_000}T" // Trillion
                absAmount >= 1_000_000_000 -> "${absAmount / 1_000_000_000}B" // Billion
                absAmount >= 1_000_000 -> "${absAmount / 1_000_000}M" // Million
                absAmount >= 1_000 -> "${absAmount / 1_000}K" // Thousand
                else -> absAmount.toString() // No abbreviation needed
            }
        } else absAmount.toString()

        // Return formatted string with color and symbol
        return if (amount < 0) "§c$formatted${Economy.MONEY_SYMBOL}"
        else "${Economy.MONEY_COLOR}$formatted${Economy.MONEY_SYMBOL}"
    }

    // Function to create player yml containing player balance
    fun createPlayerBalanceFile(player: Player, startingBalance: Long) {
        val file = getPlayerBalanceFile(player)
        if (file.exists()) return // End if file exist
        NationsEvent.INSTANCE.logger.info("Creating player ${player.name} balance file with starting balance $startingBalance")
        LogsManager.log(Saves.LOG_FILE_ECONOMY, "Economy", "Creating player ${player.name} balance file with starting balance $startingBalance")
        val config = YamlConfiguration()
        config.set(Economy.PLAYER_BALANCE_FILE_KEY_IGN, player.name)
        config.set(Economy.PLAYER_BALANCE_FILE_KEY_BALANCE, startingBalance)
        config.save(file)
    }

    // Function that returns the balance of a given player
    fun getPlayerBalance(player: Player): Long {
        val file = getPlayerBalanceFile(player)
        val config = YamlConfiguration.loadConfiguration(file)
        return config.getLong(Economy.PLAYER_BALANCE_FILE_KEY_BALANCE)
    }

    // Function to set a player's balance
    fun setPlayerBalance(player: Player, newBalance: Long): Long {
        val file = getPlayerBalanceFile(player)
        val config = YamlConfiguration.loadConfiguration(file)

        // Clamp to min/max | Round down safely whole number | Prevent negative balance if disabled
        var updatedBalance = newBalance.coerceIn(Economy.MIN_MONEY, Economy.MAX_MONEY)
        if (!Economy.ALLOW_NEGATIVE_BALANCE && updatedBalance < 0) updatedBalance = 0

        // Set new balance
        config.set(Economy.PLAYER_BALANCE_FILE_KEY_BALANCE, updatedBalance)
        config.save(file)
        LogsManager.log(Saves.LOG_FILE_ECONOMY, "Economy", "Updated player ${player.name} balance to $updatedBalance")
        return updatedBalance
    }


}