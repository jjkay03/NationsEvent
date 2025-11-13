package com.jjkay03.nationsevent.commands.utility

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.integrations.luckperms.LuckPermsUtils
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class ListLuckPermsGroupsCommand(private val commandName: String) : CommandExecutor {

    // INITIALIZATION
    init {
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
    }

    // COMMAND EXECUTION
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // Get all groups
        val groups = LuckPermsUtils.getAllGroups()

        // Header
        sender.sendMessage("§r")
        sender.sendMessage("§6Loaded groups:")

        // List all groups with player counts
        groups.forEach { group ->
            val allUsers = LuckPermsUtils.groupGetAllUsers(group)
            val totalPlayers = allUsers.size
            val onlinePlayers = allUsers.count { uuid ->
                Bukkit.getPlayer(uuid)?.isOnline == true
            }
            sender.sendMessage("§f- ${group.name} §7(Players: $totalPlayers / Online: §a$onlinePlayers§7)")
        }

        sender.sendMessage("§r")

        return true
    }

}
