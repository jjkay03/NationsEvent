package com.jjkay03.nationsevent

import com.jjkay03.nationsevent.economy.Economy
import com.jjkay03.nationsevent.economy.EconomyUtils
import com.jjkay03.nationsevent.specific.ne3.NE3_SocialStatus
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player

// PLACEHOLDERS MANAGER FOR PLACEHOLDER API

class Placeholders : PlaceholderExpansion() {

    override fun getIdentifier(): String { return "nationsevent" }
    override fun getAuthor(): String { return "jjkay03" }
    override fun getVersion(): String { return NationsEvent.INSTANCE.description.version}
    override fun persist(): Boolean { return true }

    override fun onPlaceholderRequest(player: Player?, identifier: String): String? {

        // If this placeholder depends on the economy feature, and it is disabled return the disabled message
        if (identifier.startsWith("eco") && !Economy.FEATURE_ENABLED) { return "§cEconomy is disabled" }

        return when (identifier) {
            // Test placeholder
            "test" -> "§eNationsEvent"

            // Economy placeholders
            "eco_player_balance" -> if (player == null) "" else EconomyUtils.getPlayerBalance(player).toString()
            "eco_player_balance_format_full" -> if (player == null) "" else EconomyUtils.formatMoney(EconomyUtils.getPlayerBalance(player), EconomyUtils.MoneyFormat.FULL)
            "eco_player_balance_format_shorten" -> if (player == null) "" else EconomyUtils.formatMoney(EconomyUtils.getPlayerBalance(player), EconomyUtils.MoneyFormat.SHORTEN)
            "eco_total_balances" -> EconomyUtils.formatMoney(Economy.TOTAL_BALANCES_AMOUNT)
            "eco_total_balances_format_full" -> EconomyUtils.formatMoney(Economy.TOTAL_BALANCES_AMOUNT, EconomyUtils.MoneyFormat.FULL)
            "eco_total_balances_format_shorten" -> EconomyUtils.formatMoney(Economy.TOTAL_BALANCES_AMOUNT, EconomyUtils.MoneyFormat.SHORTEN)

            // NE3 Social Status placeholder
            "ne3_social_status_icon" -> player?.let { NE3_SocialStatus.getSocialStatusIcon(it) } ?: ""

            else -> null
        }
    }
}