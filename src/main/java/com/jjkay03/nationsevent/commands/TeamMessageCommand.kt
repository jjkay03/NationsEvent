package com.jjkay03.nationsevent.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class TeamMessageCommand(private val plugin: JavaPlugin): CommandExecutor, TabCompleter {

    private lateinit var config: FileConfiguration

    // COMMAND
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        // End command if sender is not a player
        if (sender !is Player) { sender.sendMessage("§cOnly players can run this command!"); return true }

        // End if wrong amount of args
        if (args.size < 2) { sender.sendMessage("§cUsage: /teammsg <team> <message>"); return true }

        val teamKey = args[0]
        val teamMessage = args.drop(1).joinToString(" ")

        // End if no perm for team
        if (!canPlayerSendMessage(sender, teamKey)) { sender.sendMessage("§cYou don't have permission to send messages to that team!"); return true }

        val teamName = getTeamName(teamKey) ?: "Unknown Team"
        val colorCode = getColorCode(teamKey) ?: "7"
        val formattedMessage = "§$colorCode[$teamName] ${sender.name}§r: $teamMessage"

        plugin.server.onlinePlayers.forEach { player ->
            if (getTeamPermission(teamKey)?.let { player.hasPermission(it) } == true) {
                player.sendMessage(formattedMessage)
            }
        }

        return true
    }

    // TAB COMPLETE
    override fun onTabComplete(sender: CommandSender, cmd: Command, alias: String, args: Array<out String>): MutableList<String>? {
        if (args.size == 1 && sender is Player) {
            val teamNames = mutableListOf<String>()
            val playerPermissions = sender.effectivePermissions
                .map { it.permission }
                .filter { it.startsWith("team.") }

            playerPermissions.forEach { permission ->
                val teamKey = permission.substringAfter("team.")
                val teamName = config.getString("teams.$teamKey.name")
                if (teamName != null) {
                    teamNames.add(teamName)
                }
            }

            return teamNames
        }
        return null
    }

    // Config reading functions
    private fun getTeamName(teamKey: String): String? { return config.getString("teams.$teamKey.name") }
    private fun getColorCode(teamKey: String): String? { return config.getString("teams.$teamKey.color-code") }
    private fun getTeamPermission(teamKey: String): String? { return config.getString("teams.$teamKey.permission") }

    // Functions that checks if player has the perm for a team
    private fun canPlayerSendMessage(player: Player, teamKey: String): Boolean {
        val requiredPermission = getTeamPermission(teamKey)
        return requiredPermission != null && player.hasPermission(requiredPermission)
    }

}