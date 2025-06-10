package com.jjkay03.nationsevent.commands.others

import com.jjkay03.nationsevent.Saves
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class GlobalChatCommand : CommandExecutor, TabCompleter {

    private val defaultGroupName = "default"

    // Command
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        var chatPermsState: Boolean = false

        // Check if the first argument is "on" or "off"
        if (args.isNotEmpty() && args[0].toLowerCase() == "on") { chatPermsState = true }
        else if (args.isNotEmpty() && args[0].toLowerCase() == "off") { chatPermsState = false }
        else { sender.sendMessage("§cInvalid argument, usage: /globalchat on/off"); return true }

        // Notify all players on the server
        Bukkit.getServer().onlinePlayers.forEach { player ->
            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f)
            player.sendMessage(
                if (chatPermsState) "§a\uD83D\uDCAC Global chat has been ENABLED!"
                else "§c\uD83D\uDCAC Global chat has been DISABLED!"
            )
        }

        // Deal with permissions
        if (chatPermsState) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                "lp group $defaultGroupName permission set ${Saves.PERM_USE_CHAT} true"
            )
        } else {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                "lp group $defaultGroupName permission set ${Saves.PERM_USE_CHAT} false"
            )
        }

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
