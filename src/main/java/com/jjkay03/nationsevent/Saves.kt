package com.jjkay03.nationsevent

import net.luckperms.api.model.group.Group

class Saves() {
    companion object {
        // Rank perms nations event
        const val PERM_ADMIN: String = "nationsevent.admin"
        const val PERM_PROD: String = "nationsevent.production"
        const val PERM_STAFF: String = "nationsevent.staff"
        const val PERM_SPECTATOR: String = "nationsevent.spectator"

        // Permissions
        const val PERM_SIMPLE_VOICECHAT_SPEAK: String = "voicechat.speak"

        // Luckperms groups
        val LP_GROUP_ADMIN: Group? = NationsEvent.LP_GROUP_MANAGER.getGroup("admin")
        val LP_GROUP_PROD: Group? = NationsEvent.LP_GROUP_MANAGER.getGroup("prod")
        val LP_GROUP_STAFF: Group? = NationsEvent.LP_GROUP_MANAGER.getGroup("staff")
        val LP_GROUP_DEFAULT: Group? = NationsEvent.LP_GROUP_MANAGER.getGroup("default")

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
