package com.jjkay03.nationsevent

class Saves() {
    companion object {
        // Rank perms nations event
        const val PERM_ADMIN: String = "nationsevent.admin"
        const val PERM_PROD: String = "nationsevent.production"
        const val PERM_STAFF: String = "nationsevent.staff"
        const val PERM_SPECTATOR: String = "nationsevent.spectator"

        // Event variables
        lateinit var EVENT_CODENAME: String
        var SESSION_STARTED: Boolean = false
        var SESSION_START_TIME: Long = 0
    }

    init {
        // Variables from config
        EVENT_CODENAME = NationsEvent.INSTANCE.config.getString("event-codename").toString()
    }
}
