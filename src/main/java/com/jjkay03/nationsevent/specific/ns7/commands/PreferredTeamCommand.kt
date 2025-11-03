package com.jjkay03.nationsevent.specific.ns7.commands

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.specific.EventSpecific
import net.luckperms.api.model.group.Group
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap

class PreferredTeamCommand(private val commandName: String) : CommandExecutor, TabCompleter {

    companion object {
        // Folia-safe concurrent map
        val PLAYER_PREFERENCES = ConcurrentHashMap<Player, Group>()

        // Define options mapping
        private val OPTIONS_MAP = mapOf(
            "OPTION_1" to EventSpecific.LP_GROUP_NS7_PLAINS,
            "OPTION_2" to EventSpecific.LP_GROUP_NS7_DESERT,
            "OPTION_3" to EventSpecific.LP_GROUP_NS7_SNOW
        )
    }

    // INITIALIZATION
    init {
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
        NationsEvent.INSTANCE.getCommand(commandName)?.tabCompleter = this
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // End if sender not player
        if (sender !is Player) {
            sender.sendMessage("§cOnly players can use this command!")
            return true
        }

        // End if player already chose
        if (PLAYER_PREFERENCES.containsKey(sender)) {
            sender.sendMessage("§cYou have already chosen your preferred team!")
            return true
        }

        // Check args
        if (args.isEmpty()) {
            sender.sendMessage("§cUsage: /$commandName <${OPTIONS_MAP.keys.joinToString("|")}>")
            return true
        }

        // Get the chosen group (convert input to uppercase for matching)
        val chosenGroup: Group? = OPTIONS_MAP[args[0].uppercase()]

        if (chosenGroup == null) {
            sender.sendMessage("§cInvalid option! Choose: ${OPTIONS_MAP.keys.joinToString(", ")}")
            return true
        }

        // Add player to map
        PLAYER_PREFERENCES[sender] = chosenGroup
        sender.sendMessage("§aYou have chosen: ${args[0].uppercase()}")

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String>? {
        if (args.size == 1) { return OPTIONS_MAP.keys.filter { it.startsWith(args[0].uppercase()) } }
        return emptyList()
    }
}