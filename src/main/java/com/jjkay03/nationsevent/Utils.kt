package com.jjkay03.nationsevent

import org.bukkit.Bukkit
import org.bukkit.Sound

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

    // Function that plays a sound to all player on the server
    fun playSoundToAllPlayers(sound: Sound, volume: Float, pitch: Float) {
        Bukkit.getOnlinePlayers().forEach { player ->
            player.playSound(player.location, sound, volume, pitch)
        }
    }

}
