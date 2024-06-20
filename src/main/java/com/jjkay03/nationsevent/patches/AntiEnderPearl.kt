package com.jjkay03.nationsevent.patches

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerTeleportEvent

class AntiEnderPearl: Listener {

    // REGISTER IF ENABLED
    private val config = NationsEvent.INSTANCE.config
    private val featureEnabled: Boolean = config.getBoolean("patch-anti-ender-pearl")
    init { if (featureEnabled) Bukkit.getPluginManager().registerEvents(this, NationsEvent.INSTANCE) }


    @EventHandler
    fun onPlayerUseEnderPearl(event: PlayerTeleportEvent) {
        // End if the teleportation cause is not using an ender pearl
        if (event.cause != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return

        // End if player has bypass perm (staff)
        if (event.player.hasPermission(NationsEvent.PERM_STAFF)) return

        // Delete ender pearl
        event.isCancelled = true
        event.player.inventory.itemInMainHand.amount--
        event.player.sendMessage("§cEnder pearls are disabled")
    }

    @EventHandler
    fun onPlayerHoldEnderPearl(event: PlayerItemHeldEvent) {
        // End if player has bypass perm (staff)
        if (event.player.hasPermission(NationsEvent.PERM_STAFF)) return

        // End if item in hand is not ender peal
        if (event.player.inventory.getItem(event.newSlot)?.type == Material.ENDER_PEARL) return

        event.player.sendMessage("§cWarning: Ender pearls are disabled")
    }
}