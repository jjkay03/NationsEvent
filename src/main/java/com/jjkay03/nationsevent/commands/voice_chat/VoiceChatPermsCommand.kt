package com.jjkay03.nationsevent.commands.voice_chat

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.integrations.luckperms.LuckPermsUtils
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class VoiceChatPermsCommand(private val commandName: String): CommandExecutor, TabCompleter {

    // INITIALIZATION (Register command and events)
    init {
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
        NationsEvent.INSTANCE.getCommand(commandName)?.tabCompleter = this
    }

    // COMMAND
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        val invalidArgument = "§cInvalid argument, usage: /voicechatperms on/off"

        // Check 'default' luckperms group and args
        if (Saves.LP_GROUP_DEFAULT == null) { sender.sendMessage("§cDefault group in Saves class not found!"); return true }
        if (args.isEmpty()) { sender.sendMessage(invalidArgument); return true }

        // Deal with args
        when (args[0].lowercase()) {
            "on" -> {
                if (LuckPermsUtils.groupAddPermission(Saves.LP_GROUP_DEFAULT, Saves.PERM_SIMPLE_VOICE_CHAT_SPEAK, true)) { alertPlayers(true) }
                else { sender.sendMessage("§7Voicechat already enabled!") }
            }
            "off" -> {
                if (LuckPermsUtils.groupAddPermission(Saves.LP_GROUP_DEFAULT, Saves.PERM_SIMPLE_VOICE_CHAT_SPEAK, false)) { alertPlayers(false) }
                else { sender.sendMessage("§7Voicechat already disabled!") }
            }
            else -> {
                sender.sendMessage(invalidArgument)
            }
        }

        return true
    }

    // TAB COMPLETER
    override fun onTabComplete(sender: CommandSender, cmd: Command, alias: String, args: Array<out String>): List<String>? {
        if (args.size == 1) {
            val completions = mutableListOf("on", "off")
            return completions.filter { it.startsWith(args[0], ignoreCase = true) }
        }
        return null
    }

    // Helper function to alert players in chat
    fun alertPlayers(status: Boolean) {
        val message = if (status) { "§a\uD83D\uDD0A Voicechat has been ENABLED!" } else { "§c\uD83D\uDD07 Voicechat has been DISABLED!" }
        Bukkit.getServer().onlinePlayers.forEach { player ->
            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f)
            player.sendMessage(message)
        }
    }
}