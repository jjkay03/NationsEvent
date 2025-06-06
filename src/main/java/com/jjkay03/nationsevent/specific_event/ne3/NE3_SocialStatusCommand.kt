package com.jjkay03.nationsevent.specific_event.ne3

import com.jjkay03.nationsevent.Utils
import com.jjkay03.nationsevent.utils.LuckPermsUtils
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class NE3_SocialStatusCommand : CommandExecutor, TabCompleter {

    // COMMAND
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // Check arguments
        if (args.size != 2) {
            val options = NE3_SocialStatus.entries.joinToString("|") { it.name } + "|NONE"
            sender.sendMessage("§cUsage: /socialstatus <player> $options")
            return true
        }
        
        // End if player not found
        val target = Bukkit.getPlayer(args[0]) ?: run {
            sender.sendMessage("§cPlayer not found!")
            return true
        }

        // End if player can't get social status
        if (!target.hasPermission(NE3_SocialStatus.PERM_RECEIVE_STATUS)) {
            sender.sendMessage("§cThis player is not eligible for social status!")
            return true
        }

        val option = args[1].uppercase()
        when {
            option == "NONE" -> {
                removePlayerAllSocialStatus(target)
                sender.sendMessage("§aCleared ${target.name}'s social status")
                Utils.messagePlayerWithPerm(
                    "§7[§cRED-CHAT§7] §f${target.name} has been demoted to NO social status!",
                    "nationsevent.ne3.red-chat.view"
                )
            }
            else -> {
                try {
                    val status = NE3_SocialStatus.valueOf(option)
                    givePlayerSocialStatus(target, status)
                    sender.sendMessage("§aSet ${target.name}'s social status to $option")
                    Utils.messagePlayerWithPerm(
                        "§7[§cRED-CHAT§7] §f${target.name} has been promoted to social status §c$option§f!",
                        "nationsevent.ne3.red-chat.view"
                    )
                } catch (e: IllegalArgumentException) {
                    sender.sendMessage("§cInvalid status. Valid options: ${
                        NE3_SocialStatus.entries.joinToString(", ") { it.name }
                    }, NONE")
                }
            }
        }
        return true
    }

    // TAB COMPLETER
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        return when (args.size) {
            1 -> Bukkit.getOnlinePlayers().map { it.name }.filter { it.startsWith(args[0], true) }
            2 -> (NE3_SocialStatus.entries.map { it.name } + "NONE").filter { it.startsWith(args[1], true) }
            else -> emptyList()
        }
    }

    // Helper function to give a player the perm for a social status
    private fun givePlayerSocialStatus(player: Player, status: NE3_SocialStatus) {
        removePlayerAllSocialStatus(player)
        LuckPermsUtils.playerAddPermission(player, status.permission)
    }

    // Helper function to remove all social status of a player
    private fun removePlayerAllSocialStatus(player: Player) {
        NE3_SocialStatus.ALL_SOCIAL_STATUS_PERMS.forEach { perm ->
            LuckPermsUtils.playerRemovePermission(player, perm)
        }
    }
}