package com.jjkay03.nationsevent.settings

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerTeleportEvent

class DisableEnderPearls: Listener {

    // REGISTER EVENTS
    init {
        Bukkit.getPluginManager().registerEvents(this, NationsEvent.INSTANCE)
        NationsEvent.INSTANCE.logger.info("- Loading gameplay setting: ${this::class.simpleName}")
    }

    // Handle player using ender pearl item
    @EventHandler
    fun onPlayerUseEnderPearl(event: PlayerInteractEvent) {
        // End if player has bypass perm (staff)
        if (event.player.hasPermission(Saves.PERM_STAFF)) return

        // Check if the action is a right-click (use) action
        if (!event.action.isRightClick) return

        // Check if the item is an Ender Pearl
        if (event.item?.type != Material.ENDER_PEARL) return

        // Delete ender pearl
        event.isCancelled = true
        event.player.sendMessage("§cEnder pearls are disabled")
    }

    // Handle player teleporting with ender pearl
    @EventHandler
    fun onPlayerTeleportEnderPearl(event: PlayerTeleportEvent) {
        // End if the teleportation cause is not using an ender pearl
        if (event.cause != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return

        // End if player has bypass perm (staff)
        if (event.player.hasPermission(Saves.PERM_STAFF)) return

        // Delete ender pearl
        event.isCancelled = true
        event.player.inventory.itemInMainHand.amount--
        event.player.sendMessage("§cTeleportation canceled ender pearls are disabled")
    }

    // Handle giving player warning that pearls are disabled when holding ender pearl item
    @EventHandler
    fun onPlayerHoldEnderPearl(event: PlayerItemHeldEvent) {
        // End if player has bypass perm (staff)
        if (event.player.hasPermission(Saves.PERM_STAFF)) return

        // End if item in hand is not ender pearl
        if (event.player.inventory.getItem(event.newSlot)?.type != Material.ENDER_PEARL) return

        event.player.sendMessage("§cWarning: Ender pearls are disabled")
    }

    // Handle enderman never dropping ender pearls
    @EventHandler
    fun onEndermanDeath(event: EntityDeathEvent) {
        if (event.entity.type != EntityType.ENDERMAN) return
        event.drops.clear()
    }
}