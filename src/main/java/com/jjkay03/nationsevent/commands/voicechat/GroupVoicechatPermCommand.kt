package com.jjkay03.nationsevent.commands.voicechat

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.utils.LuckPermsUtils
import net.luckperms.api.model.group.Group
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class GroupVoicechatPermCommand : CommandExecutor, TabCompleter {

    // COMMAND
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // Show all groups that have voice chat disabled when no args are given
        if (args.isEmpty()) {
            val disabledGroups = NationsEvent.LP_GROUP_MANAGER.loadedGroups
                .filter { LuckPermsUtils.groupHasPermission(it, Saves.PERM_SIMPLE_VOICECHAT_SPEAK).not() }
                .map { it.name }

            if (disabledGroups.isEmpty()) {
                sender.sendMessage("§aAll groups have voice chat enabled")
            } else {
                sender.sendMessage("")
                sender.sendMessage("§e\uD83D\uDD07 §nGroups with voice chat disabled:")
                sender.sendMessage("§7${disabledGroups.joinToString(", ")}")
                sender.sendMessage("")
            }

            return true
        }

        // Get group
        val groupName = args[0]
        val group: Group? = LuckPermsUtils.getGroup(groupName)
        if (group == null) { sender.sendMessage("§cGroup '$groupName' not found!"); return true }

        // Deal with args
        when (args[1].lowercase()) {
            "on" -> {
                if (LuckPermsUtils.groupRemovePermission(group, Saves.PERM_SIMPLE_VOICECHAT_SPEAK, false)) { alertPlayers(true, group) }
                else { sender.sendMessage("§7Group '$groupName' already had voice chat enabled!") }
            }
            "off" -> {
                if (LuckPermsUtils.groupAddPermission(group, Saves.PERM_SIMPLE_VOICECHAT_SPEAK, false)) { alertPlayers(false, group) }
                else { sender.sendMessage("§7Group '$groupName' already had voice chat disabled!") }
            }
            else -> {
                sender.sendMessage("§cInvalid state '${args[1]}'. Use 'on' or 'off'")
            }
        }
        return true
    }

    // TAB COMPLETE
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        return when (args.size) {
            1 -> NationsEvent.LP_GROUP_MANAGER.loadedGroups
                .map { it.name }
                .filter { it.startsWith(args[0], ignoreCase = true) }
            2 -> listOf("on", "off").filter { it.startsWith(args[1], ignoreCase = true) }
            else -> emptyList()
        }
    }

    // Helper function to alert players in chat
    fun alertPlayers(status: Boolean, group: Group) {
        val message = if (status) { "§a🔊 Voice chat ENABLED for group '${group.name}'!" } else { "§a\uD83D\uDD07 Voice chat DISABLED for group '${group.name}'!" }
        Bukkit.getServer().onlinePlayers.forEach { player ->
            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f)
            player.sendMessage(message)
        }
    }
}
