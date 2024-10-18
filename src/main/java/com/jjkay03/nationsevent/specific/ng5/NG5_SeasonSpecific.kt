package com.jjkay03.nationsevent.specific.ng4

import com.jjkay03.nationsevent.NationsEvent
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

        // Check if the killer has the werewolf permission
        if (killer.hasPermission(PERM_GROUP_WEREWOLF)) {
            // Play the wolf howl sound to all online players
            Bukkit.getOnlinePlayers().forEach { player ->
                player.playSound(player.location, Sound.ENTITY_WOLF_HOWL, 0.3f, 1.0f)
            }
        }
    }

    // EventHandler - Deal with milk buckets if GLOBAL_BLINDNESS is true
    @EventHandler
    fun globalBlindnessOnPlayerConsume(event: PlayerItemConsumeEvent) {
        val player = event.player
        if (!NG4_GlobalBlindnessCommand.GLOBAL_BLINDNESS) return // End of GLOBAL_BLINDNESS false
        if (event.item.type != Material.MILK_BUCKET) return // End if not milk bucket
        if (player.hasPermission(NationsEvent.PERM_STAFF) || player.hasPermission(PERM_GROUP_WEREWOLF)) return // End if player has bypass perm
        event.isCancelled = true
        player.sendMessage("§cYou cannot drink milk during global blindness!")
    }

    // EventHandler - Deal with login when GLOBAL_BLINDNESS is true
    @EventHandler
    fun globalBlindnessOnPlayerLogin(event: PlayerJoinEvent) {
        val player = event.player
        if (!NG4_GlobalBlindnessCommand.GLOBAL_BLINDNESS) return // End of GLOBAL_BLINDNESS false
        // Give player blindness
        if (player.hasPermission(NationsEvent.PERM_STAFF) || player.hasPermission(PERM_GROUP_WEREWOLF)) return // End if player has bypass perm
        player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, PotionEffect.INFINITE_DURATION, 0, false, false))
        player.sendMessage("§7You are affected by global blindness...")
    }

    // EventHandler - Deal with logout when GLOBAL_BLINDNESS is true
    @EventHandler
    fun globalBlindnessOnPlayerLogout(event: PlayerQuitEvent) {
        val player = event.player
        if (!NG4_GlobalBlindnessCommand.GLOBAL_BLINDNESS) return // End of GLOBAL_BLINDNESS false
        if (player.hasPotionEffect(PotionEffectType.BLINDNESS)) {
            player.removePotionEffect(PotionEffectType.BLINDNESS)
        }
    }

    // Hide player name tag (TAB API) if global blindness is on, when player login
    init {
        NationsEvent.TAB_INSTANCE.eventBus?.register(PlayerLoadEvent::class.java) { event ->
            val player = event.player
            if (NG4_GlobalBlindnessCommand.GLOBAL_BLINDNESS) NationsEvent.TAB_NAMETAG_MANAGER.hideNameTag(player)
        }
    }

}
