package com.jjkay03.nationsevent.specific.ng5.commands

import com.jjkay03.nationsevent.specific.ng5.NG5_RolesEnum
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class NG5_Role : CommandExecutor {

    // Command
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        // End if sender is not a player
        if (sender !is Player) { sender.sendMessage("§cOnly players can use this command!"); return true }

        when {
            sender.hasPermission(NG5_RolesEnum.WEREWOLF.groupPerm) -> displayRole(sender, NG5_RolesEnum.WEREWOLF)
            sender.hasPermission(NG5_RolesEnum.LONEWOLF.groupPerm) -> displayRole(sender, NG5_RolesEnum.LONEWOLF)
            sender.hasPermission(NG5_RolesEnum.SEER.groupPerm) -> displayRole(sender, NG5_RolesEnum.SEER)
            sender.hasPermission(NG5_RolesEnum.WITCH.groupPerm) -> displayRole(sender, NG5_RolesEnum.WITCH)
            sender.hasPermission(NG5_RolesEnum.HUNTER.groupPerm) -> displayRole(sender, NG5_RolesEnum.HUNTER)
            sender.hasPermission(NG5_RolesEnum.CUPID.groupPerm) -> displayRole(sender, NG5_RolesEnum.CUPID)
            else -> displayRole(sender, NG5_RolesEnum.VILLAGER)
        }

        return true
    }

    private fun displayRole(player: Player, role: NG5_RolesEnum) {
        // MAYOR & LOVER
        if (player.hasPermission(NG5_RolesEnum.MAYOR.groupPerm) && player.hasPermission(NG5_RolesEnum.LOVER.groupPerm)) {
            player.sendTitle(role.displayName, "", 10, 100, 10)
            player.playSound(player.location, role.sound, 1f, 1f)
            player.sendMessage(""); player.sendMessage("")
            player.sendMessage("§e\uD83D\uDC65 Your role: ${role.displayName}§7, ${NG5_RolesEnum.MAYOR.displayName} §7& ${NG5_RolesEnum.LOVER.displayName}")
            player.sendMessage("")
            player.sendMessage("§eⓘ Role Info: §7${role.description}")
            player.sendMessage("")
            player.sendMessage("§e\uD83D\uDD14 Mayor Info: §7${NG5_RolesEnum.MAYOR.description}")
            player.sendMessage("")
            player.sendMessage("§e❤ Lover Info: §7${NG5_RolesEnum.LOVER.description}")
            player.sendMessage(""); player.sendMessage("")
        }
        // MAYOR
        else if (player.hasPermission(NG5_RolesEnum.MAYOR.groupPerm)) {
            player.sendTitle(role.displayName, "", 10, 100, 10)
            player.playSound(player.location, role.sound, 1f, 1f)
            player.sendMessage(""); player.sendMessage("")
            player.sendMessage("§e\uD83D\uDC65 Your role: ${role.displayName} §7& ${NG5_RolesEnum.MAYOR.displayName}")
            player.sendMessage("")
            player.sendMessage("§eⓘ Role Info: §7${role.description}")
            player.sendMessage("")
            player.sendMessage("§e\uD83D\uDD14 Mayor Info: §7${NG5_RolesEnum.MAYOR.description}")
            player.sendMessage(""); player.sendMessage("")
        }
        // LOVER
        else if (player.hasPermission(NG5_RolesEnum.LOVER.groupPerm)) {
            player.sendTitle(role.displayName, "", 10, 100, 10)
            player.playSound(player.location, role.sound, 1f, 1f)
            player.sendMessage(""); player.sendMessage("")
            player.sendMessage("§e\uD83D\uDC65 Your role: ${role.displayName} §7& ${NG5_RolesEnum.LOVER.displayName}")
            player.sendMessage("")
            player.sendMessage("§eⓘ Role Info: §7${role.description}")
            player.sendMessage("")
            player.sendMessage("§e❤ Lover Info: §7${NG5_RolesEnum.LOVER.description}")
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