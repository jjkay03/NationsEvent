package com.jjkay03.nationsevent.utils

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Utils

class Config {
    companion object {

        // MAIN
        val EVENT_CODENAME = NationsEvent.INSTANCE.config.getString("event-codename", "NT0")

        // SETTINGS
        val SETTINGS_DISABLED_CRAFTS = NationsEvent.INSTANCE.config.getBoolean("settings-disable-crafts", false)
        val SETTINGS_LIMIT_ENCHANTS = NationsEvent.INSTANCE.config.getBoolean("settings-limit-enchants", false)
        val SETTINGS_DISABLE_ENDER_PEARLS = NationsEvent.INSTANCE.config.getBoolean("settings-disable-ender-pearls", false)
        val SETTINGS_DISABLE_WOLF_BREEDING = NationsEvent.INSTANCE.config.getBoolean("settings-disable-wolf-breeding", false)
        val SETTINGS_FARM_PROTECTION = NationsEvent.INSTANCE.config.getBoolean("settings-farm-protection", false)

        // PLAYER GROUP CHAT
        val PGC_ENABLED = NationsEvent.INSTANCE.config.getBoolean("pgc-enable", false)
        val PGC_SAVE_ON_SERVER_RESTART = NationsEvent.INSTANCE.config.getBoolean("pgc-save-on-server-restart", false)
        val PGC_LIMIT = NationsEvent.INSTANCE.config.getInt("pgc-limit", 1)
        val PGC_BYPASS_DISABLED_CHAT = NationsEvent.INSTANCE.config.getBoolean("pgc-bypass-disabled-chat", false)
        val PGC_COLOR = NationsEvent.INSTANCE.config.getString("pgc-color", "§7")
        val PGC_SPY_COLOR = NationsEvent.INSTANCE.config.getString("pgc-spy-color", "§8")
        val PGC_STAFF_MESSAGE_PREFIX = NationsEvent.INSTANCE.config.getString("pgc-staff-msg-prefix", "§6[STAFF]")
        val PGC_NAME_CHARACTER_LIMIT = NationsEvent.INSTANCE.config.getInt("pgc-name-character-limit", 6)

    }
}