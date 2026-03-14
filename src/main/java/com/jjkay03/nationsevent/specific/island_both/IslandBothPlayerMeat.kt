package com.jjkay03.nationsevent.specific.island_both

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.specific.EventSpecific
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.FurnaceSmeltEvent
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

class IslandBothPlayerMeat : Listener {

    // INITIALIZATION
    init {
        NationsEvent.INSTANCE.server.pluginManager.registerEvents(this, NationsEvent.INSTANCE)
    }

    val meatName = Component.text("Suspicious Meat", NamedTextColor.WHITE)
    val cookedMeatName = Component.text("Cooked Suspicious Meat", NamedTextColor.WHITE)

    // Drop suspicious meat when player dies
    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        // End if not on hot or cold island
        val world = event.player.world
        if (world != EventSpecific.WORLD_NS8_HOT && world != EventSpecific.WORLD_NS8_COLD) return

        // Drop random amount of raw beef (8-15)
        val amount = Random.nextInt(8, 16)
        val suspiciousMeat = ItemStack(Material.BEEF, amount)

        // Rename to "Suspicious Meat"
        val meta = suspiciousMeat.itemMeta
        meta.displayName(meatName)
        suspiciousMeat.itemMeta = meta

        // Drop at player's location
        event.player.world.dropItemNaturally(event.player.location, suspiciousMeat)
    }

    // Rename cooked suspicious meat
    @EventHandler
    fun onMeatCook(event: FurnaceSmeltEvent) {
        // Check if the source item is raw beef with "Suspicious Meat" name
        val sourceItem = event.source
        if (sourceItem.type != Material.BEEF) return

        val sourceMeta = sourceItem.itemMeta ?: return
        val displayName = sourceMeta.displayName() ?: return

        // Check if it's named "Suspicious Meat"
        if (displayName == meatName) {
            // Change result to cooked beef with new name
            val cookedMeat = ItemStack(Material.COOKED_BEEF, 1)
            val cookedMeta = cookedMeat.itemMeta
            cookedMeta.displayName(cookedMeatName)
            cookedMeat.itemMeta = cookedMeta

            event.result = cookedMeat
        }
    }
}