package com.jjkay03.nationsevent.commands

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
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

    companion object {
        // Save what players have hidden staff
        val HIDE_STAFF_PLAYERS = mutableSetOf<String>()
    }

    // List of permissions that will cause a player to be hidden if they have any of them
    private val hidePermissions = listOf(Saves.PERM_STAFF, Saves.PERM_SPECTATOR)

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // End if sender is not a player
        if (sender !is Player) {
            sender.sendMessage("§cOnly players can run this command!")
            return true
        }

        // Toggle hide state based on whether the player is already in the list
        if (HIDE_STAFF_PLAYERS.contains(sender.uniqueId.toString())) {
            // If player is already hiding staff, remove them from the set and show staff
            showStaff(sender)
            HIDE_STAFF_PLAYERS.remove(sender.uniqueId.toString())
            Utils.messageStaff("§7\uD83D\uDC41 ${sender.name} §cDISABLED §7hide staff (they can see you)")
        } else {
            // If player is not hiding staff, add them to the set and hide staff
            hideStaff(sender)
            HIDE_STAFF_PLAYERS.add(sender.uniqueId.toString())
            Utils.messageStaff("§7\uD83D\uDC41 ${sender.name} §aENABLED §7hide staff (they can't see you)")
        }

        return true
    }

    // Tab Completer - no arguments needed
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String>? {
        return emptyList()
    }

    // Handle staff login when someone has hide staff on
    @EventHandler
    fun onStaffJoin(event: PlayerJoinEvent) {
        // End if the joining player doesn't have any of the hide permissions
        if (!hasHidePermission(event.player)) return

        // Hide the joining player for all players who have hideStaff enabled
        Bukkit.getOnlinePlayers().forEach { onlinePlayer ->
            if (HIDE_STAFF_PLAYERS.contains(onlinePlayer.uniqueId.toString())) {
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
