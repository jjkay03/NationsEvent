package com.jjkay03.nationsevent.commands.management

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap

class RandomPlayerTPCommand(private val commandName: String) : CommandExecutor, TabCompleter {

    // Variables
    private val bypassPermsList = listOf(Saves.PERM_STAFF, Saves.PERM_SPECTATOR)
    private val lastTeleportationMap = ConcurrentHashMap<Player, Player>()

    // INITIALIZATION (Register command)
    init {
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
        NationsEvent.INSTANCE.getCommand(commandName)?.tabCompleter = this
    }

    // COMMAND
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // Check sender is player
        if (sender !is Player) { sender.sendMessage("§cOnly players can use this command!"); return true }

        // Get target world
        val targetWorld = if (args.isNotEmpty()) {
            val worldName = args[0]
            val world = Bukkit.getWorld(worldName)
            if (world == null) { sender.sendMessage("§cWorld '$worldName' not found!"); return true }
            world
        } else null

        // Teleport to random player
        teleportToRandomPlayer(sender, targetWorld)
        return true
    }

    // TAB COMPLETER
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        if (args.size == 1) {
            val worldNames = Bukkit.getWorlds().map { it.name }
            return worldNames.filter { it.lowercase().startsWith(args[0].lowercase()) }.toMutableList()
        }
        return mutableListOf()
    }

    // Function to teleport to a random player
    private fun teleportToRandomPlayer(player: Player, targetWorld: World?) {
        val onlinePlayers =
            if (targetWorld != null) { Bukkit.getOnlinePlayers().filter { it.world == targetWorld } }
            else { Bukkit.getOnlinePlayers().toList() }

        // Filter out the player themselves and players with bypass permissions
        val eligiblePlayers = onlinePlayers.filter { it != player && !bypassPermsList.any { perm -> it.hasPermission(perm) } }

        // Check if there are any eligible players to teleport to
        if (eligiblePlayers.isEmpty()) {
            val worldMsg = if (targetWorld != null) " in world '${targetWorld.name}'" else ""
            player.sendMessage("§cNo eligible player to teleport to$worldMsg")
            return
        }

        // Filter out the last player the current player teleported to
        val lastTeleportedPlayer = lastTeleportationMap[player]
        val finalEligiblePlayers =
            if (lastTeleportedPlayer != null && lastTeleportedPlayer.isOnline) { eligiblePlayers.filter { it != lastTeleportedPlayer } }
            else { eligiblePlayers }

        // If all remaining players are excluded, reset the exclusion
        val targetPlayer = if (finalEligiblePlayers.isEmpty()) { eligiblePlayers.random() } else { finalEligiblePlayers.random() }

        // Update the last teleportation map before teleporting
        lastTeleportationMap[player] = targetPlayer

        // Teleport
        player.teleportAsync(targetPlayer.location)

        // Send feedback to sender
        val worldMsg = if (targetWorld != null) " in ${targetWorld.name}" else ""
        player.sendMessage("§6Teleported to ${targetPlayer.name}$worldMsg")
    }
}
