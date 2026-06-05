package com.jjkay03.nationsevent.specific.ne5.commands

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.specific.EventSpecific
import com.jjkay03.nationsevent.integrations.luckperms.LuckPermsUtils
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class MakeSoldierCommand(private val commandName: String) : CommandExecutor, TabCompleter {

    // INITIALIZATION
    init {
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
        NationsEvent.INSTANCE.getCommand(commandName)?.tabCompleter = this
    }

    // COMMAND
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) { sender.sendMessage("§cInvalid argument, usage: /$label <player>"); return true }

        // Get player
        val target = Bukkit.getPlayer(args[0])
        if (target == null) { sender.sendMessage("§cPlayer not found: ${args[0]}"); return true }

        // Get group
        val soldierGroup = EventSpecific.LP_GROUP_NE5_SOLDIER
        if (soldierGroup == null) { sender.sendMessage("§cSoldier group not found!"); return true }

        // Get sender name or console
        val senderName = if (sender is Player) sender.name else "Console"

        // Add or remove to group + notify
        if (LuckPermsUtils.isPlayerInGroup(target, soldierGroup)) {
            LuckPermsUtils.playerRemoveGroup(target, soldierGroup)
            target.sendMessage("§7⚔ You have been demoted from your soldier rank by $senderName")
            sender.sendMessage("§7⚔ ${target.name} has been demoted from soldier")
        } else {
            LuckPermsUtils.playerSetGroup(target, soldierGroup)
            target.sendMessage("§7⚔ You have been promoted to soldier by $senderName")
            sender.sendMessage("§7⚔ ${target.name} has been promoted to soldier")
        }

        return true
    }

    // TAB COMPLETER
    override fun onTabComplete(sender: CommandSender, cmd: Command, alias: String, args: Array<out String>): List<String>? {
        if (args.size == 1) return Bukkit.getOnlinePlayers().map { it.name }.filter { it.startsWith(args[0], ignoreCase = true) }
        return null
    }

}