package com.jjkay03.nationsevent.economy

import com.jjkay03.nationsevent.FilesManager
import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.economy.commands.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

class Economy (private val plugin: JavaPlugin) : Listener {

    companion object {
        // Get config settings
        val FEATURE_ENABLED: Boolean = NationsEvent.INSTANCE.config.getBoolean("economy-enable")
        val MONEY_SYMBOL: String = NationsEvent.INSTANCE.config.getString("economy-money-symbol").toString()
        val MONEY_COLOR: String = NationsEvent.INSTANCE.config.getString("economy-money-color").toString()
        val ALLOW_NEGATIVE_BALANCE: Boolean = NationsEvent.INSTANCE.config.getBoolean("economy-allow-negative-balance")

        // Keys used in player balance file yml
        const val KEY_IGN = "ign"
        const val KEY_BALANCE = "balance"
        const val KEY_PAYMENT_SENT = "payment-sent"
        const val KEY_PAYMENT_RECEIVED = "payment-received"
        const val KEY_SOLD_ITEMS = "sold-items"
        const val KEY_PAYMENT_SENT_SLT = "payment-sent-slt"
        const val KEY_PAYMENT_RECEIVED_SLT = "payment-received-slt"
        const val KEY_PROFIT_SLT = "profit-slt"
        const val KEY_SOLD_ITEMS_SLT = "sold-items-slt"
        const val KEY_SOLD_ITEMS_PROFIT_SLT = "sold-items-profit-slt"

        // Variables
        const val MAX_MONEY = 999_999_999_999_999_999L
        const val MIN_MONEY = -999_999_999_999_999_999L
        const val TXT_IN_DEBT = "§4(YOU ARE IN DEBT ☠)"
    }

    init {
        // Load economy feature when plugin starts if enabled
        if (FEATURE_ENABLED) loadEconomy()
    }

    // Function to load the economy (create files, commands, listeners ect)
    private fun loadEconomy() {
        // Log message
        NationsEvent.INSTANCE.logger.info("Loading economy $ $ $")

        // Create default economy directories & files
        FilesManager.createDirectory(Saves.DIR_ECONOMY)
        FilesManager.createDirectory(Saves.DIR_ECONOMY_BALANCES)
        FilesManager.createDirectory(Saves.DIR_ECONOMY_LOGS)
        FilesManager.createFile(Saves.LOG_FILE_ECONOMY)

        // Class variables
        val economyCommand = EconomyCommand()
        val balanceCommand = BalanceCommand()
        val payCommand = PayCommand()
        val rollMoneyCommand = RollMoneyCommand()
        val taxCommand = TaxCommand()
        val taxPayCommand = TaxPayCommand()
        val balanceFileCommand = BalanceFileCommand()

        // Register commands
        plugin.getCommand("economy")?.apply { setExecutor(economyCommand); tabCompleter = economyCommand }
        plugin.getCommand("balance")?.apply { setExecutor(balanceCommand); tabCompleter = balanceCommand }
        plugin.getCommand("pay")?.apply { setExecutor(payCommand); tabCompleter = payCommand }
        plugin.getCommand("rollmoney")?.apply { setExecutor(rollMoneyCommand); tabCompleter = rollMoneyCommand }
        plugin.getCommand("tax")?.apply { setExecutor(taxCommand); tabCompleter = taxCommand }
        plugin.getCommand("taxpay")?.apply { setExecutor(taxPayCommand); tabCompleter = taxPayCommand }
        plugin.getCommand("balancefile")?.apply { setExecutor(balanceFileCommand); tabCompleter = balanceFileCommand }

        // Register events
        plugin.server.pluginManager.registerEvents(this, plugin)
        plugin.server.pluginManager.registerEvents(EconomyItems(), plugin)
    }

    // Create balance file for player when joining the server if it doesn't exist
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        EconomyUtils.createPlayerBalanceFile(event.player, 0)
    }

}