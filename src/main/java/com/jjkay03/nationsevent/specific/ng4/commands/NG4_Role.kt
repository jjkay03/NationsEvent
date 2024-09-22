package com.jjkay03.nationsevent.specific.ng4.commands

import com.jjkay03.nationsevent.specific.ng4.NG4_RolesEnum
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class NG4_Role : CommandExecutor {

    // Command
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        // End if sender is not a player
        if (sender !is Player) { sender.sendMessage("§cOnly players can use this command!"); return true }

        when {
            sender.hasPermission(NG4_RolesEnum.WEREWOLF.groupPerm) -> displayRole(sender, NG4_RolesEnum.WEREWOLF)
            sender.hasPermission(NG4_RolesEnum.LONEWOLF.groupPerm) -> displayRole(sender, NG4_RolesEnum.LONEWOLF)
            sender.hasPermission(NG4_RolesEnum.SEER.groupPerm) -> displayRole(sender, NG4_RolesEnum.SEER)
            sender.hasPermission(NG4_RolesEnum.WITCH.groupPerm) -> displayRole(sender, NG4_RolesEnum.WITCH)
            sender.hasPermission(NG4_RolesEnum.HUNTER.groupPerm) -> displayRole(sender, NG4_RolesEnum.HUNTER)
            sender.hasPermission(NG4_RolesEnum.CUPID.groupPerm) -> displayRole(sender, NG4_RolesEnum.CUPID)
            else -> displayRole(sender, NG4_RolesEnum.VILLAGER)
        }

        return true
    }

    private fun displayRole(player: Player, role: NG4_RolesEnum) {
        // MAYOR & LOVER
        if (player.hasPermission(NG4_RolesEnum.MAYOR.groupPerm) && player.hasPermission(NG4_RolesEnum.LOVER.groupPerm)) {
            player.sendTitle(role.displayName, "", 10, 100, 10)
            player.playSound(player.location, role.sound, 1f, 1f)
            player.sendMessage(""); player.sendMessage("")
            player.sendMessage("§e\uD83D\uDC65 Your role: ${role.displayName}§7, ${NG4_RolesEnum.MAYOR.displayName} §7& ${NG4_RolesEnum.LOVER.displayName}")
            player.sendMessage("")
            player.sendMessage("§eⓘ Role Info: §7${role.description}")
            player.sendMessage("")
            player.sendMessage("§e\uD83D\uDD14 Mayor Info: §7${NG4_RolesEnum.MAYOR.description}")
            player.sendMessage("")
            player.sendMessage("§e❤ Lover Info: §7${NG4_RolesEnum.LOVER.description}")
            player.sendMessage(""); player.sendMessage("")
        }
        // MAYOR
        else if (player.hasPermission(NG4_RolesEnum.MAYOR.groupPerm)) {
            player.sendTitle(role.displayName, "", 10, 100, 10)
            player.playSound(player.location, role.sound, 1f, 1f)
            player.sendMessage(""); player.sendMessage("")
            player.sendMessage("§e\uD83D\uDC65 Your role: ${role.displayName} §7& ${NG4_RolesEnum.MAYOR.displayName}")
            player.sendMessage("")
            player.sendMessage("§eⓘ Role Info: §7${role.description}")
            player.sendMessage("")
            player.sendMessage("§e\uD83D\uDD14 Mayor Info: §7${NG4_RolesEnum.MAYOR.description}")
            player.sendMessage(""); player.sendMessage("")
        }
        // LOVER
        else if (player.hasPermission(NG4_RolesEnum.LOVER.groupPerm)) {
            player.sendTitle(role.displayName, "", 10, 100, 10)
            player.playSound(player.location, role.sound, 1f, 1f)
            player.sendMessage(""); player.sendMessage("")
            player.sendMessage("§e\uD83D\uDC65 Your role: ${role.displayName} §7& ${NG4_RolesEnum.LOVER.displayName}")
            player.sendMessage("")
            player.sendMessage("§eⓘ Role Info: §7${role.description}")
            player.sendMessage("")
            player.sendMessage("§e❤ Lover Info: §7${NG4_RolesEnum.LOVER.description}")
            player.sendMessage(""); player.sendMessage("")
        }
        // 1 ROLE ONLY
        else {
            player.sendTitle(role.displayName, "", 10, 100, 10)
            player.playSound(player.location, role.sound, 1f, 1f)
            player.sendMessage(""); player.sendMessage("")
            player.sendMessage("§e\uD83D\uDC65 Your role: ${role.displayName}")
            player.sendMessage("")
            player.sendMessage("§eⓘ Role Info: §7${role.description}")
            player.sendMessage(""); player.sendMessage("")
        }


    }
}