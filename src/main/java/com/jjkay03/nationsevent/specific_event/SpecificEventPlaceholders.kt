package com.jjkay03.nationsevent.specific_event

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.specific_event.ne3.NE3_SocialStatus
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player

// PLACEHOLDERS MANAGER FOR PLACEHOLDER API (Specific Event)

class SpecificEventPlaceholders : PlaceholderExpansion() {

    override fun getIdentifier(): String { return "nationsevent" }
    override fun getAuthor(): String { return "jjkay03" }
    override fun getVersion(): String { return NationsEvent.INSTANCE.description.version }
    override fun persist(): Boolean { return true }

    override fun onPlaceholderRequest(player: Player?, identifier: String): String? {
        if (!SpecificEvent.SPECIFIC_EVENT_LOADED) {
            return "§c${SpecificEvent.SPECIFIC_EVENT_CODENAME} code not loaded"
        }

        return when (identifier) {
            // NE3
            "ne3_social_status_icon" -> player?.let { NE3_SocialStatus.getPlayerSocialStatusIcon(it) } ?: ""
            "ne3_social_status_priority" -> player?.let { NE3_SocialStatus.getPlayerSocialStatusPriority(it).toString() } ?: "999"

            else -> null
        }
    }
}