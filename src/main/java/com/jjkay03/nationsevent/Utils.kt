package com.jjkay03.nationsevent

import com.jjkay03.nationsevent.commands.DisabledCommands
import net.kyori.adventure.text.Component
import net.luckperms.api.model.group.Group
import net.luckperms.api.node.Node
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.Sound
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.permissions.Permission
import org.bukkit.plugin.java.JavaPlugin
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

    // Function that sends message to all staff
    fun messageStaff(message: String) {
        Bukkit.getServer().onlinePlayers.forEach { player -> if (player.hasPermission(Saves.PERM_STAFF)) player.sendMessage(message) }
    }

    // Function that sends message to all player with a certain permission
    fun messagePlayerWithPerm(message: String, vararg permissions: String) {
        Bukkit.getServer().onlinePlayers.forEach { player -> if (permissions.any { player.hasPermission(it) }) player.sendMessage(message) }
    }

    // Function to send a message to all players online
    fun messageAllPlayers(message: String) {
        Bukkit.getOnlinePlayers().forEach { player -> player.sendMessage(message) }
    }

    // Function to send title to all players online
    fun sendTitleToAllPlayers(title: String = "", subtitle: String = "", fadeIn: Int = 10, stay: Int = 20, fadeOut: Int = 10) {
        Bukkit.getOnlinePlayers().forEach { player -> player.sendTitle(title, subtitle, fadeIn, stay, fadeOut) }
    }

    // Function that plays a sound to all player on the server
    fun playSoundToAllPlayers(sound: Sound, volume: Float, pitch: Float) {
        Bukkit.getOnlinePlayers().forEach { player -> player.playSound(player.location, sound, volume, pitch) }
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

    // Function to give an item to a player (item will be placed in player's hand)
    fun giveItemToPlayer(player: Player, item: ItemStack) {
        val inventory = player.inventory
        if (inventory.itemInMainHand.type == Material.AIR) inventory.setItemInMainHand(item)
        else if (inventory.firstEmpty() != -1) { inventory.setItem(inventory.firstEmpty(), inventory.itemInMainHand); inventory.setItemInMainHand(item) }
        else { player.world.dropItemNaturally(player.location, item); player.sendMessage("§c⚠ Your inventory is full ${item.type} has been dropped at your feet!") }
        player.playSound(player.location, Sound.ENTITY_ITEM_PICKUP, 0.5f, 1f)
    }

    // Function to give an item to all players (item will be placed in player's hand)
    fun giveItemToAllPlayers(item: ItemStack) {
        Bukkit.getOnlinePlayers().forEach { player -> giveItemToPlayer(player, item) }
    }

    // Function to disable all commands in a list
    fun disableCommands(commands: Set<String>, featureName: String) {
        for (command in commands) NationsEvent.INSTANCE.getCommand(command)?.setExecutor(DisabledCommands(featureName))
    }

    // Function that removes the formats from strings. https://minecraft.wiki/w/Formatting_codes
    fun removeFormattingCodes(string: String?): String {
        return if (string == null) "null" else {
            val result = StringBuilder(string)
            val indexes = mutableListOf<Int>()

            for (i in 0..<result.length) { if (result[i] == '§') { indexes.add(i - 2*indexes.size) } }
            indexes.forEach { result.delete(it, it+2) }
            return result.toString()
        }
    }

    // Function that sends a 'message' to all online players in 'receivers'
    fun sendMessageToPlayerList(receivers: List<OfflinePlayer>, message: Component) {
        receivers.filter { it.isOnline }.forEach { it.player!!.sendMessage(message) }
    }
}
