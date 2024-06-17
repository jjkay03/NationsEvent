package com.jjkay03.nationsevent

import org.bukkit.Bukkit

object Utils {

    // Function that sends message to all staff
    fun messageStaff(message: String) {
        Bukkit.getServer().onlinePlayers.forEach { player ->
            if (player.hasPermission(NationsEvent.PERM_STAFF)) player.sendMessage(message)
        }
    }

}
