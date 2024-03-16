package com.jjkay03.nationsevent.utils

import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.util.*

class MilkTheGator: Listener {

    private val gatorUUID = "c97caa03-87a8-4c7d-8e6c-60d985939276"

    // Milk listener
    @EventHandler
    fun onPlayerRightClickEntity(event: PlayerInteractEntityEvent) {
        // Check if the interacting entity is a player
        val clickedEntity = event.rightClicked
        val player: Player = event.player

        // Check if player is gator
        if (clickedEntity is Player && clickedEntity.uniqueId == UUID.fromString(gatorUUID)) {

            // Check if the player is holding an empty bucket
            val itemInHand: ItemStack = player.inventory.itemInMainHand
            if (itemInHand.type == Material.BUCKET) {
                // Create the milk bucket with the desired properties
                val milkBucket = createMilkBucket()
                val bucketCount = itemInHand.amount

                // Replace the empty bucket with the named milk bucket
                player.inventory.setItemInMainHand(milkBucket)

                if (bucketCount > 1) {
                    val newStack = ItemStack(Material.BUCKET, bucketCount - 1)
                    player.inventory.addItem(newStack).forEach { (_, item) -> player.world.dropItem(player.location, item) }
                }

                // Play the milking sound to players around
                player.world.playSound(player.location, Sound.ENTITY_GOAT_SCREAMING_MILK, 1.0f, 1.0f)
            }
        }
    }

    // Function to create milk bucket
    private fun createMilkBucket(): ItemStack {
        val milkBucket = ItemStack(Material.MILK_BUCKET)
        val itemMeta: ItemMeta? = milkBucket.itemMeta
        itemMeta?.setDisplayName("§eGator Milk Bucket")
        itemMeta?.setCustomModelData(200)
        milkBucket.itemMeta = itemMeta
        return milkBucket
    }
}