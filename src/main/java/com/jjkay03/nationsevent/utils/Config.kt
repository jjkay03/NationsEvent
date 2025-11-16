package com.jjkay03.nationsevent.utils

import com.jjkay03.nationsevent.NationsEvent

class Config {
    companion object {
        val CONFIG = NationsEvent.INSTANCE.config

        // MAIN
        val EVENT_CODENAME = CONFIG.getString("event-codename", "NT0")

        // INTEGRATIONS
        val INTEGRATIONS_VOICECHAT = CONFIG.getBoolean("integrations.voicechat", false)

        // WORLDS - Loader
        val WORLDS_LOADER_ENABLE = CONFIG.getBoolean("worlds.loader.enable", false)
        val WORLDS_LOADER_WORLDS = CONFIG.getStringList("worlds.loader.worlds")
        // WORLDS - Sync
        val WORLDS_SYNC_ENABLE = CONFIG.getBoolean("worlds.sync.enable", false)
        val WORLDS_SYNC_ENABLE_TIME = CONFIG.getBoolean("worlds.sync.enable-time", false)
        val WORLDS_SYNC_ENABLE_WEATHER = CONFIG.getBoolean("worlds.sync.enable-weather", false)
        val WORLDS_SYNC_ENABLE_GAME_RULES = CONFIG.getBoolean("worlds.sync.enable-game-rules", false)
        val WORLDS_SYNC_ENABLE_DIFFICULTY = CONFIG.getBoolean("worlds.sync.enable-difficulty", false)
        val WORLDS_SYNC_WORLDS = CONFIG.getStringList("worlds.sync.worlds")
        // WORLDS - Lobby
        val WORLDS_LOBBY_ENABLE = CONFIG.getBoolean("worlds.lobby.enable", false)
        val WORLDS_LOBBY_WORLD = CONFIG.getString("worlds.lobby.world")
        // WORLDS - Bridge
        val WORLDS_BRIDGE_ENABLE = CONFIG.getBoolean("worlds.bridge.enable", false)
        val WORLDS_BRIDGE_CARRY_VEHICLE = CONFIG.getBoolean("worlds.bridge.carry-vehicle", false)


        // SETTINGS
        val SETTINGS_DISABLED_CRAFTS = CONFIG.getBoolean("settings.disable-crafts", false)
        val SETTINGS_LIMIT_ENCHANTS = CONFIG.getBoolean("settings.limit-enchants", false)
        val SETTINGS_DISABLE_ENDER_PEARLS = CONFIG.getBoolean("settings.disable-ender-pearls", false)
        val SETTINGS_DISABLE_WOLF_BREEDING = CONFIG.getBoolean("settings.disable-wolf-breeding", false)
        val SETTINGS_FARM_PROTECTION = CONFIG.getBoolean("settings.farm-protection", false)
        val SETTINGS_DISABLE_JOIN_LEAVE_MESSAGES = CONFIG.getBoolean("settings.disable-join-leave-messages", false)
        val SETTINGS_DISABLE_HOSTILE_MOBS_SPAWN = CONFIG.getBoolean("settings.disable-hostile-mobs-spawn", false)


        // FEATURES - Death Ban
        val FEATURES_DEATH_BAN_ENABLE = CONFIG.getBoolean("features.death-ban.enable", false)
        val FEATURES_DEATH_BAN_MESSAGE = CONFIG.getString("features.death-ban.message", "Your fight is over.")
        val FEATURES_DEATH_BAN_LIGHTNING = CONFIG.getBoolean("features.death-ban.lightning", false)
        // FEATURES - Event IGNs
        val FEATURES_EVENT_IGNS = CONFIG.getBoolean("features.event-igns", false)
        // FEATURES - Boat PVP
        val FEATURES_BOAT_PVP_ENABLE = CONFIG.getBoolean("features.boat-pvp.enable", false)
        val FEATURES_BOAT_PVP_DAMAGE_KICK = CONFIG.getBoolean("features.boat-pvp.damage-kick", false)
        val FEATURES_BOAT_PVP_WEAPON_DESTROY = CONFIG.getBoolean("features.boat-pvp.weapon-destroy", false)


        // PLAYER GROUP CHAT
        val PGC_ENABLED = CONFIG.getBoolean("player-group-chat.enable", false)
        val PGC_SAVE_ON_SERVER_RESTART = CONFIG.getBoolean("player-group-chat.save-on-server-restart", false)
        val PGC_LIMIT = CONFIG.getInt("player-group-chat.limit", 1)
        val PGC_BYPASS_DISABLED_CHAT = CONFIG.getBoolean("player-group-chat.bypass-disabled-chat", false)
        val PGC_COLOR = CONFIG.getString("player-group-chat.color", "§7")
        val PGC_SPY_COLOR = CONFIG.getString("player-group-chat.spy-color", "§8")
        val PGC_STAFF_MESSAGE_PREFIX = CONFIG.getString("player-group-chat.staff-msg-prefix", "§6[STAFF]")
        val PGC_NAME_CHARACTER_LIMIT = CONFIG.getInt("player-group-chat.name-character-limit", 6)

    }
}
