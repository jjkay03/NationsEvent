package com.jjkay03.nationsevent.specific.ng3.commands

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.specific.ng3.NG3_SeasonSpecific
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class NG3_VoicechatPermsPrisonersCommand: CommandExecutor, TabCompleter {

    private val voicechatSpeakPerm = "voicechat.speak"
    private val prisonerLPGroup = "prisoner"

    // Command
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        var voicechatPermsState: Boolean = false

        // Check if the first argument is "silent"
        if (args.isNotEmpty() && args[0].toLowerCase() == "on") { voicechatPermsState = true }
        else if (args.isNotEmpty() && args[0].toLowerCase() == "off") { voicechatPermsState = false }
        else { sender.sendMessage("§cInvalid argument, usage: /voicechatpermsprisoners on/off"); return true }

        // Notify staff, guard, prisoner and sender of command
        Bukkit.getServer().onlinePlayers.forEach { player ->
            if (player.hasPermission(NationsEvent.PERM_STAFF) ||
                player.hasPermission(NG3_SeasonSpecific.PERM_GROUP_COP) ||
                player.hasPermission(NG3_SeasonSpecific.PERM_GROUP_PRISONER) ||
                player == sender) {
                player.sendMessage(
                    if (voicechatPermsState) "§7\uD83D\uDD0A Prisoners Voicechat has been ENABLED by ${sender.name}"
                    else "§7\uD83D\uDD0A Prisoners Voicechat has been DISABLED by ${sender.name}"
                )
            }
        }

        // Deal with perms
        if (voicechatPermsState) Bukkit.dispatchCommand(
            Bukkit.getConsoleSender(),
            "lp group $prisonerLPGroup permission unset $voicechatSpeakPerm"
        )
        else Bukkit.dispatchCommand(
            Bukkit.getConsoleSender(),
            "lp group $prisonerLPGroup permission set $voicechatSpeakPerm false"
        )

        return true
    }

    // Tab complete
    override fun onTabComplete(sender: CommandSender, cmd: Command, alias: String, args: Array<out String>): List<String>? {
        if (args.size == 1) {
            val completions = mutableListOf("on", "off")
            return completions.filter { it.startsWith(args[0], ignoreCase = true) }
        }
        return null
    }
}