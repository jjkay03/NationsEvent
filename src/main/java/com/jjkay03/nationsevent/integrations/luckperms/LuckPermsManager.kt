package com.jjkay03.nationsevent.integrations.luckperms

import com.jjkay03.nationsevent.NationsEvent
import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.model.group.GroupManager
import net.luckperms.api.model.user.UserManager

class LuckPermsManager {

    // COMPANIONS
    companion object {
        lateinit var LP_INSTANCE: LuckPerms
        lateinit var LP_GROUP_MANAGER: GroupManager
        lateinit var LP_USER_MANAGER: UserManager
    }

    // INITIALIZATION
    init { getAPI() }

    // Helper function to get LuckPerms API
    private fun getAPI() {
        LP_INSTANCE = LuckPermsProvider.get()
        LP_GROUP_MANAGER = LP_INSTANCE.groupManager
        LP_USER_MANAGER = LP_INSTANCE.userManager

        @Suppress("SENSELESS_COMPARISON")
        if (LP_INSTANCE != null) {
            NationsEvent.INSTANCE.logger.info("Connected to LuckPerms API")
        } else {
            NationsEvent.INSTANCE.logger.severe("Can't connect to LuckPerms API")
        }
    }

}