package com.jjkay03.nationsevent.specific.ng6

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.scheduler.BukkitRunnable

class NG6_GunBullet : Listener {

    // TODO: Attempt to make arrows not lose velocity and go strait

    private val gunItems = setOf(Material.BOW, Material.CROSSBOW)

    // Shot arrows deal extra damage (gun)
    @EventHandler
    fun onArrowHit(event: EntityDamageByEntityEvent) {
        if (event.damager !is Arrow) return // End if damage not caused by arrow
        val shooter = (event.damager as Arrow).shooter // Get shooter
        if (shooter !is Player) return // End if shooter not a player
        event.damage *= 3
    }

    // Delete arrows when they touch the ground
    @EventHandler
    fun onProjectileHit(event: ProjectileHitEvent) {
        if (event.entity is Arrow) event.entity.remove()
    }

    // Disable durability loss on gun items
    @EventHandler
    fun onItemDamage(event: PlayerItemDamageEvent) {
        if (event.item.type in gunItems) event.isCancelled = true
    }

    // Arrow particle trail
    //   - Start with 1s delay
    //   - Arrows will be tracked for 10s
    @EventHandler
    fun onArrowLaunch(event: ProjectileLaunchEvent) {
        val projectile = event.entity
        if (projectile !is Arrow) return // End if not arrow

        // Run task
        object : BukkitRunnable() {
            private var timer = 0 // Timer to track elapsed time
            override fun run() {
                // If the arrow is dead or has landed, stop the task
                if (projectile.isDead || projectile.isInBlock || timer >= 200) {cancel(); return }

                // Create particle effects at the arrow's current location
                val location = projectile.location
                location.world?.spawnParticle(Particle.CRIT, location, 1, 0.0, 0.0, 0.0, 0.05)

                // Increase timer
                timer++
            }
        }.runTaskTimer(NationsEvent.INSTANCE, 20L, 1L)
    }
}