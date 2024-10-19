package com.jjkay03.nationsevent.specific.ng5

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Utils
import com.jjkay03.nationsevent.specific.ng5.commands.NG5_GlobalBlindnessCommand
import me.neznamy.tab.api.event.player.PlayerLoadEvent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class NG5_SeasonSpecific : Listener {

    companion object {
         val PERM_GROUP_WEREWOLF: String = NG5_RolesEnum.WEREWOLF.groupPerm
    }

    // EventHandler - Play wolf sound when wolf get a kill
    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val killer = event.entity.killer ?: return
        val dead = event.entity

        // If killer is wolf
        if (NG5_RolesEnum.getPlayerRole(killer).team == NG5_TeamsEnum.WOLVES_KILLERS) {
            Utils.playSoundToAllPlayers(Sound.ENTITY_WOLF_HOWL, .3f, 1f)
        }
        // If killer is bear
        else if (NG5_RolesEnum.getPlayerRole(killer) == NG5_RolesEnum.BEAR) {
            Utils.playSoundToAllPlayers(Sound.ENTITY_RAVAGER_CELEBRATE, .75f, 1f)
        }
        // If killer is assassin
        else if (NG5_RolesEnum.getPlayerRole(killer) == NG5_RolesEnum.ASSASSIN) {
            Utils.playSoundToAllPlayers(Sound.ENTITY_ARROW_HIT, .3f, .5f)
        }

        // If wolf dies
        if (NG5_RolesEnum.getPlayerRole(dead).team == NG5_TeamsEnum.WOLVES_KILLERS || NG5_RolesEnum.getPlayerRole(dead).team == NG5_TeamsEnum.WOLVES) {
            dead.world.playSound(dead.location, Sound.ENTITY_WOLF_DEATH, 1f, .5f)
        }
        // If bear dies
        else if (NG5_RolesEnum.getPlayerRole(dead) == NG5_RolesEnum.BEAR) {
            dead.world.playSound(dead.location, Sound.ENTITY_RAVAGER_DEATH, 1f, .5f)
        }

    }

    // EventHandler - Deal with milk buckets if GLOBAL_BLINDNESS is true
    @EventHandler
    fun globalBlindnessOnPlayerConsume(event: PlayerItemConsumeEvent) {
        val player = event.player
        if (!NG5_GlobalBlindnessCommand.GLOBAL_BLINDNESS) return // End of GLOBAL_BLINDNESS false
        if (event.item.type != Material.MILK_BUCKET) return // End if not milk bucket
        if (player.hasPermission(NationsEvent.PERM_STAFF) || NG5_RolesEnum.getPlayerRole(player).team == NG5_TeamsEnum.WOLVES_KILLERS) return // End if player has bypass perm
        event.isCancelled = true
        player.sendMessage("§cYou cannot drink milk during global blindness!")
    }

    // EventHandler - Deal with login when GLOBAL_BLINDNESS is true
    @EventHandler
    fun globalBlindnessOnPlayerLogin(event: PlayerJoinEvent) {
        val player = event.player
        if (!NG5_GlobalBlindnessCommand.GLOBAL_BLINDNESS) return // End of GLOBAL_BLINDNESS false
        // Give player blindness
        val playerTeam = NG5_RolesEnum.getPlayerRole(player).team
        if (player.hasPermission(NationsEvent.PERM_STAFF) || playerTeam == NG5_TeamsEnum.WOLVES_KILLERS || playerTeam == NG5_TeamsEnum.SOLITARIES ) return // End if player has bypass perm
        player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, PotionEffect.INFINITE_DURATION, 0, false, false))
        player.sendMessage("§7You are affected by global blindness...")
    }

    // EventHandler - Deal with logout when GLOBAL_BLINDNESS is true
    @EventHandler
    fun globalBlindnessOnPlayerLogout(event: PlayerQuitEvent) {
        val player = event.player
        if (!NG5_GlobalBlindnessCommand.GLOBAL_BLINDNESS) return // End of GLOBAL_BLINDNESS false
        if (player.hasPotionEffect(PotionEffectType.BLINDNESS)) {
            player.removePotionEffect(PotionEffectType.BLINDNESS)
        }
    }

    // Hide player name tag and fill TAB header (TAB API) if global blindness is on, when player login
    init {
        NationsEvent.TAB_INSTANCE.eventBus?.register(PlayerLoadEvent::class.java) { event ->
            val player = event.player
            if (NG5_GlobalBlindnessCommand.GLOBAL_BLINDNESS) {
                NationsEvent.TAB_NAMETAG_MANAGER.hideNameTag(player)
                NationsEvent.TAB_HEADER_FOOTER_MANAGER.setHeader(player, NG5_GlobalBlindnessCommand.HIDE_STRING)
            }

        }
    }

}
