package com.jjkay03.nationsevent.economy

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.utils.LogsManager
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.util.UUID

object EconomyUtils {

    // Enum for different money format types
    enum class MoneyFormat {
        FULL,                // 5000$
        SHORTEN,             // 5K$
        SHORTEN_NUMBER_ONLY, // 5
        SHORTEN_LETTER_ONLY  // K
    }

    // Function that returns the player balance file of a given player
    fun getPlayerBalanceFile(player: Player): File { return File(Saves.DIR_ECONOMY_BALANCES,"${player.uniqueId}.yml") }

    // Function to create player yml containing player balance
    fun createPlayerBalanceFile(player: Player, startingBalance: Long) {
        val file = getPlayerBalanceFile(player)
        if (file.exists()) return // End if file exist

        // Logs
        NationsEvent.INSTANCE.logger.info("Creating player ${player.name} balance file with starting balance $startingBalance")
        LogsManager.log(Saves.LOG_FILE_ECONOMY, "Economy", "Creating player ${player.name} balance file with starting balance $startingBalance")

        // Create file
        val config = YamlConfiguration().apply {
            set(Economy.KEY_IGN, player.name)
            set(Economy.KEY_BALANCE, startingBalance)
            setOf(
                Economy.KEY_PAYMENT_SENT,
                Economy.KEY_PAYMENT_RECEIVED,
                Economy.KEY_SOLD_ITEMS,
                Economy.KEY_PAYMENT_SENT_SLT,
                Economy.KEY_PAYMENT_RECEIVED_SLT,
                Economy.KEY_PROFIT_SLT,
                Economy.KEY_SOLD_ITEMS_SLT,
                Economy.KEY_SOLD_ITEMS_PROFIT_SLT
            ).forEach { set(it, 0) }
            save(file)
        }
    }

    // Function that updates a set of long values in a player balance file by adding a specified amount to each key
    fun updatePlayerBalanceFileKeyLong(player: Player, amount: Long, keys: Set<String>) {
        val file = getPlayerBalanceFile(player)
        val config = YamlConfiguration.loadConfiguration(file)
        keys.forEach { key -> config.set(key, config.getLong(key) + amount) }
        config.save(file)
    }

    // Function that sets a set of long values in a player balance file by setting each key to a specific amount
    fun setPlayerBalanceFileKeyLong(player: Player, amount: Long, keys: Set<String>) {
        val file = getPlayerBalanceFile(player)
        val config = YamlConfiguration.loadConfiguration(file)
        keys.forEach { key -> config.set(key, amount) }
        config.save(file)
    }

    // Function that gets a long value from a player balance file assigned to a key
    fun getPlayerBalanceFileKeyLong(player: Player, key: String) : Long {
        val file = getPlayerBalanceFile(player)
        val config = YamlConfiguration.loadConfiguration(file)
        return config.getLong(key)
    }

    // Function that gets all player balances from balances folder into Economy.PLAYERS_BALANCES_MAP
    fun getAllPlayersBalancesAsync(map: MutableMap<UUID, Long> = Economy.PLAYERS_BALANCES_MAP) {
        Bukkit.getScheduler().runTaskAsynchronously(NationsEvent.INSTANCE, Runnable {
            Saves.DIR_ECONOMY_BALANCES.listFiles { file -> file.extension == "yml" }?.forEach { file ->
                val uuid = runCatching { UUID.fromString(file.nameWithoutExtension) }.getOrNull() ?: return@forEach
                val config = YamlConfiguration.loadConfiguration(file)
                val balance = config.getLong(Economy.KEY_BALANCE)
                synchronized(map) { map[uuid] = balance }
            }
        })
    }

    // Function that returns the balance of a given player
    fun getPlayerBalance(player: Player): Long {
        val file = getPlayerBalanceFile(player)
        val config = YamlConfiguration.loadConfiguration(file)
        return config.getLong(Economy.KEY_BALANCE)
    }

    // Function to set a player's balance
    fun setPlayerBalance(player: Player, newBalance: Long): Long {
        val file = getPlayerBalanceFile(player)
        val config = YamlConfiguration.loadConfiguration(file)

        // Clamp to min/max | Round down safely whole number | Prevent negative balance if disabled
        var updatedBalance = newBalance.coerceIn(Economy.MIN_MONEY, Economy.MAX_MONEY)
        if (!Economy.ALLOW_NEGATIVE_BALANCE && updatedBalance < 0) updatedBalance = 0

        // Set new balance
        config.set(Economy.KEY_BALANCE, updatedBalance)
        config.save(file)
        LogsManager.log(Saves.LOG_FILE_ECONOMY, "Economy", "Updated player ${player.name} balance to $updatedBalance")
        return updatedBalance
    }

    // Function that formats money for messages
    fun formatMoney(amount: Long, format: MoneyFormat = MoneyFormat.FULL): String {
        val absAmount = kotlin.math.abs(amount)

        // Determine suffix and divisor based on amount
        val (suffix, divisor) = when {
            absAmount >= 1_000_000_000_000_000L -> "Q" to 1_000_000_000_000_000L  // Quadrillion
            absAmount >= 1_000_000_000_000L -> "T" to 1_000_000_000_000L          // Trillions
            absAmount >= 1_000_000_000L -> "B" to 1_000_000_000L                  // Billions
            absAmount >= 1_000_000L -> "M" to 1_000_000L                          // Millions
            absAmount >= 1_000L -> "K" to 1_000L                                  // Thousands
            else -> "" to 1L
        }

        // Format based on the selected format type
        val formatted = when (format) {
            MoneyFormat.FULL -> absAmount.toString() + Economy.MONEY_SYMBOL
            MoneyFormat.SHORTEN -> if (suffix.isEmpty()) absAmount.toString() + Economy.MONEY_SYMBOL else "${(absAmount / divisor)}$suffix${Economy.MONEY_SYMBOL}"
            MoneyFormat.SHORTEN_LETTER_ONLY -> if (suffix.isEmpty()) "" else suffix
            MoneyFormat.SHORTEN_NUMBER_ONLY -> if (suffix.isEmpty()) absAmount.toString() else (absAmount / divisor).toString()
        }

        // Add color except for LETTER_ONLY which has no color
        return when {
            format == MoneyFormat.SHORTEN_LETTER_ONLY -> formatted
            format == MoneyFormat.SHORTEN_NUMBER_ONLY -> formatted
            amount < 0 -> "§c-$formatted"
            else -> "${Economy.MONEY_COLOR}$formatted"
        }
    }

}