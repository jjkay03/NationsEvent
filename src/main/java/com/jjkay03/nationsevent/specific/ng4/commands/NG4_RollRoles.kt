package com.jjkay03.nationsevent.specific.ng4.commands

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.specific.ng4.NG4_RolesEnum
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitScheduler

class NG4_RollRoles : CommandExecutor {

    // Command
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        // End if sender is not a player
        if (sender !is Player) { sender.sendMessage("§cOnly players can use this command!"); return true }

        val players = Bukkit.getOnlinePlayers()
        val scheduler: BukkitScheduler = Bukkit.getScheduler()

        // Apply the count and titles
        players.forEach { player ->

            // 3
            scheduler.runTaskLater(NationsEvent.INSTANCE, Runnable {
                player.sendTitle("§f3", "", 10, 40, 10)
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1f, 1f)
            }, 0)

            // 2
            scheduler.runTaskLater(NationsEvent.INSTANCE, Runnable {
                player.sendTitle("§f2", "", 10, 40, 10)
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1f, 1f)
            }, 40)

            // 1
            scheduler.runTaskLater(NationsEvent.INSTANCE, Runnable {
                player.sendTitle("§f1", "", 10, 40, 10)
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1f, 1f)
            }, 80)

            // You are...
            scheduler.runTaskLater(NationsEvent.INSTANCE, Runnable {
                player.sendTitle("§fYou are...", "", 10, 40, 10)
                player.playSound(player.location, Sound.EVENT_MOB_EFFECT_RAID_OMEN, 1f, 1f)
            }, 120)

            // Role reveal
            scheduler.runTaskLater(NationsEvent.INSTANCE, Runnable {
                when {
                    player.hasPermission(NG4_RolesEnum.WEREWOLF.groupPerm) -> revealRole(player, NG4_RolesEnum.WEREWOLF)
                    player.hasPermission(NG4_RolesEnum.LONEWOLF.groupPerm) -> revealRole(player, NG4_RolesEnum.LONEWOLF)
                    player.hasPermission(NG4_RolesEnum.SEER.groupPerm) -> revealRole(player, NG4_RolesEnum.SEER)
                    player.hasPermission(NG4_RolesEnum.WITCH.groupPerm) -> revealRole(player, NG4_RolesEnum.WITCH)
                    player.hasPermission(NG4_RolesEnum.HUNTER.groupPerm) -> revealRole(player, NG4_RolesEnum.HUNTER)
                    player.hasPermission(NG4_RolesEnum.CUPID.groupPerm) -> revealRole(player, NG4_RolesEnum.CUPID)
                    else -> revealRole(player, NG4_RolesEnum.VILLAGER)
                }
            }, 200)
        }

        return true
    }

    private fun revealRole(player: Player, role: NG4_RolesEnum) {
        player.sendTitle(role.displayName, "", 10, 200, 10)
        player.playSound(player.location, role.sound, 1f, 1f)
        player.sendMessage("")
        player.sendMessage("§e\uD83D\uDC65 Your role: ${role.displayName}")
        player.sendMessage("§eⓘ Role Info: §7${role.description}")
        player.sendMessage("")
    }

}