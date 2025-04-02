package com.jjkay03.nationsevent

import net.luckperms.api.model.group.Group
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.lang.reflect.Field

object Utils {

    private var commandMap: CommandMap? = null

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

    // Function to create default directories if they don't exist
    fun createDefaultDirectories() {
        NationsEvent.INSTANCE.logger.info("Generating default plugin directories...")
        if (!Saves.DIR_EVENT_IGNS.exists()) Saves.DIR_EVENT_IGNS.mkdirs()
        if (!Saves.DIR_EXPORTED_VOTES.exists()) Saves.DIR_EXPORTED_VOTES.mkdirs()
    }

    // Function that sends message to all staff
    fun messageStaff(message: String) {
        Bukkit.getServer().onlinePlayers.forEach { player ->
            if (player.hasPermission(Saves.PERM_STAFF)) player.sendMessage(message)
        }
    }

    // Function that sends message to all player with a certain permission
    fun messagePlayerWithPerm(message: String, vararg permissions: String) {
        Bukkit.getServer().onlinePlayers.forEach { player ->
            if (permissions.any { player.hasPermission(it) }) player.sendMessage(message)
        }
    }

    // Function that plays a sound to all player on the server
    fun playSoundToAllPlayers(sound: Sound, volume: Float, pitch: Float) {
        Bukkit.getOnlinePlayers().forEach { player ->
            player.playSound(player.location, sound, volume, pitch)
        }
    }

    // Function that checks if a luckperms group has a permission
    fun luckPermsGroupHasPermission(group: Group?, permission: String): Boolean {
        if (group == null) return false // Return false if the group is null
        return group.nodes.any { it.key == permission && it.value }
    }

    // Function to initialize commandMap using reflection
    private fun getCommandMap(): CommandMap {
        if (commandMap == null) {
            val commandMapField: Field = Bukkit.getServer().javaClass.getDeclaredField("commandMap")
            commandMapField.isAccessible = true
            commandMap = commandMapField.get(Bukkit.getServer()) as CommandMap
        }
        return commandMap!!
    }

    // Function to register command
    fun registerCommand(plugin: JavaPlugin, commandName: String, executor: CommandExecutor) {
        val command = object : BukkitCommand(commandName) {
            override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
                return executor.onCommand(sender, this, label, args)
            }
        }
        getCommandMap().register(plugin.name, command)
    }

}
