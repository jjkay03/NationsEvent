package com.jjkay03.nationsevent.commands.player_assistance

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class NeedRecordCommand(private val commandName: String) : CommandExecutor {

    // INITIALIZATION (Register command)
    init {
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
    }

    // COMMAND
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // End if not player
        if (sender !is Player) { sender.sendMessage("§cOnly players can use this command!"); return true }

        // Check if already has request
        if (PlayerAssistance.NEED_RECORD_REQUESTS.contains(sender)) {
            sender.sendMessage("§cYou have already sent a record requested!")
            return true
        }

        // Add to list
        PlayerAssistance.NEED_RECORD_REQUESTS.add(sender)

        // Send feedback
        sender.sendMessage("§a⚑ You have been added to the RECORD REQUESTS list!")
        sender.playSound(sender.location, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f)

        // Alert staff
        val playersAround = sender.world.getNearbyEntities(sender.location, 80.0, 40.0, 80.0).filterIsInstance<Player>()
        PlayerAssistance.alertNeedRecord(sender, playersAround.size)

        return true
    }
}
