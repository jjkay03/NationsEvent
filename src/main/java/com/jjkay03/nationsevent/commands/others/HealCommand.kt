package com.jjkay03.nationsevent.commands.others

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.attribute.Attribute

class HealCommand(private val commandName: String) : CommandExecutor, TabCompleter {

    // INITIALIZATION
    init {
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
        NationsEvent.INSTANCE.getCommand(commandName)?.tabCompleter = this
    }

    // COMMAND EXECUTOR
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // Check if sender is player
        if (sender !is Player) { sender.sendMessage("§cOnly players can use this command!"); return true }

        // No args - heal sender
        if (args.isEmpty()) {
            healPlayer(sender)
            sender.sendMessage("§6Healed your self")
            return true
        }

        // Get target player
        val targetName = args[0]
        val target = Bukkit.getPlayer(targetName)

        // Player not found
        if (target == null) {
            sender.sendMessage("§cPlayer not found!")
            return true
        }

        // Heal target player
        healPlayer(target)
        sender.sendMessage("§6Healed $targetName")
        return true
    }

    // TAB COMPLETION
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String> {
        if (args.size == 1) {
            return Bukkit.getOnlinePlayers().map { it.name }.filter { it.lowercase().startsWith(args[0].lowercase()) }
        }
        return emptyList()
    }

    // Helper function to heal a player
    private fun healPlayer(player: Player) {
        player.health = player.getAttribute(Attribute.MAX_HEALTH)?.value ?: 20.0
        player.saturation = 20f
        player.foodLevel = 20
    }

}
