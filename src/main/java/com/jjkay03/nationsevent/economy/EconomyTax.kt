package com.jjkay03.nationsevent.economy

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.time.Instant

data class EconomyPlayerTaxData(
    var updateTime: Instant = Instant.now(),
    var balance: Long = 0,
    var paymentSentSLT: Long = 0,
    var paymentReceivedSLT: Long = 0,
    var profitSLT: Long = 0,
    var soldItemsSLT: Long = 0,
    var soldItemsProfitSLT: Long = 0,
    var dueTaxPercentage: Int = 0,
    var dueTaxAmount: Long = 0,
    var paidTaxAmount: Long = 0,
    var taxFraud: Boolean = false,
)

object EconomyTax {

    val PLAYER_TAX_DATA_MAP = mutableMapOf<Player, EconomyPlayerTaxData>()

    private const val TAX_RATE_BASE = 15                          // 15%
    private const val TAX_RATE_PAYMENT_SENT_OVER_AVERAGE = 2      // 2%
    private const val TAX_RATE_PAYMENT_RECEIVED_OVER_AVERAGE = 5  // 5%
    private const val TAX_RATE_SOLD_ITEMS_OVER_AVERAGE = 3        // 3%
    private val TAX_RATE_BONUS_RANGE = 0..4                 // 0% -> 4%

    private var AVERAGE_PAYMENT_SENT: Long  = 0
    private var AVERAGE_PAYMENT_RECEIVED: Long  = 0
    private var AVERAGE_SOLD_ITEMS: Long  = 0

    var TOTAL_DUE_TAX: Long = 0


    // Function to update the player tax data map
    fun calculateTaxes() {
        updateTaxMapStats() // Collect payment stats from players balances files to map
        calculateAverages() // Calculate averages
        updateTaxMapDueTaxes() // Calculate taxes and update tax map
    }

    // Function to collect the tax from all players
    fun collectTaxes() {
        // TODO
    }

    // Helper function to update data in map from players balances files
    private fun updateTaxMapStats() {
        for (player in Bukkit.getOnlinePlayers()) {
            // Add player to map
            val playerTaxData = PLAYER_TAX_DATA_MAP.getOrPut(player) { EconomyPlayerTaxData() }

            // Update stats
            playerTaxData.balance = EconomyUtils.getPlayerBalance(player)
            playerTaxData.paymentSentSLT = EconomyUtils.getPlayerBalanceFileKeyLong(player, Economy.KEY_PAYMENT_SENT_SLT)
            playerTaxData.paymentReceivedSLT = EconomyUtils.getPlayerBalanceFileKeyLong(player, Economy.KEY_PAYMENT_RECEIVED_SLT)
            playerTaxData.profitSLT = EconomyUtils.getPlayerBalanceFileKeyLong(player, Economy.KEY_PROFIT_SLT)
            playerTaxData.soldItemsSLT = EconomyUtils.getPlayerBalanceFileKeyLong(player, Economy.KEY_SOLD_ITEMS_SLT)
            playerTaxData.soldItemsProfitSLT = EconomyUtils.getPlayerBalanceFileKeyLong(player, Economy.KEY_SOLD_ITEMS_PROFIT_SLT)
        }
    }

    // Helper function to update tax map with due tax
    private fun updateTaxMapDueTaxes() {
        TOTAL_DUE_TAX = 0
        for (player in Bukkit.getOnlinePlayers()) {
            // Get player from map
            val playerTaxData = PLAYER_TAX_DATA_MAP.getOrPut(player) { EconomyPlayerTaxData() }

            // Calculate tax info
            var taxRate = TAX_RATE_BASE
            if (playerTaxData.paymentSentSLT > AVERAGE_PAYMENT_SENT) taxRate += TAX_RATE_PAYMENT_SENT_OVER_AVERAGE
            if (playerTaxData.paymentReceivedSLT > AVERAGE_PAYMENT_RECEIVED) taxRate += TAX_RATE_PAYMENT_RECEIVED_OVER_AVERAGE
            if (playerTaxData.soldItemsSLT > AVERAGE_SOLD_ITEMS) taxRate += TAX_RATE_SOLD_ITEMS_OVER_AVERAGE
            taxRate += TAX_RATE_BONUS_RANGE.random()
            val dueTax = (playerTaxData.profitSLT * taxRate / 100)

            // Update tax info
            playerTaxData.dueTaxPercentage = taxRate
            playerTaxData.dueTaxAmount = dueTax

            // Add to total due tax
            TOTAL_DUE_TAX += dueTax
        }
    }

    // Helper function to update averages variables based on data in map
    private fun calculateAverages() {
        val playerCount = PLAYER_TAX_DATA_MAP.size
        if (playerCount == 0) return
        var totalPaymentSent = 0L
        var totalPaymentReceived = 0L
        var totalSoldItems = 0L
        for (data in PLAYER_TAX_DATA_MAP.values) {
            totalPaymentSent += data.paymentSentSLT
            totalPaymentReceived += data.paymentReceivedSLT
            totalSoldItems += data.soldItemsSLT
        }
        AVERAGE_PAYMENT_SENT = (totalPaymentSent / playerCount).toLong()
        AVERAGE_PAYMENT_RECEIVED = (totalPaymentReceived / playerCount).toLong()
        AVERAGE_SOLD_ITEMS = (totalSoldItems / playerCount).toLong()
    }

}