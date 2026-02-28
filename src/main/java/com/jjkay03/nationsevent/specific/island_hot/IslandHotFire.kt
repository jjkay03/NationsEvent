package com.jjkay03.nationsevent.specific.island_hot

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.specific.EventSpecific
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

class IslandHotFire : Listener {

    // INITIALIZATION
    init {
        NationsEvent.INSTANCE.server.pluginManager.registerEvents(this, NationsEvent.INSTANCE)
    }

    // Fire damage deals 25% more
    @EventHandler
    fun onFireDamage(event: EntityDamageEvent) {
        if (event.entity.world != EventSpecific.WORLD_NS8_HOT) return
        if (event.cause != EntityDamageEvent.DamageCause.FIRE
            && event.cause != EntityDamageEvent.DamageCause.FIRE_TICK
            && event.cause != EntityDamageEvent.DamageCause.LAVA) return
        event.damage *= 1.25
    }
}
