package com.jjkay03.nationsevent.commands.others

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class GamemodeCommand(private val commandName: String) : CommandExecutor, TabCompleter {

    // INITIALIZATION
    init {
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
        NationsEvent.INSTANCE.getCommand(commandName)?.tabCompleter = this
    }

    // COMMAND EXECUTION
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // End if invalid args
        if (args.isEmpty()) { sender.sendMessage("§cUsage: /$label <gamemode> [player]"); return true }

        // Parse gamemode
        val gameMode = parseGameMode(args[0]) ?: run { sender.sendMessage("§cInvalid gamemode!"); return true }

        // Two arguments: set gamemode for specified player
        if (args.size >= 2) {
            // Check if sender has permission to set others
            if (!sender.hasPermission(Saves.PERM_COMMAND_GAMEMODE_SET_OTHERS)) { sender.sendMessage("§cYou don't have permission to set others gamemode!"); return true }

            // Check if sender has permission for this gamemode
            if (!hasGameModePermission(sender, gameMode)) { sender.sendMessage("§cYou don't have permission for gamemode ${gameMode.name.lowercase()}!"); return true }

            val targetPlayer = Bukkit.getPlayer(args[1]) ?: run { sender.sendMessage("§cPlayer '${args[1]}' not found!"); return true }

            // Set gamemode
            targetPlayer.gameMode = gameMode

            // Send feedback
            sender.sendMessage("§6Set ${targetPlayer.name}'s gamemode to ${gameMode.name.lowercase()}")
        }

        // One argument: set gamemode for sender
        else {
            // End if console
            if (sender !is Player) { sender.sendMessage("§cConsole must specify a player!"); return true }

            // Check if sender has permission for this gamemode
            if (!hasGameModePermission(sender, gameMode)) { sender.sendMessage("§cYou don't have permission for gamemode ${gameMode.name.lowercase()}!"); return true }

            // Set gamemode
            sender.gameMode = gameMode

            // Send feedback
            sender.sendMessage("§6Set own gamemode to ${gameMode.name.lowercase()}")
        }

        return true
    }

    // Helper function to parse gamemode
    private fun parseGameMode(input: String): GameMode? {
        return when (input.lowercase()) {
            "s", "survival", "0" -> GameMode.SURVIVAL
            "c", "creative", "1" -> GameMode.CREATIVE
            "a", "adventure", "2" -> GameMode.ADVENTURE
            "sp", "spectator", "3" -> GameMode.SPECTATOR
            else -> null
        }
    }

    // Helper function to check gamemode permission
    private fun hasGameModePermission(sender: CommandSender, gameMode: GameMode): Boolean {
        return when (gameMode) {
            GameMode.SURVIVAL -> sender.hasPermission(Saves.PERM_COMMAND_GAMEMODE_SURVIVAL)
            GameMode.CREATIVE -> sender.hasPermission(Saves.PERM_COMMAND_GAMEMODE_CREATIVE)
            GameMode.ADVENTURE -> sender.hasPermission(Saves.PERM_COMMAND_GAMEMODE_ADVENTURE)
            GameMode.SPECTATOR -> sender.hasPermission(Saves.PERM_COMMAND_GAMEMODE_SPECTATOR)
        }
    }

    // TAB COMPLETION
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        if (args.size == 1) {
            val gamemodes = mutableListOf<String>()
            if (sender.hasPermission(Saves.PERM_COMMAND_GAMEMODE_SURVIVAL)) gamemodes.add("survival")
            if (sender.hasPermission(Saves.PERM_COMMAND_GAMEMODE_CREATIVE)) gamemodes.add("creative")
            if (sender.hasPermission(Saves.PERM_COMMAND_GAMEMODE_ADVENTURE)) gamemodes.add("adventure")
            if (sender.hasPermission(Saves.PERM_COMMAND_GAMEMODE_SPECTATOR)) gamemodes.add("spectator")
            return gamemodes.filter { it.startsWith(args[0], ignoreCase = true) }
        }
        if (args.size == 2 && sender.hasPermission(Saves.PERM_COMMAND_GAMEMODE_SET_OTHERS)) {
            return Bukkit.getOnlinePlayers()
                .map { it.name }
                .filter { it.startsWith(args[1], ignoreCase = true) }
        }
        return emptyList()
    }

}
