package com.jjkay03.nationsevent.specific.island_hot

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.specific.EventSpecific
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockFertilizeEvent
import org.bukkit.event.block.BlockGrowEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.world.StructureGrowEvent
import kotlin.random.Random

class IslandHotPlants : Listener {

    // INITIALIZATION
    init {
        NationsEvent.INSTANCE.server.pluginManager.registerEvents(this, NationsEvent.INSTANCE)
    }

    // Prevent natural crop/plant growth
    @EventHandler
    fun onPlantGrow(event: BlockGrowEvent) {
        if (EventSpecific.WORLD_NS8_HOT != event.block.world) return
        event.isCancelled = true
    }

    // Bone meal does nothing but still gets consumed
    @EventHandler
    fun onBoneMeal(event: BlockFertilizeEvent) {
        // End if not on hot island
        if (EventSpecific.WORLD_NS8_HOT != event.block.world) return

        // Cancel the fertilize effect but bone meal is still consumed
        event.isCancelled = true

        // Play evaporation effect
        val location = event.block.location.add(0.5, 0.5, 0.5)
        event.block.world.playSound(location, Sound.ITEM_BONE_MEAL_USE, 0.5f, 2.0f)
        event.block.world.spawnParticle(Particle.SMOKE, location, 6, 0.3, 0.3, 0.3, 0.01)
    }

    // Prevent farmland creation with hoe
    @EventHandler
    fun onHoeUse(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK) return
        if (EventSpecific.WORLD_NS8_HOT != event.clickedBlock?.world) return

        // End if not a hoe
        val item = event.item ?: return
        val hoes = setOf(
            Material.WOODEN_HOE, Material.STONE_HOE, Material.COPPER_HOE, Material.IRON_HOE,
            Material.GOLDEN_HOE, Material.DIAMOND_HOE, Material.NETHERITE_HOE
        )
        if (item.type !in hoes) return

        // End if not tillable block
        val block = event.clickedBlock ?: return
        val tillableBlocks = setOf(Material.DIRT, Material.GRASS_BLOCK, Material.COARSE_DIRT, Material.DIRT_PATH)
        if (block.type !in tillableBlocks) return

        // Cancel farmland creation
        event.isCancelled = true

        // Play evaporation effect
        val location = block.location.add(0.5, 1.0, 0.5)
        block.world.spawnParticle(Particle.SMOKE, location, 6, 0.3, 0.1, 0.3, 0.01)
    }

    // Turn farmland back into dirt when right-clicked
    @EventHandler
    fun onFarmlandInteract(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK) return
        if (EventSpecific.WORLD_NS8_HOT != event.clickedBlock?.world) return

        // End if not farmland
        val block = event.clickedBlock ?: return
        if (block.type != Material.FARMLAND) return

        // Cancel interaction
        event.isCancelled = true

        // Turn farmland back into dirt
        block.type = Material.DIRT

        // Play evaporation effect
        val location = block.location.add(0.5, 1.0, 0.5)
        block.world.spawnParticle(Particle.SMOKE, location, 6, 0.3, 0.1, 0.3, 0.01)
    }

    // Chance tree sapling dies on growth
    @EventHandler
    fun onTreeGrow(event: StructureGrowEvent) {
        // End if not on hot island
        if (EventSpecific.WORLD_NS8_HOT != event.world) return

        // 30% chance to turn into dead bush
        if (Random.nextInt(100) < 30) {
            // Cancel the tree growth
            event.isCancelled = true

            // Replace the sapling with a dead bush
            event.location.block.type = Material.DEAD_BUSH
        }
    }
}
