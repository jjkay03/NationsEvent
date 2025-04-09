package com.jjkay03.nationsevent.economy

import com.jjkay03.nationsevent.NationsEvent
import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.persistence.PersistentDataType

object EconomyTraderEntity : Listener {

    private val NAMESPACED_KEY_TRADER_ENTITY: NamespacedKey = NamespacedKey(NationsEvent.INSTANCE, "economy_trader_entity")

    @EventHandler
    fun onEntityInteract(event: PlayerInteractEntityEvent) {
        val entity = event.rightClicked
        if (entity !is Villager) return
        if (!entity.persistentDataContainer.has(NAMESPACED_KEY_TRADER_ENTITY, PersistentDataType.BYTE)) return

        //event.isCancelled = true
        event.player.sendMessage("§aJOHNNY DETECTED!")
    }

    // Function to spawn trader
    fun spawn(player: Player): Villager {
        val villager = player.world.spawn(player.location, Villager::class.java)
        villager.setAI(false)
        villager.isSilent = true
        villager.isInvulnerable = true
        villager.canPickupItems = false
        villager.villagerType = Villager.Type.SAVANNA
        villager.profession = Villager.Profession.LIBRARIAN
        villager.recipes = mutableListOf() // Remove all trades
        villager.customName(Component.text("Johnny"))
        villager.persistentDataContainer.set(NAMESPACED_KEY_TRADER_ENTITY, PersistentDataType.BYTE, 1)
        return villager
    }

}