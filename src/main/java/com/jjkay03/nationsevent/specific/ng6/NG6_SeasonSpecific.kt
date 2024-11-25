package com.jjkay03.nationsevent.specific.ng6

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.specific.ng6.commands.*
import me.neznamy.tab.api.event.player.PlayerLoadEvent
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class NG6_SeasonSpecific : Listener {

    // EventHandler - Deal with milk buckets if GLOBAL_BLINDNESS is true
    @EventHandler
    fun globalBlindnessOnPlayerConsume(event: PlayerItemConsumeEvent) {
        val player = event.player
        if (!NG6_GlobalBlindnessCommand.GLOBAL_BLINDNESS) return // End of GLOBAL_BLINDNESS false
        if (event.item.type != Material.MILK_BUCKET) return // End if not milk bucket
        val playerTeam = NG6_RolesEnum.getPlayerRole(player).team
        if (player.hasPermission(Saves.PERM_STAFF) || playerTeam == NG6_TeamsEnum.MAFIA_KILLERS || playerTeam == NG6_TeamsEnum.SOLITARIES ) return  // End if player has bypass perm
        event.isCancelled = true
        player.sendMessage("§cYou cannot drink milk during global blindness!")
    }

    // EventHandler - Deal with login when GLOBAL_BLINDNESS is true
    @EventHandler
    fun globalBlindnessOnPlayerLogin(event: PlayerJoinEvent) {
        val player = event.player
        if (!NG6_GlobalBlindnessCommand.GLOBAL_BLINDNESS) return // End of GLOBAL_BLINDNESS false
        // Give player blindness
        val playerTeam = NG6_RolesEnum.getPlayerRole(player).team
        if (player.hasPermission(Saves.PERM_STAFF) || playerTeam == NG6_TeamsEnum.MAFIA_KILLERS || playerTeam == NG6_TeamsEnum.SOLITARIES ) return // End if player has bypass perm
        player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, PotionEffect.INFINITE_DURATION, 0, false, false))
        player.sendMessage("§7You are affected by global blindness...")
    }

    // EventHandler - Deal with logout when GLOBAL_BLINDNESS is true
    @EventHandler
    fun globalBlindnessOnPlayerLogout(event: PlayerQuitEvent) {
        val player = event.player
        if (!NG6_GlobalBlindnessCommand.GLOBAL_BLINDNESS) return // End of GLOBAL_BLINDNESS false
        if (player.hasPotionEffect(PotionEffectType.BLINDNESS)) {
            player.removePotionEffect(PotionEffectType.BLINDNESS)
        }
    }

    // Hide player name tag and fill TAB header (TAB API) if global blindness is on, when player login
    init {
        NationsEvent.TAB_INSTANCE.eventBus?.register(PlayerLoadEvent::class.java) { event ->
            val player = event.player
            if (NG6_GlobalBlindnessCommand.GLOBAL_BLINDNESS) {
                NationsEvent.TAB_NAMETAG_MANAGER.hideNameTag(player)
                NationsEvent.TAB_HEADER_FOOTER_MANAGER.setHeader(player, NG6_GlobalBlindnessCommand.HIDE_STRING)
            }

        }
    }
}