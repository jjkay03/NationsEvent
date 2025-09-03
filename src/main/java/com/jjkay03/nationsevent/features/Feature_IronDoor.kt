package com.jjkay03.nationsevent.features

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.data.type.Door
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class Feature_IronDoor: Listener {
    private val config = NationsEvent.INSTANCE.config
    private val featureEnabled: Boolean = config.getBoolean("feature-iron-door")

    private val openIronDoorsPermission = "nationsevent.openirondoor"

    // REGISTER IF ENABLED
    init {
        if (featureEnabled) { Bukkit.getPluginManager().registerEvents(this, NationsEvent.INSTANCE) }
    }

    @EventHandler(ignoreCancelled = false)
    fun onPlayerInteract(event: PlayerInteractEvent)  {
        val player = event.player
        val block = event.clickedBlock

        // Check all condition to open door
        if (
            block != null
            && !player.isSneaking
            && event.hand == EquipmentSlot.HAND
            && event.action == Action.RIGHT_CLICK_BLOCK
            && block.type == Material.IRON_DOOR
            && player.hasPermission(openIronDoorsPermission)
        ) {
            // Open the door
            val blockData = block.blockData as Door
            // Open door if door closed
            if (!blockData.isOpen) {
                blockData.isOpen = true
                block.world.playSound(block.location, Sound.BLOCK_IRON_DOOR_OPEN, 1.0f, 1.0f)
            }
            // Close door if door is open
            else if (blockData.isOpen) {
                blockData.isOpen = false
                block.world.playSound(block.location, Sound.BLOCK_IRON_DOOR_CLOSE, 1.0f, 1.0f)
            }
            block.setBlockData(blockData, true) // Update block
            player.swingMainHand() // Swing player hand
            event.isCancelled = true // Cancel event
        }
    }
}