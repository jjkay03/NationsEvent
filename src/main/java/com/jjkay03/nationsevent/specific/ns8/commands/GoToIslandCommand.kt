package com.jjkay03.nationsevent.specific.ns8.commands

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Utils
import com.jjkay03.nationsevent.specific.EventSpecific
import com.jjkay03.nationsevent.integrations.luckperms.LuckPermsUtils
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GoToIslandCommand(private val commandName: String) : CommandExecutor {

    // INITIALIZATION
    init {
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
    }

    // COMMAND
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // End if not player
        if (sender !is Player) {
            sender.sendMessage("§cOnly players can use this command!")
            return true
        }

        // Get target world based on player's group
        val targetWorld: World? = when {
            LuckPermsUtils.isPlayerInGroup(sender, EventSpecific.LP_GROUP_NS8_HOT) -> EventSpecific.WORLD_NS8_HOT
            LuckPermsUtils.isPlayerInGroup(sender, EventSpecific.LP_GROUP_NS8_COLD) -> EventSpecific.WORLD_NS8_COLD
            else -> null
        }

        // End if player doesn't have a valid island group
        if (targetWorld == null) {
            sender.sendMessage("§cYou don't belong to any island team!")
            return true
        }

        // Teleport to island spawn
        Utils.teleportPlayerToWorldSpawnRadius(sender, targetWorld, 200)
        sender.sendMessage("§7⛵ Teleporting to your island '${targetWorld.name}'")

        return true
    }
}
