package com.jjkay03.nationsevent.specific.ne3

import com.jjkay03.nationsevent.Utils
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class NE3_SocialStatus : CommandExecutor, TabCompleter {

    companion object{
        const val PERM_RECEIVE_STATUS = "nationsevent.ne3.receive-status"

        const val PERM_SOCIAL_STATUS_LEADER = "nationsevent.ne3.social-status.leader"
        const val PERM_SOCIAL_STATUS_TIER_1 = "nationsevent.ne3.social-status.1"
        const val PERM_SOCIAL_STATUS_TIER_2 = "nationsevent.ne3.social-status.2"
        const val PERM_SOCIAL_STATUS_TIER_3 = "nationsevent.ne3.social-status.3"
        val ALL_SOCIAL_STATUS_PERMS = listOf<String>(PERM_SOCIAL_STATUS_LEADER, PERM_SOCIAL_STATUS_TIER_1, PERM_SOCIAL_STATUS_TIER_2, PERM_SOCIAL_STATUS_TIER_3)

        const val ICON_SOCIAL_STATUS_LEADER = "\uD83D\uDC51"
        const val ICON_SOCIAL_STATUS_1 = "✵ "
        const val ICON_SOCIAL_STATUS_2 = "✯ "
        const val ICON_SOCIAL_STATUS_3 = "⭐ "

        // Function to get icon social status of a player
        fun getSocialStatusIcon(player: Player): String {
            if (!player.hasPermission(PERM_RECEIVE_STATUS)) return ""
            return when {
                player.hasPermission(PERM_SOCIAL_STATUS_LEADER) -> ICON_SOCIAL_STATUS_LEADER
                player.hasPermission(PERM_SOCIAL_STATUS_TIER_1) -> ICON_SOCIAL_STATUS_1
                player.hasPermission(PERM_SOCIAL_STATUS_TIER_2) -> ICON_SOCIAL_STATUS_2
                player.hasPermission(PERM_SOCIAL_STATUS_TIER_3) -> ICON_SOCIAL_STATUS_3
                else -> ""
            }
        }
    }


    // COMMAND
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // End if no args
        if (args.size != 2) { sender.sendMessage("§cUsage: /socialstatus <player> T1|T2|T3|NONE"); return true }

        // End if player not found
        val target = Bukkit.getPlayer(args[0])
        if (target == null) { sender.sendMessage("§cPlayer not found!"); return true }

        // End if player doesn't have perm PERM_RECEIVE_STATUS
        if (!target.hasPermission(PERM_RECEIVE_STATUS)) { sender.sendMessage("§cThis player is not eligible for social status!"); return true }

        val option = args[1].uppercase()
        val redChatMessagePromote = "§7[§cRED-CHAT§7] §f${target.name} has been promoted to social status §c$option §f!"
        val redChatMessageDemote = "§7[§cRED-CHAT§7] §f${target.name} has been demoted to NO social status !"

        when(option) {
            "T1" -> {
                givePlayerTier(target, PERM_SOCIAL_STATUS_TIER_1)
                sender.sendMessage("§aSet ${target.name}'s social status to $option")
                Utils.messagePlayerWithPerm(redChatMessagePromote, "nationsevent.ne3.red-chat.view")
            }
            "T2" -> {
                givePlayerTier(target, PERM_SOCIAL_STATUS_TIER_2)
                sender.sendMessage("§aSet ${target.name}'s social status to $option")
                Utils.messagePlayerWithPerm(redChatMessagePromote, "nationsevent.ne3.red-chat.view")
            }
            "T3" -> {
                givePlayerTier(target, PERM_SOCIAL_STATUS_TIER_3)
                sender.sendMessage("§aSet ${target.name}'s social status to $option")
                Utils.messagePlayerWithPerm(redChatMessagePromote, "nationsevent.ne3.red-chat.view")
            }
            "NONE" -> {
                removePlayerTiers(target)
                sender.sendMessage("§aCleared ${target.name}'s social status")
                Utils.messagePlayerWithPerm(redChatMessageDemote, "nationsevent.ne3.red-chat.view")
            }
            else -> { sender.sendMessage("§cInvalid status, use T1, T2, T3, or NONE") }
        }
        return true
    }

    // TAB COMPLETER
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        return when (args.size) {
            1 -> Bukkit.getOnlinePlayers().map { it.name }.filter { it.startsWith(args[0], ignoreCase = true) }
            2 -> listOf("T1", "T2", "T3", "NONE").filter { it.startsWith(args[1], ignoreCase = true) }
            else -> emptyList()
        }
    }

    // Helper function to give a player a tier
    fun givePlayerTier(player: Player, permission: String) {
        removePlayerTiers(player)
        Utils.luckPermsPlayerAddPermission(player, permission)
    }

    // Helper function to remove a players tiers
    fun removePlayerTiers(player: Player) {
        for (perm in ALL_SOCIAL_STATUS_PERMS) { Utils.luckPermsPlayerRemovePermission(player, perm) }
    }
}
