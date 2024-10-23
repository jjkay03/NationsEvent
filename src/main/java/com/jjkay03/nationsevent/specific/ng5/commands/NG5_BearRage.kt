package com.jjkay03.nationsevent.specific.ng5.commands

import com.jjkay03.nationsevent.specific.ng5.NG5_RolesEnum
import com.jjkay03.nationsevent.specific.ng5.NG5_TeamsEnum
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class NG5_BearRage: CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        // Check if the command is being used correctly
        if (args.size != 1) { sender.sendMessage("§cUsage: /bearrage <player>"); return true }

        // Get the player by their name
        val targetPlayer: Player? = Bukkit.getPlayer(args[0])

        // Check if the player is online
        if (targetPlayer == null) { sender.sendMessage("§cPlayer ${args[0]} is not online!"); return true }

        // Check if the player has bear perm
        if (targetPlayer.hasPermission(NG5_RolesEnum.BEAR.groupPerm)) {
            sender.sendMessage("§cPlayer ${targetPlayer.name} is not a Bear!")
            return true
        }

        // Apply Strength 3 for 2 minutes (2400 ticks) without particles
        val strengthEffect = PotionEffect(PotionEffectType.STRENGTH, 2400, 2, false, false)
        targetPlayer.addPotionEffect(strengthEffect)

        // Send an action bar message and sound to the player
        targetPlayer.sendActionBar(Component.text("§5\uD83D\uDC3B BEAR RAGE ACTIVATED"))
        targetPlayer.playSound(targetPlayer.location, Sound.BLOCK_TRIAL_SPAWNER_DETECT_PLAYER, 1f, 1f)

        sender.sendMessage("§aBear rage activated for ${targetPlayer.name}")
        return true
    }
}