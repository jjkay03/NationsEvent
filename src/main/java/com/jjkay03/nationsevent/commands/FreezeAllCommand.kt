package com.jjkay03.nationsevent.commands

import com.jjkay03.nationsevent.commands.voting.VoteCommand
import com.jjkay03.nationsevent.utils.FreezeAll
import com.jjkay03.nationsevent.utils.PVPToggle
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class FreezeAllCommand : CommandExecutor {

    // Command
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {

        // Flip FREEZE_ALL_ENABLED bool
        FreezeAll.FREEZE_ALL_ENABLED = !FreezeAll.FREEZE_ALL_ENABLED

        // Notify all players on the server
        Bukkit.getServer().onlinePlayers.forEach { player ->
            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f)
            player.sendMessage(
                if (FreezeAll.FREEZE_ALL_ENABLED) "§c❄ All players have been FROZEN!"
                else "§a❄ All players are UNFROZEN!"
            )
        }

        return true
    }
}