package com.jjkay03.nationsevent.specific.ng4

import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

class NG4_SeasonSpecific : Listener {

    companion object {
        const val PERM_GROUP_WEREWOLF: String = "group.werewolf"
        const val PERM_GROUP_VILLAGER: String = "group.robber"
    }

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
}
