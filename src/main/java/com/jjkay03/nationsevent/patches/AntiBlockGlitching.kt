package com.jjkay03.nationsevent.patches

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.util.BoundingBox

class AntiBlockGlitching: Listener {

    // REGISTER IF ENABLED
    private val config = NationsEvent.INSTANCE.config
    private val featureEnabled: Boolean = config.getBoolean("patch-anti-block-glitching")
    init { if (featureEnabled) Bukkit.getPluginManager().registerEvents(this, NationsEvent.INSTANCE) }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (!event.isCancelled) return
        handleEvent(event.player, event.blockPlaced)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onBucketUse(event: PlayerBucketEmptyEvent) {
        if (!event.isCancelled) return
        handleEvent(event.player, event.block)
    }

    /**
     * Handles the event for a player placing a block.
     *
     * @param player the player interacting with the block
     * @param block the block being placed.
     */

    private fun handleEvent(player: Player, block: Block) {
        val type = block.type
        val shouldTP = if (isClimbable(type)) {
            val box = player.boundingBox
            val expandedBox = BoundingBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY + 1, box.maxZ)
            val blockBox = generateBlockBoundingBox(block.location)

            expandedBox.overlaps(blockBox)
        } else {
            val box = player.boundingBox
            val expandedBox = BoundingBox(box.minX - 1, box.minY - 1, box.minZ - 1, box.maxX + 1, box.maxY, box.maxZ + 1)
            val blockBox = generateBlockBoundingBox(block.location)

            expandedBox.overlaps(blockBox)
        }

        if (!shouldTP) return

        val hasSpace = getBlocksUnder(player).none {
            it.type.isSolid && it.boundingBox.overlaps(player.boundingBox)
        }

        if (hasSpace) player.teleport(player.location.clone().subtract(0.0, 1.0, 0.0))
    }

    /**
     * Returns a list of blocks underneath the given player.
     * The list includes the block on which the player is standing.
     *
     * @param player the player for whom to get the blocks underneath
     * @return a list of blocks underneath the player
     */
    private fun getBlocksUnder(player: Player): List<Block> {
        val boundingBox = player.boundingBox

        val playerBlockLoc = player.location.toBlockLocation()

        val locations = listOf(
            playerBlockLoc.clone(),
            Location(player.world, boundingBox.minX, boundingBox.minY - 1, boundingBox.minZ),
            Location(player.world, boundingBox.minX, boundingBox.minY - 1, boundingBox.maxZ - .0001),
            Location(player.world, boundingBox.maxX - .0001, boundingBox.minY - 1, boundingBox.minZ),
            Location(player.world, boundingBox.maxX - .0001, boundingBox.minY - 1, boundingBox.maxZ - .0001),
        )

        val blocks: MutableSet<Block> = HashSet()

        for (location in locations)
            blocks.add(location.world.getBlockAt(location))

        return blocks.toList()
    }

    /**
     * Determines if a block is climbable.
     *
     * @param type the Material type of the block
     * @return true if the block is climbable, false otherwise
     */

    private fun isClimbable(type: Material): Boolean =
        type == Material.LADDER
                || type == Material.VINE
                || type == Material.CAVE_VINES_PLANT
                || type == Material.WATER
                || type == Material.LAVA


    /**
     * Generates the bounding box for a block based on its location.
     *
     * @param location the location of the block
     * @return the BoundingBox of the block
     */
    private fun generateBlockBoundingBox(location: Location): BoundingBox =
        BoundingBox(
            location.x, location.y, location.z,
            location.x + 1.0, location.y + 1.0, location.z + 1.0
        )
}