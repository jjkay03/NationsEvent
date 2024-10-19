package com.jjkay03.nationsevent.commands

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Utils
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class HideStaffCommand : CommandExecutor, TabCompleter, Listener {

    // List of permissions that will cause a player to be hidden if they have any of them
    private val hidePermissions = listOf(NationsEvent.PERM_STAFF, NationsEvent.PERM_SPECTATOR)

    // Save what players have hidden staff
    private val hideStaffPlayers = mutableSetOf<String>()


    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // End if sender not a player
        if (sender !is Player) { sender.sendMessage("§cOnly players can run this command!"); return true }

        // Check for correct arguments on or off
        if (args.isEmpty() || (args[0] != "on" && args[0] != "off")) { sender.sendMessage("§cUsage: /hidestaff <on|off>"); return true }

        // Toggle global blindness on or off based on the argument
        if (args[0].equals("on", ignoreCase = true)) {
            hideStaff(sender)
            hideStaffPlayers.add(sender.uniqueId.toString())
            Utils.messageStaff("§7\uD83D\uDC41 ${sender.name} enabled hide staff (they can't see you)")
        }
        else if (args[0].equals("off", ignoreCase = true)) {
            showStaff(sender)
            hideStaffPlayers.remove(sender.uniqueId.toString())
            Utils.messageStaff("§7\uD83D\uDC41 ${sender.name} disabled hide staff (they can see you)")
        }

        return true
    }


    // Tab Completer - provide "on" and "off" as options for tab completion
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String>? {
        if (args.size == 1) {
            return listOf("on", "off").filter { it.startsWith(args[0], ignoreCase = true) }
        }
        return null
    }


    // Deal with staff login when someone has hide staff on
    @EventHandler
    fun onStaffJoin(event: PlayerJoinEvent) {
        // End if the joining player doesn't have any of the hide permissions
        if (!hasHidePermission(event.player)) return

        // Hide the joining player for all players who have hideStaff enabled
        Bukkit.getOnlinePlayers().forEach { onlinePlayer ->
            if (hideStaffPlayers.contains(onlinePlayer.uniqueId.toString())) {
                onlinePlayer.hidePlayer(NationsEvent.INSTANCE, event.player)
            }
        }
    }


    // Hide players with certain permissions
    private fun hideStaff(player: Player) {
        Bukkit.getOnlinePlayers().filter { hasHidePermission(it) }.forEach {
            player.hidePlayer(NationsEvent.INSTANCE, it)
        }
    }

    // Show players with certain permissions
    private fun showStaff(player: Player) {
        Bukkit.getOnlinePlayers().filter { hasHidePermission(it) }.forEach {
            player.showPlayer(NationsEvent.INSTANCE, it)
        }
    }

    // Check if a player has any of the permissions in hidePermissions
    private fun hasHidePermission(player: Player): Boolean {
        return hidePermissions.any { player.hasPermission(it) }
    }
}
