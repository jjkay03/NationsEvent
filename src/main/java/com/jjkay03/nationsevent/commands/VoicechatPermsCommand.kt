package com.jjkay03.nationsevent.commands

import com.jjkay03.nationsevent.utils.PVPToggle
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class VoicechatPermsCommand: CommandExecutor, TabCompleter {

    private val voicechatSpeakPerm = "voicechat.speak"
    private val defaultLPGroup = "default"

    // Command
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        var voicechatPermsState: Boolean = false

        // Check if the first argument is "silent"
        if (args.isNotEmpty() && args[0].toLowerCase() == "on") { voicechatPermsState = true }
        else if (args.isNotEmpty() && args[0].toLowerCase() == "off") { voicechatPermsState = false }
        else { sender.sendMessage("§cInvalid argument, usage: /voicechatperms on/off"); return true }

        // Notify all players on the server
        Bukkit.getServer().onlinePlayers.forEach { player ->
            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f)
            player.sendMessage(
                if (voicechatPermsState) "§a\uD83D\uDD0A Voicechat has been ENABLED!"
                else "§c🔊 Voicechat has been DISABLED!"
            )
        }

        // Deal with perms
        if (voicechatPermsState) Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            "lp group $defaultLPGroup permission set $voicechatSpeakPerm true"
        )
        else Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            "lp group $defaultLPGroup permission set $voicechatSpeakPerm false"
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