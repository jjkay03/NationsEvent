package com.jjkay03.nationsevent.specific.ng6

import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.PlayerItemDamageEvent

class NG6_GunBullet : Listener {

    // TODO: Make arrows have bullet trails
    // TODO: Attempt to make arrows not lose velocity and go strait
    // DONE: Make arrows dispawn on ground touch
    // DONE: Make bow and crossbow not take durability damage

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
}