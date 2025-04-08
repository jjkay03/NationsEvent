package com.jjkay03.nationsevent.economy

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Utils
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import org.bukkit.scheduler.BukkitRunnable
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
    private var TOTAL_COLLECTED_TAX: Long = 0
    private var TAX_COLLECTION_DURATION_MINUTES: Int = 2
    var TAX_PAYMENTS_OPEN: Boolean = false


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
            "§7You can pay your due tax using §e/paytax <amount>§7.\n§r " )
        for (line in taxAlertMessage) Utils.messageAllPlayers(line)
        giveTaxRecordsToAllPlayers() // Give all players tax records
        TAX_PAYMENTS_OPEN = true // Open tax payment

        // Run timer and pay taxes once done
        runTimer(TAX_COLLECTION_DURATION_MINUTES) {
            // TODO - make players pay tax
            TAX_PAYMENTS_OPEN = false // Close tax payment
            Utils.messageStaff("§6COLLECTED TAXES - ${PLAYER_TAX_DATA_MAP.size} players collected total of ${EconomyUtils.formatMoney(TOTAL_COLLECTED_TAX)}")
        }
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
        meta.title = "Tax Records - ${player.name}"
        meta.author = "Nations Revenue Services (NRS)"
        val page1 = buildString {
            append("§lTax Summary\n")
            append(". . .\n")
            append(". . .\n")
            append(". . .\n")
        }
        val page2 = buildString {
            append("§lTEST\n")
            append(". . .\n")
            append(". . .\n")
            append(". . .\n")
        }
        meta.pages = listOf(page1, page2)
        book.itemMeta = meta
        return book
    }

}