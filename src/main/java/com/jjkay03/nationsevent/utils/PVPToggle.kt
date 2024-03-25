package com.jjkay03.nationsevent.utils

import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class PVPToggle(private val plugin: JavaPlugin) : Listener {
    private val config = plugin.config
    private val pvpToggle: Boolean = config.getBoolean("pvp-toggle")


}