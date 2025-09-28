package com.jjkay03.nationsevent.commands.player_assistance

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class NeedStaffCommand(private val commandName: String) : CommandExecutor, TabCompleter {

    // INITIALIZATION (Register command)
    init {
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
        NationsEvent.INSTANCE.getCommand(commandName)?.tabCompleter = this
    }

    // COMMAND
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // End if not player
        if (sender !is Player) { sender.sendMessage("§cOnly players can use this command!"); return true }

        // Check if reason provided
        if (args.isEmpty()) { sender.sendMessage("§cPlease provide a reason: /$label <reason>"); return true }

        // Parse reason
        val reasonString = args[0].uppercase()
        val reason = try {
            PlayerAssistance.NeedStaffReasons.valueOf(reasonString)
        } catch (e: IllegalArgumentException) {
            sender.sendMessage("§cInvalid reason! Use: ${PlayerAssistance.NeedStaffReasons.entries.joinToString(", ")}")
            return true
        }

        // Check if already has request
        if (PlayerAssistance.NEED_STAFF_REQUESTS.containsKey(sender)) {
            sender.sendMessage("§cYou have already requested staff assistance!")
            return true
        }

        // Add to list
        PlayerAssistance.NEED_STAFF_REQUESTS.put(sender, reason)

        // Send feedback
        sender.sendMessage("§a⚑ You have been added to the STAFF ASSISTANCE list!")
        sender.playSound(sender.location, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f)

        // Alert staff
        PlayerAssistance.alertNeedStaff(sender, reason)

        return true
    }

    // TAB COMPLETER
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String>? {
        if (args.size == 1) {
            return PlayerAssistance.NeedStaffReasons.entries
                .map { it.name.lowercase() }
                .filter { it.startsWith(args[0].lowercase()) }
        }
        return emptyList()
    }
}
