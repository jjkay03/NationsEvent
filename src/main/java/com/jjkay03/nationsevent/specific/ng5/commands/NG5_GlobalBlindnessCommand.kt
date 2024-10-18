package com.jjkay03.nationsevent.specific.ng5.commands

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.specific.ng5.NG5_SeasonSpecific
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class NG5_GlobalBlindnessCommand : CommandExecutor, TabCompleter {

    private val hideString = mutableListOf<String>().apply {
        (0..100).forEach { _ ->
            add("§c§lNO TAB FOR YOU\n")
            add("§4§lTHE WOLVES ARE OUT TO GET YOU\n")
            add("§6§lSUB TO jjkay03 !\n")
        } }.joinToString("\n")

    companion object {
        var GLOBAL_BLINDNESS = false
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // Check for correct arguments on or off
        if (args.isEmpty() || (args[0] != "on" && args[0] != "off")) { sender.sendMessage("§cUsage: /globalblindness <on|off>"); return true }

        // Toggle global blindness on or off based on the argument
        if (args[0].equals("on", ignoreCase = true)) enableGlobalBlindness(sender)
        else if (args[0].equals("off", ignoreCase = true)) disableGlobalBlindness(sender)

        return true
    }

    // ENABLE - global blindness effect for all players except those with the bypass permission
    private fun enableGlobalBlindness(sender: CommandSender) {
        if (GLOBAL_BLINDNESS) { sender.sendMessage("§cGlobal blindness is already enabled!"); return }
        GLOBAL_BLINDNESS = true
        for (player in Bukkit.getOnlinePlayers()) {
            // Hide player list by filling TAB header
            tabFillHeader(player)
            // Hide player nametag using TAB API
            NationsEvent.TAB_NAMETAG_MANAGER?.hideNameTag(NationsEvent.TAB_INSTANCE.getPlayer(player.uniqueId)!!)
            // Skip player if player has bypass perm
            if (player.hasPermission(NationsEvent.PERM_STAFF) || player.hasPermission(NG5_SeasonSpecific.PERM_GROUP_WEREWOLF)) continue
            player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, PotionEffect.INFINITE_DURATION, 0, false, false))
            player.playSound(player.location, Sound.BLOCK_SCULK_SHRIEKER_SHRIEK, 1f, .5f)
            player.sendMessage("§7You are affected by global blindness...")
        }
        sender.sendMessage("§6Global blindness has been §aenabled §6for all players")
    }

    // DISABLE - blindness effect for all players who currently have it
    private fun disableGlobalBlindness(sender: CommandSender) {
        if (!GLOBAL_BLINDNESS) { sender.sendMessage("§cGlobal blindness is already disabled!"); return }
        GLOBAL_BLINDNESS = false
        for (player in Bukkit.getOnlinePlayers()) {
            // Un-hide player list by un-filling TAB header
            tabUnFillHeader(player)
            // Show player nametag using TAB API
            NationsEvent.TAB_NAMETAG_MANAGER?.showNameTag(NationsEvent.TAB_INSTANCE.getPlayer(player.uniqueId)!!)
            // Clear everyone bliness
            if (player.hasPotionEffect(PotionEffectType.BLINDNESS)) {
                player.removePotionEffect(PotionEffectType.BLINDNESS)
            }
        }
        sender.sendMessage("§6Global blindness has been §cdisabled §6for all players")
    }

    // Tab Completer - provide "on" and "off" as options for tab completion
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String>? {
        if (args.size == 1) {
            return listOf("on", "off").filter { it.startsWith(args[0], ignoreCase = true) }
        }
        return null
    }

    // Tab fill header
    private fun tabFillHeader(player: Player) {
        val tabPlayer = NationsEvent.TAB_INSTANCE.getPlayer(player.uniqueId)?: return
        NationsEvent.TAB_HEADER_FOOTER_MANAGER.setHeader(tabPlayer, hideString)
    }

    // Tab un-fill header
    private fun tabUnFillHeader(player: Player) {
        val tabPlayer = NationsEvent.TAB_INSTANCE.getPlayer(player.uniqueId)?: return
        NationsEvent.TAB_HEADER_FOOTER_MANAGER.setHeader(tabPlayer, null)
    }
}
