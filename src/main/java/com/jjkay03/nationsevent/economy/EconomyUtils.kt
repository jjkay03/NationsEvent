package com.jjkay03.nationsevent.economy

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.utils.LogsManager
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.awt.Color
import java.io.File

object EconomyUtils {

    // Function that returns the player balance file of a given player
    private fun getPlayerBalanceFile(player: Player): File { return File(Saves.DIR_ECONOMY_BALANCES,"${player.uniqueId}.yml") }

    // Function that formats money for messages
    fun formatMoney(amount: Int) : String {
        return if (amount < 0) ("§c" + amount + Economy.MONEY_SYMBOL)
        else (Economy.MONEY_COLOR + amount + Economy.MONEY_SYMBOL)
    }

    // Function to create player yml containing player balance
    fun createPlayerBalanceFile(player: Player, startingBalance: Int) {
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
    fun getPlayerBalance(player: Player): Int {
        val file = getPlayerBalanceFile(player)
        val config = YamlConfiguration.loadConfiguration(file)
        return config.getInt(Economy.PLAYER_BALANCE_FILE_KEY_BALANCE)
    }

    // Function to set a player's balance
    fun setPlayerBalance(player: Player, newBalance: Int): Int {
        val file = getPlayerBalanceFile(player)
        val config = YamlConfiguration.loadConfiguration(file)
        val updatedBalance = if (!Economy.ALLOW_NEGATIVE_BALANCE && newBalance < 0) 0 else newBalance
        config.set(Economy.PLAYER_BALANCE_FILE_KEY_BALANCE, updatedBalance)
        config.save(file)
        LogsManager.log(Saves.LOG_FILE_ECONOMY, "Economy", "Updated player ${player.name} balance to $updatedBalance")
        return updatedBalance
    }

}