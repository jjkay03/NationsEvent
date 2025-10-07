package com.jjkay03.nationsevent.utils

import com.jjkay03.nationsevent.NationsEvent

class Config {
    companion object {

        // MAIN
        val EVENT_CODENAME = NationsEvent.INSTANCE.config.getString("event-codename", "NT0")

        // SETTINGS
        val SETTINGS_DISABLED_CRAFTS = NationsEvent.INSTANCE.config.getBoolean("settings.disable-crafts", false)
        val SETTINGS_LIMIT_ENCHANTS = NationsEvent.INSTANCE.config.getBoolean("settings.limit-enchants", false)
        val SETTINGS_DISABLE_ENDER_PEARLS = NationsEvent.INSTANCE.config.getBoolean("settings.disable-ender-pearls", false)
        val SETTINGS_DISABLE_WOLF_BREEDING = NationsEvent.INSTANCE.config.getBoolean("settings.disable-wolf-breeding", false)
        val SETTINGS_FARM_PROTECTION = NationsEvent.INSTANCE.config.getBoolean("settings.farm-protection", false)
        val SETTINGS_DISABLE_JOIN_LEAVE_MESSAGES = NationsEvent.INSTANCE.config.getBoolean("settings.disable-join-leave-messages", false)

        // FEATURES
        val FEATURES_DEATH_BAN_ENABLE = NationsEvent.INSTANCE.config.getBoolean("features.death-ban.enable", false)
        val FEATURES_DEATH_BAN_MESSAGE = NationsEvent.INSTANCE.config.getString("features.death-ban.message", "Your fight is over.")
        val FEATURES_DEATH_BAN_LIGHTNING = NationsEvent.INSTANCE.config.getBoolean("features.death-ban.lightning", false)
        val FEATURES_EVENT_IGNS = NationsEvent.INSTANCE.config.getBoolean("features.event-igns", false)

        // PLAYER GROUP CHAT
        val PGC_ENABLED = NationsEvent.INSTANCE.config.getBoolean("player-group-chat.enable", false)
        val PGC_SAVE_ON_SERVER_RESTART = NationsEvent.INSTANCE.config.getBoolean("player-group-chat.save-on-server-restart", false)
        val PGC_LIMIT = NationsEvent.INSTANCE.config.getInt("player-group-chat.limit", 1)
        val PGC_BYPASS_DISABLED_CHAT = NationsEvent.INSTANCE.config.getBoolean("player-group-chat.bypass-disabled-chat", false)
        val PGC_COLOR = NationsEvent.INSTANCE.config.getString("player-group-chat.color", "§7")
        val PGC_SPY_COLOR = NationsEvent.INSTANCE.config.getString("player-group-chat.spy-color", "§8")
        val PGC_STAFF_MESSAGE_PREFIX = NationsEvent.INSTANCE.config.getString("player-group-chat.staff-msg-prefix", "§6[STAFF]")
        val PGC_NAME_CHARACTER_LIMIT = NationsEvent.INSTANCE.config.getInt("player-group-chat.name-character-limit", 6)
    }
}
