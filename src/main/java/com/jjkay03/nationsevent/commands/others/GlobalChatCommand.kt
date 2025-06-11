package com.jjkay03.nationsevent.commands.others

import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.utils.LuckPermsUtils
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class GlobalChatCommand : CommandExecutor, TabCompleter {

    // COMMAND
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        val invalidArgument = "§cInvalid argument, usage: /globalchat on/off"

        // Check 'default' luckperms group and args
        if (Saves.LP_GROUP_DEFAULT == null) { sender.sendMessage("§cDefault group in Saves class not found!"); return true }
        if (args.isEmpty()) { sender.sendMessage(invalidArgument); return true }

        // Handle arguments using when
        when (args[0].lowercase()) {
            "on" -> {
                if (LuckPermsUtils.groupAddPermission(Saves.LP_GROUP_DEFAULT, Saves.PERM_USE_CHAT, true)) { alertPlayers(true) }
                else { sender.sendMessage("§7Global chat already enabled!") }
            }
            "off" -> {
                if (LuckPermsUtils.groupAddPermission(Saves.LP_GROUP_DEFAULT, Saves.PERM_USE_CHAT, false)) { alertPlayers(false) }
                else { sender.sendMessage("§7Global chat already disabled!") }
            }
            else -> {
                sender.sendMessage("§cInvalid argument, usage: /globalchat on/off")
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

    // Helper function to alert players
    private fun alertPlayers(status: Boolean) {
        val message = if (status) { "§a\uD83D\uDCAC Global chat has been ENABLED!" } else { "§c\uD83D\uDCAC Global chat has been DISABLED!" }
        Bukkit.getServer().onlinePlayers.forEach { player ->
            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f)
            player.sendMessage(message)
        }
    }
}