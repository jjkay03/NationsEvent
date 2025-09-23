package com.jjkay03.nationsevent

import com.jjkay03.nationsevent.commands.others.DisabledCommands
import com.jjkay03.nationsevent.utils.Scheduler
import com.jjkay03.nationsevent.utils.ServerType
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import java.lang.reflect.Field

object Utils {

    private var commandMap: CommandMap? = null

    // Function to display plugin welcome message
    fun pluginWelcomeMessage(color: String) {
        val welcomeMessage = mutableListOf(
            "$color  _   _       _   _                 ______               _   ",
            "$color | \\ | |     | | (_)               |  ____|             | |  ",
            "$color |  \\| | __ _| |_ _  ___  _ __  ___| |____   _____ _ __ | |_ ",
            "$color | . ` |/ _` | __| |/ _ \\| '_ \\/ __|  __\\ \\ / / _ \\ '_ \\| __|",
            "$color | |\\  | (_| | |_| | (_) | | | \\__ \\ |___\\ V /  __/ | | | |_ ",
            "$color |_| \\_|\\__,_|\\__|_|\\___/|_| |_|___/______\\_/ \\___|_| |_|\\__|"
        )
        if (ServerType.SERVER_TYPE == ServerType.ThreadingType.MULTI_THREADED_FOLIA) welcomeMessage.add("$color THREADED VERSION §2(FOLIA)")
        welcomeMessage.add("")
        welcomeMessage.forEach { line -> Bukkit.getConsoleSender().sendMessage(line) }
        NationsEvent.INSTANCE.logger.info("NationsEvent is running!")
        NationsEvent.INSTANCE.logger.info("Plugin version: ${NationsEvent.INSTANCE.description.version}")
    }

    // Function to register command
    fun registerCommand(commandName: String, executor: CommandExecutor) {
        val command = object : BukkitCommand(commandName) {
            override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
                return executor.onCommand(sender, this, label, args)
            }
        }
        getCommandMap().register(NationsEvent.INSTANCE.name, command)
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
    fun messagePlayerList(receivers: List<OfflinePlayer>, message: Component) {
        receivers.filter { it.isOnline }.forEach { offlinePlayer -> offlinePlayer.player!!.sendMessage(message) }
    }

    // Function that sends message to all player with a certain permission (scheduler thread safe)
    fun messagePlayerWithPerm(message: String, vararg permissions: String) {
        Bukkit.getServer().onlinePlayers.forEach { player ->
            if (permissions.any { player.hasPermission(it) }) {
                Scheduler.task(Scheduler.SchedulerType.PLAYER, {
                    player.sendMessage(message)
                }, player = player)
            }
        }
    }

}