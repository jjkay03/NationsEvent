package com.jjkay03.nationsevent.specific.ng6.commands

import com.jjkay03.nationsevent.specific.ng6.NG6_RolesEnum
import com.jjkay03.nationsevent.specific.ng6.NG6_TeamsEnum
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class NG6_MafiaRageAllCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        // Only allow players to use this command
        if (sender !is Player) {
            sender.sendMessage("§cOnly players can use this command!")
            return true
        }

        // Iterate through all online players
        for (player in Bukkit.getOnlinePlayers()) {
            // Check if the player is in mafia killers team
            if (NG6_RolesEnum.getTeamRoles(NG6_TeamsEnum.MAFIA_KILLERS).any { player.hasPermission(it.groupPerm) }) {

                // Apply strength effect
                player.addPotionEffect(PotionEffect(PotionEffectType.STRENGTH, 24000, 0, false, false))

                // Send an action bar and message to the player + sound
                player.sendActionBar(Component.text("§c\uD83D\uDDE1 MAFIA RAGE ACTIVATED (all mafia)"))
                player.sendMessage("§7\uD83D\uDC3A Mafia rage has been activated for all mafia - coordinate with admins and other mafia members to organise the attack")
                player.playSound(player.location, Sound.BLOCK_TRIAL_SPAWNER_OMINOUS_ACTIVATE, 1.0f, 1.0f)
            }
        }

        // Notify the sender that the command has been executed
        sender.sendMessage("§aMafia Rage activated for all mafia members!")

        return true
    }
}