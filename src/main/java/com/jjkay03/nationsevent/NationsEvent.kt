package com.jjkay03.nationsevent

import com.jjkay03.nationsevent.commands.announce.*
import com.jjkay03.nationsevent.commands.management.SessionTimeCommand
import com.jjkay03.nationsevent.settings.*
import com.jjkay03.nationsevent.utils.FilesManager
import com.jjkay03.nationsevent.utils.ServerType
import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.model.group.GroupManager
import net.luckperms.api.model.user.UserManager
import org.bukkit.plugin.java.JavaPlugin

open class NationsEvent : JavaPlugin() {

    companion object {
        lateinit var INSTANCE: NationsEvent

        // LUCKPERMS API
        lateinit var LP_INSTANCE: LuckPerms
        lateinit var LP_GROUP_MANAGER: GroupManager
        lateinit var LP_USER_MANAGER: UserManager
    }

    // PLUGIN STARTUP LOGIC
    override fun onEnable() {
        INSTANCE = this

        // STARTUP INFO
        ServerType.detect()
        Utils.pluginWelcomeMessage("§e")

        // STARTUP
        saveDefaultConfig()            // Save the default configuration
        reloadConfig()                 // Reload the configuration
        getAPIs()                      // Get all APIs instances and info
        Saves()                        // Load all variables in saves class
        FilesManager.createDefaults()  // Generate default directories and files

        // REGISTER COMMANDS
        logger.info("REGISTER AND LOAD ALL FEATURES:")
        logger.info("- Register all commands")
        // COMMANDS : ANNOUNCE
        AnnounceSessionCommand("announcesession")
        JoinStageCommand("joinstage")
        // COMMANDS : MANAGEMENT
        SessionTimeCommand("sessiontime")

        // SETTINGS
        DisabledCrafts()
        DisableCrafterCrafts()
        LimitEnchants()
        DisableEnderPearls()
        FarmProtection()
        DisableWolfBreeding()

    }

    // PLUGIN SHUTDOWN LOGIC
    override fun onDisable() {
        logger.info("Bye bye!")
    }

    // Function to get APIs
    private fun getAPIs() {
        // LUCKPERMS API
        LP_INSTANCE = LuckPermsProvider.get()
        LP_GROUP_MANAGER = LP_INSTANCE.groupManager
        LP_USER_MANAGER = LP_INSTANCE.userManager
        @Suppress("SENSELESS_COMPARISON")
        if (LP_INSTANCE != null) logger.info("Connected to LuckPerms API") else logger.severe("Can't connect to LuckPerms API")
    }
}