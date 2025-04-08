package com.jjkay03.nationsevent.economy

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.Utils
import com.jjkay03.nationsevent.utils.LogsManager
import com.jjkay03.nationsevent.utils.Webhook
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import org.bukkit.scheduler.BukkitRunnable
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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

enum class EconomyTaxValidity { VALID, OVERPAID, UNDERPAID, UNKNOWN }

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

    var TAX_PAYMENTS_OPEN: Boolean = false
    var TOTAL_DUE_TAX: Long = 0
    private var TOTAL_COLLECTED_TAX: Long = 0
    private var TAX_COLLECTION_DURATION_MINUTES: Int = 1


    // Function to update the player tax data map
    fun calculateTaxes() {
        updateTaxMapStats() // Collect payment stats from players balances files to map
        calculateAverages() // Calculate averages
        updateTaxMapDueTaxes() // Calculate taxes and update tax map
    }

    // Function to collect the tax from all players
    fun collectTaxes() {
        // Alert players of tax collection
        Utils.sendTitleToAllPlayers("§e${Economy.MONEY_SYMBOL} TAX COLLECTION ${Economy.MONEY_SYMBOL}", "§7Info in chat", stay = 80)
        Utils.playSoundToAllPlayers(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 0f)
        val taxAlertMessage = setOf(
            "\n§e⚠ §7Tax collection has started, you have received a \"§fTax Records§7\" book in your inventory.",
            "§7Read the book and calculate how much tax you are due. You have §e$TAX_COLLECTION_DURATION_MINUTES minutes §7to pay.",
            "§7You can pay your due tax using §e/taxpay <amount>§7.\n§r " )
        for (line in taxAlertMessage) Utils.messageAllPlayers(line)
        giveTaxRecordsToAllPlayers() // Give all players tax records
        TAX_PAYMENTS_OPEN = true // Open tax payment

        // Run timer and pay taxes once done
        runTimer(TAX_COLLECTION_DURATION_MINUTES) {
            payTaxes() // Make all players pay taxes
            Utils.messageStaff("§6COLLECTED TAXES - ${PLAYER_TAX_DATA_MAP.size} players collected total of ${EconomyUtils.formatMoney(TOTAL_COLLECTED_TAX)}")
            TAX_PAYMENTS_OPEN = false // Close tax payment
            TOTAL_DUE_TAX = 0 // Reset
            TOTAL_COLLECTED_TAX = 0 // Reset
        }
    }

    // Function to save how much a player set aside to pay due tax
    fun setPlayerPaidTaxAmount(player: Player, amount: Long) {
        val playerTaxData = PLAYER_TAX_DATA_MAP[player] ?: return
        playerTaxData.paidTaxAmount = amount
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

    // Helper function to start tax collection timer
    private fun runTimer(duration: Int, onFinish: () -> Unit) {
        val intervalSeconds = 60L
        val totalTimeSeconds = duration * 60L
        var timeLeftSeconds = totalTimeSeconds

        object : BukkitRunnable() {
            override fun run() {
                timeLeftSeconds -= intervalSeconds
                if (timeLeftSeconds <= 0) { onFinish(); cancel() }
                else {
                    val minutesLeft = (timeLeftSeconds / 60).toInt()
                    Utils.messageAllPlayers("§e⌚ $minutesLeft minute${if (minutesLeft > 1) "s" else ""} remaining to pay taxes")
                    Utils.playSoundToAllPlayers(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 0f)
                }
            }
        }.runTaskTimer(NationsEvent.INSTANCE, intervalSeconds * 20L, intervalSeconds * 20L)
    }

    // Helper function to give tax record to all players
    private fun giveTaxRecordsToAllPlayers() {
        Bukkit.getOnlinePlayers().forEach { player ->
            val book = createTaxRecordsBook(player)
            val inventory = player.inventory
            val handItem = inventory.itemInMainHand

            // If main hand is empty place book there
            if (handItem.type == Material.AIR) inventory.setItemInMainHand(book)

            // If hand is not empty move item to inv and put book instead
            else if (inventory.firstEmpty() != -1) {
                inventory.setItem(inventory.firstEmpty(), handItem)
                inventory.setItemInMainHand(book)
            }

            // Inventory is full drop the book
            else {
                player.world.dropItemNaturally(player.location, book)
                player.sendMessage("§cYour inventory is full, your Tax Records book has been dropped at your feet.")
            }
        }
    }

    // Helper function to create tax record book
    private fun createTaxRecordsBook(player: Player): ItemStack {
        val playerTaxData = PLAYER_TAX_DATA_MAP.getOrPut(player) { EconomyPlayerTaxData() }
        val book = ItemStack(Material.WRITTEN_BOOK)
        val meta = book.itemMeta as BookMeta
        meta.title = "✉ Tax Records - ${player.name}"
        meta.author = "Nations Revenue Services (NRS)"
        meta.generation = BookMeta.Generation.COPY_OF_COPY
        val page1 = buildString {
            append("§l§nTAX RECORDS§r\n")
            append("\n")
            append("Player: ${player.name}\n")
            append("\n")
            append("Date: ${formatTimeDate(playerTaxData.updateTime)}\n")
            append("Time: ${formatTimeHoursMinutes(playerTaxData.updateTime)}\n")
            append("\n")
            append("§8✉ Official tax records document certified by the NRS.")
        }
        val page2 = buildString {
            append("§l§nRECORDS§r\n")
            append("\n")
            append("• Pay sent: ${playerTaxData.paymentSentSLT}\n")
            append("• Pay received: ${playerTaxData.paymentReceivedSLT}\n")
            append("• Sold items: ${playerTaxData.soldItemsSLT}\n")
            append("\n")
            append("• Items profit: ${playerTaxData.soldItemsProfitSLT}${Economy.MONEY_SYMBOL}\n")
            append("• Profit: §4${playerTaxData.profitSLT}${Economy.MONEY_SYMBOL}§r\n")
            append("\n")
            append("• Tax rate: §4${playerTaxData.dueTaxPercentage}%\n")
            append("\n")
            append("§8* SINCE LAST TAX\n")
        }
        val page3 = buildString {
            append("§l§nFORMALITIES§r\n")
            append("\n")
            append("Your due taxes are calculated based on your revenue, transactions and items sold.\n")
            append("\n")
            append("On the previous page, you can find all of those listed along side your tax rate percentage.\n")
        }
        val page4 = buildString {
            append("§l§nDUE TAXES§r\n")
            append("\n")
            append("You are due to pay your tax rate percentage of your profit:\n")
            append("\n")
            append("→ §4${playerTaxData.dueTaxPercentage}%§r of §4${playerTaxData.profitSLT}${Economy.MONEY_SYMBOL}§r\n")
            append("\n")
            append("Pay your due amount using: §6/taxpay§r.\n")
        }
        val page5 = buildString {
            append("⚠ §l§nCONSEQUENCES§r ⚠\n")
            append("\n")
            append("Failing to pay your due taxes will be considered §4tax fraud§r, and will be met with consequences by the NRS.")
        }
        meta.pages = listOf(page1, page2, page3, page4, page5)
        book.itemMeta = meta
        return book
    }

    // Helper function to format time - date
    private fun formatTimeDate(instant: Instant): String {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        return localDateTime.format(formatter)
    }

    // Helper function to format time - hours & minutes
    private fun formatTimeHoursMinutes(instant: Instant): String {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        return localDateTime.format(formatter)
    }

    // Helper function to make all players on map pay their taxes
    private fun payTaxes() {
        val webhookMessagesLog = mutableListOf<String>()

        // Go through all players in map
        for ((player, playerTaxData) in PLAYER_TAX_DATA_MAP) {
            // Payment
            val playerBalance = EconomyUtils.getPlayerBalance(player)
            val playerUpdatedBalance = playerBalance - playerTaxData.paidTaxAmount
            val missingAmount = (playerTaxData.dueTaxAmount - playerTaxData.paidTaxAmount).coerceAtLeast(0)
            EconomyUtils.setPlayerBalance(player, playerUpdatedBalance)
            TOTAL_COLLECTED_TAX += playerTaxData.paidTaxAmount

            // Check validity of payment
            val validity = when {
                playerTaxData.paidTaxAmount == playerTaxData.dueTaxAmount -> EconomyTaxValidity.VALID
                playerTaxData.paidTaxAmount > playerTaxData.dueTaxAmount -> EconomyTaxValidity.OVERPAID
                playerTaxData.paidTaxAmount < playerTaxData.dueTaxAmount -> EconomyTaxValidity.UNDERPAID
                else -> EconomyTaxValidity.UNKNOWN
            }

            // Update tax fraud in map if needed
            if (validity == EconomyTaxValidity.UNDERPAID) playerTaxData.taxFraud = true

            // Alert player + log
            player.sendMessage("§c[${Economy.MONEY_SYMBOL}➖] §7You paid a total of ${EconomyUtils.formatMoney(playerTaxData.paidTaxAmount)}§7 to cover your taxes (new balance ${EconomyUtils.formatMoney(playerUpdatedBalance)}§7)")
            LogsManager.log(Saves.LOG_FILE_ECONOMY, "Economy", "[TAX] ${player.name} ([-] $playerBalance -> $playerUpdatedBalance) paid ${playerTaxData.paidTaxAmount} to cover taxes ($validity - due taxes were ${playerTaxData.dueTaxAmount} missing $missingAmount).")

            // Webhook
            val colorSignMessage = if (validity == EconomyTaxValidity.VALID || validity == EconomyTaxValidity.OVERPAID ) "+" else "-"
            val playerWebhookMessage = """
                ```diff
                $colorSignMessage ${player.name} $validity TAX - Paid: ${playerTaxData.paidTaxAmount}${Economy.MONEY_SYMBOL}${if (missingAmount > 0) " (Missing amount: $missingAmount${Economy.MONEY_SYMBOL})" else ""}
                • Due tax: ${playerTaxData.dueTaxPercentage}% of ${playerTaxData.profitSLT}${Economy.MONEY_SYMBOL} profit = ${playerTaxData.dueTaxAmount}${Economy.MONEY_SYMBOL} due tax
                • Pay sent: ${playerTaxData.paymentSentSLT} / Pay received: ${playerTaxData.paymentReceivedSLT} / Item sold: ${playerTaxData.soldItemsSLT}
                • Item profit: ${playerTaxData.soldItemsProfitSLT}${Economy.MONEY_SYMBOL} / Profit:  ${playerTaxData.profitSLT}${Economy.MONEY_SYMBOL}
                ```
            """.trimIndent()
            webhookMessagesLog.add(playerWebhookMessage)

            // Reset all SLT stats in player balance file
            EconomyUtils.setPlayerBalanceFileKeyLong(player, 0, setOf(
                Economy.KEY_PAYMENT_SENT_SLT,
                Economy.KEY_PAYMENT_RECEIVED_SLT,
                Economy.KEY_PROFIT_SLT,
                Economy.KEY_SOLD_ITEMS_SLT,
                Economy.KEY_SOLD_ITEMS_PROFIT_SLT
            ))
        }

        // Send all tax log webhook messages to discord gradually
        Webhook.sendBatch(Saves.WEBHOOK_ADMIN, webhookMessagesLog, 10)
    }

}