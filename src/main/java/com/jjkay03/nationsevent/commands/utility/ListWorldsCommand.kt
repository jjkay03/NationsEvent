package com.jjkay03.nationsevent.commands.utility

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.utils.Config
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class ListWorldsCommand(private val commandName: String) : CommandExecutor {

    // INITIALIZATION
    init {
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
    }

    // COMMAND EXECUTION
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // Get all worlds
        val worlds = Bukkit.getWorlds()

        // Get sync worlds from config
        val syncWorlds = Config.WORLDS_SYNC_WORLDS
        val masterWorld = syncWorlds.firstOrNull()

        // Header
        sender.sendMessage("§r")
        sender.sendMessage("§6Loaded worlds:")

        // List all worlds with player count
        worlds.forEach { world ->
            val playerCount = world.players.size
            val syncTag = when {
                world.name == masterWorld -> "§3(♻M) "
                syncWorlds.contains(world.name) -> "§3(♻) "
                else -> ""
            }
            sender.sendMessage("§f- ${world.name} $syncTag§7(Players: §a$playerCount§7)")
        }

        sender.sendMessage("§r")

        return true
    }

}
