package com.jjkay03.nationsevent.commands

import com.jjkay03.nationsevent.Saves
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class RandomPlayerTPCommand : CommandExecutor {

    private val bypassPermsList = listOf(Saves.PERM_STAFF, Saves.PERM_SPECTATOR)
    private val lastTeleportationList = mutableListOf<Pair<Player, Player>>()

    // COMMAND
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) { sender.sendMessage("§cOnly players can use this command!"); return true }
        teleportToRandomPlayer(sender)
        return true
    }

    // Function to teleport to a random player
    private fun teleportToRandomPlayer(player: Player) {
        val onlinePlayers = Bukkit.getOnlinePlayers().toList()

        // Filter out the player themselves and players with bypass permissions
        val eligiblePlayers = onlinePlayers.filter { it != player && !bypassPermsList.any { perm -> it.hasPermission(perm) } }

        // Check if there are any eligible players to teleport to
        if (eligiblePlayers.isEmpty()) { player.sendMessage("§cNo eligible player to teleport to"); return }

        // Filter out the last player the current player teleported to
        val lastTeleportedPlayer = lastTeleportationList.find { it.first == player }?.second
        val finalEligiblePlayers = if (lastTeleportedPlayer != null) {
            eligiblePlayers.filter { it != lastTeleportedPlayer }
        } else {
            eligiblePlayers
        }

        // If all remaining players are excluded, reset the exclusion
        val targetPlayer = if (finalEligiblePlayers.isEmpty()) { eligiblePlayers.random() } else { finalEligiblePlayers.random() }

        // Teleport the player to the selected target
        player.teleport(targetPlayer)

        // Update the last teleportation list
        lastTeleportationList.removeIf { it.first == player }
        lastTeleportationList.add(player to targetPlayer)
    }
}
