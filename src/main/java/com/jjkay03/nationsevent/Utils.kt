package com.jjkay03.nationsevent

import net.luckperms.api.model.group.Group
import org.bukkit.Bukkit
import org.bukkit.Sound
import java.io.File

object Utils {

    // Function to display plugin welcome message
    fun displayPluginWelcomeMessage(color: String) {
        val welcomeMessage = listOf(
            "${color}  _   _       _   _                 ______               _   ",
            "${color} | \\ | |     | | (_)               |  ____|             | |  ",
            "${color} |  \\| | __ _| |_ _  ___  _ __  ___| |____   _____ _ __ | |_ ",
            "${color} | . ` |/ _` | __| |/ _ \\| '_ \\/ __|  __\\ \\ / / _ \\ '_ \\| __|",
            "${color} | |\\  | (_| | |_| | (_) | | | \\__ \\ |___\\ V /  __/ | | | |_ ",
            "${color} |_| \\_|\\__,_|\\__|_|\\___/|_| |_|___/______\\_/ \\___|_| |_|\\__|",
            ""
        )
        welcomeMessage.forEach { line -> Bukkit.getConsoleSender().sendMessage(line) }
    }

    // Function that sends message to all staff
    fun messageStaff(message: String) {
        Bukkit.getServer().onlinePlayers.forEach { player ->
            if (player.hasPermission(Saves.PERM_STAFF)) player.sendMessage(message)
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

    // Create folder if it doesn't already exist
    fun createFolder(folderName: String): Boolean {
        return try {
            val saveFolder = File("plugins/NationsEvent/$folderName")
            if (!saveFolder.exists()) saveFolder.mkdirs()
            true
        } catch (e: Exception) {
            Bukkit.getLogger().severe("Failed to create '$folderName' folder!")
            e.printStackTrace()
            false
        }
    }

    // Function that checks if a luckperms group has a permission
    fun luckPermsGroupHasPermission(group: Group, permission: String): Boolean {
        return group.nodes.any { it.key == permission && it.value }
    }

}
