package com.jjkay03.nationsevent.specific.ng4.commands

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.Sound

class NG5_WolfRageAllCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        // Only allow players to use this command
        if (sender !is Player) {
            sender.sendMessage("§cOnly players can use this command!")
            return true
        }

        // Iterate through all online players
        for (player in Bukkit.getOnlinePlayers()) {
            // Check if the player has the werewolf permission
            if (player.hasPermission(NG4_SeasonSpecific.PERM_GROUP_WEREWOLF)) {

                // Apply strength effect
                player.addPotionEffect(PotionEffect(PotionEffectType.STRENGTH, 24000, 0, false, false))

                // Send an action bar and message to the player
                player.sendActionBar(Component.text("§c\uD83D\uDC3A WOLF RAGE ACTIVATED (all wolfs)"))
                player.sendMessage("§7\uD83D\uDC3A Wolf rage has been activated for all wolfs - coordinate with admins and other wolfs to organise the attack")

                // Play the wolf howl sound for the player
                player.playSound(player.location, Sound.BLOCK_TRIAL_SPAWNER_OMINOUS_ACTIVATE, 1.0f, 1.0f)
            }
        }

        // Notify the sender that the command has been executed
        sender.sendMessage("§aWolf Rage activated for all werewolves!")

        return true
    }
}
