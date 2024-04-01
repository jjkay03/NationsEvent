package com.jjkay03.nationsevent

import com.jjkay03.nationsevent.commands.*
import com.jjkay03.nationsevent.utils.*

class Register(private val plugin: NationsEvent) {

    // Run register functions when class initiate
    init {
        registerCommands()
        registerEvents()
    }

    // Get commands
    private fun registerCommands() {
        plugin.getCommand("joinvc")?.setExecutor(JoinvcCommand())
        plugin.getCommand("announcesession")?.setExecutor(AnnounceSessionCommand())
        plugin.getCommand("sessiontime")?.setExecutor(SessionTimeCommand())
        plugin.getCommand("pvptoggle")?.setExecutor(PVPToggle())
        plugin.getCommand("pvptoggle")?.tabCompleter = PVPToggle() // Tab completer
        plugin.getCommand("pvpalerts")?.setExecutor(PvpAlertsCommand())
        //plugin.getCommand("teammessage")?.setExecutor(TeamMessageCommand(plugin))
        //plugin.getCommand("teammessage")?.tabCompleter = TeamMessageCommand(plugin) // Tab completer
    }

    // Register event handler
    private fun registerEvents() {
        plugin.server.pluginManager.registerEvents(PVPToggle(), plugin)
        plugin.server.pluginManager.registerEvents(MilkTheGator(), plugin)
        plugin.server.pluginManager.registerEvents(TeamCore(), plugin)
    }
}