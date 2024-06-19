package com.jjkay03.nationsevent

import org.bukkit.Bukkit

object Utils {

    // Function that sends message to all staff
    fun messageStaff(message: String) {
        Bukkit.getServer().onlinePlayers.forEach { player ->
            if (player.hasPermission(NationsEvent.PERM_STAFF)) player.sendMessage(message)
        }
    }

    // Function that sends message to all player with a certain permission
    fun messagePlayerWithPerm(message: String, permission: String) {
        Bukkit.getServer().onlinePlayers.forEach { player ->
            if (player.hasPermission(permission)) player.sendMessage(message)
        }
    }

}
