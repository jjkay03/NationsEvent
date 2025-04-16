package com.jjkay03.nationsevent.economy

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe
import org.bukkit.persistence.PersistentDataType

object EconomyTraderEntity : Listener {

    private val NAMESPACED_KEY_TRADER_ENTITY: NamespacedKey = NamespacedKey(NationsEvent.INSTANCE, "economy_trader_entity")

    @EventHandler
    fun onEntityInteract(event: PlayerInteractEntityEvent) {
        // Checks
        val entity = event.rightClicked
        if (entity !is Villager) return
        if (!entity.persistentDataContainer.has(NAMESPACED_KEY_TRADER_ENTITY, PersistentDataType.BYTE)) return

        // Cancel event
        event.isCancelled = true

        // Check player permission
        if (!event.player.hasPermission(Saves.PERM_USE_ECONOMY_TRADE_ENTITY)) {event.player.sendMessage("§cYou don't have permission to use the trade entity!"); return}

        // Make the villager face the player
        val villagerLocation = entity.location
        villagerLocation.direction = event.player.location.toVector().subtract(villagerLocation.toVector()).normalize()
        entity.teleport(villagerLocation)

        // Play effects
        val itemsTotalPrice = EconomyItems.sellPlayerItems(event.player)
        if (itemsTotalPrice <= 0) event.player.world.playSound(event.player.location, Sound.ENTITY_VILLAGER_NO, 1f, 1f)
        else {
            event.player.world.playSound(event.player.location, Sound.ENTITY_VILLAGER_TRADE, 1f, 1f)
            entity.world.spawnParticle(Particle.HAPPY_VILLAGER, entity.location.add(0.0, 1.0, 0.0), 10, .3, .5, .3)
        }
    }


    // Function to spawn trader
    fun spawn(player: Player): Villager {
        val villager = player.world.spawn(player.location, Villager::class.java)
        villager.setAI(false)
        villager.isSilent = true
        villager.isInvulnerable = true
        villager.canPickupItems = false
        villager.recipes = mutableListOf() // Remove all trades
        villager.villagerType = Villager.Type.SAVANNA
        villager.profession = Villager.Profession.LIBRARIAN
        villager.getAttribute(Attribute.SCALE)!!.baseValue = 1.2
        villager.customName(Component.text("Johnny"))
        villager.persistentDataContainer.set(NAMESPACED_KEY_TRADER_ENTITY, PersistentDataType.BYTE, 1)

        // Create custom trade (barrier for barrier)
        val trade = MerchantRecipe(ItemStack(Material.BARRIER), 9999)
        trade.addIngredient(ItemStack(Material.BARRIER))
        villager.recipes = listOf(trade)

        return villager
    }

}