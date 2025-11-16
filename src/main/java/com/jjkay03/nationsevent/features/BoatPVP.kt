package com.jjkay03.nationsevent.features

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.utils.Config
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Boat
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.vehicle.VehicleDestroyEvent

class BoatPVP : Listener {

    // Weapons that destroy boats without dropping (keywords for MATERIAL)
    private val boatDestroyWeaponKeywords = listOf("SWORD", "AXE", "TRIDENT", "MACE")

    // Projectiles that destroy boats without dropping
    private val boatDestroyProjectiles = listOf(
        EntityType.ARROW,
        EntityType.SPECTRAL_ARROW,
        EntityType.TRIDENT
    )

    // INITIALIZATION
    init {
        load(Config.FEATURES_BOAT_PVP_ENABLE)
    }

    // LOAD (If enabled in config)
    fun load(enabled: Boolean) {
        // End if feature disabled
        if (!enabled) return

        // Register event
        Bukkit.getPluginManager().registerEvents(this, NationsEvent.INSTANCE)

        // Console message
        NationsEvent.INSTANCE.logger.info("- Loading feature: ${this::class.simpleName}")
    }

    // Dismount player from boat when they take damage
    @EventHandler
    fun onPlayerDamage(event: EntityDamageEvent) {
        // End if feature disabled
        if (!Config.FEATURES_BOAT_PVP_DAMAGE_DISMOUNT) return

        // End if not player
        if (event.entity !is Player) return
        val player = event.entity as Player

        // End if not boat
        if (player.vehicle !is Boat) return

        // Dismount from boat
        player.vehicle?.removePassenger(player)
    }

    // Destroy boats when hit with weapons (don't drop item)
    @EventHandler
    fun onBoatDamage(event: VehicleDestroyEvent) {
        // End if feature disabled
        if (!Config.FEATURES_BOAT_PVP_WEAPON_DESTROY) return

        // End if not boat
        val boat = event.vehicle
        if (boat !is Boat) return

        // Get attacker
        val attacker = event.attacker

        // End if not player
        if (attacker !is Player) return

        // Check if weapon in hand matches destroy keywords
        val shouldDestroy = boatDestroyWeaponKeywords.any {
            attacker.inventory.itemInMainHand.type.name.contains(it)
        }

        // Destroy boat without dropping
        if (shouldDestroy) {
            destroyBoat(boat)
            event.isCancelled = true
        }
    }

    // Destroy boats when hit with projectiles (don't drop item)
    @EventHandler
    fun onBoatProjectileDamage(event: ProjectileHitEvent) {
        // End if feature disabled
        if (!Config.FEATURES_BOAT_PVP_WEAPON_DESTROY) return

        // End if not hit entity or not boat
        val boat = event.hitEntity
        if (boat !is Boat) return

        // Check if projectile type can destroy boats
        val projectile = event.entity
        if (projectile.type !in boatDestroyProjectiles) return

        // Destroy boat without dropping
        destroyBoat(boat)
        event.isCancelled = true
    }

    // Helper function to destroy boat
    private fun destroyBoat(boat: Boat) {
        boat.location.world?.playSound(boat.location, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1.0f, 0.9f)
        boat.remove()
    }

}