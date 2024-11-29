package com.jjkay03.nationsevent.specific.ng6

import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class NG6_GunBullet : Listener {

    // TODO: Make arrows dispawn on ground touch
    // TODO: Make arrows have bullet trails
    // TODO: Make bow and crossbow not take durability damage
    // TODO: Attempt to make arrows not lose velocity and go strait

    // Shot arrows deal extra damage (gun)
    @EventHandler
    fun onArrowHit(event: EntityDamageByEntityEvent) {
        if (event.damager !is Arrow) return // End if damage not caused by arrow
        val shooter = (event.damager as Arrow).shooter // Get shooter
        if (shooter !is Player) return // End if shooter not a player
        event.damage *= 3
    }

}