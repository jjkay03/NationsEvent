package com.jjkay03.nationsevent.worlds

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.utils.Config
import com.jjkay03.nationsevent.utils.Scheduler
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import java.util.*

/*
    This class creates seamless connections between multiple Minecraft worlds by detecting when
    players cross world boundaries and automatically teleporting them to the nearest connected world.

    How it works:
    1. Each world has defined boundaries using pos1 (top-left corner) and pos2 (bottom-right corner)
    2. When a player moves outside their current world's boundary, the system finds the nearest world
    3. The player is teleported to a safe location within the target world's boundaries
    4. Safe location calculation ensures players land on solid ground, not in air or underground

    Configuration:
    - pos1: Always the top-left corner of the world boundary (smaller X, smaller Z)
    - pos2: Always the bottom-right corner of the world boundary (larger X, larger Z)
 */

class WorldsBridge : Listener {

    companion object {
        var ALLOW_CROSS = true
    }

    // Player cooldown tracking
    private val cooldownTime = 60000L // 60 second cooldown
    private val playerCooldowns = mutableMapOf<UUID, Long>()

    // WORLD CONFIGURATION
    private val worlds = mapOf(
        "world_ns7_1_plains" to WorldBoundary(
            pos1 = Point(-3100, -3100),
            pos2 = Point(3200, 0)
        ),
        "world_ns7_2_desert" to WorldBoundary(
            pos1 = Point(-3100, 143),
            pos2 = Point(-161, 3615)
        ),
        "world_ns7_3_snow" to WorldBoundary(
            pos1 = Point(122, 143),
            pos2 = Point(3225, 3292)
        )
    )

    // Data classes
    data class Point(val x: Int, val z: Int)
    data class WorldBoundary(val pos1: Point, val pos2: Point) {
        val minX = minOf(pos1.x, pos2.x)
        val maxX = maxOf(pos1.x, pos2.x)
        val minZ = minOf(pos1.z, pos2.z)
        val maxZ = maxOf(pos1.z, pos2.z)
    }

    // INITIALIZATION
    init {
        load(Config.WORLDS_BRIDGE_ENABLE)
    }

    // LOAD (If enabled in config)
    fun load(enabled: Boolean) {
        if (!enabled) return
        Bukkit.getPluginManager().registerEvents(this, NationsEvent.INSTANCE)
        NationsEvent.INSTANCE.logger.info("- Loading world manager: ${this::class.simpleName}")
    }

    // PLAYER MOVE EVENT
    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player
        val location = event.to
        val worldName = location.world?.name ?: return

        // Check cooldown
        val now = System.currentTimeMillis()
        val lastTeleport = playerCooldowns[player.uniqueId] ?: 0
        if (now - lastTeleport < cooldownTime) {
            // Check if player is in a monitored world and outside boundary
            val boundary = worlds[worldName] ?: return
            if (isOutsideBoundary(location, boundary)) {
                val cooldownLeft = ((cooldownTime - (now - lastTeleport)) / 1000.0)
                player.sendMessage("§c⛵ Wait ${String.format("%.1f", cooldownLeft)}s before crossing again!")
            }
            return // Player is still in cooldown
        }

        // Check if player is in a monitored world
        val boundary = worlds[worldName] ?: return

        // Check if player is outside boundary
        if (isOutsideBoundary(location, boundary)) {
            // Set cooldown
            playerCooldowns[player.uniqueId] = now

            // Check if crossing is allowed
            if (!ALLOW_CROSS) {
                player.sendMessage("§c⛵ You can't cross at this moment!")
                // Calculate safe location in current world using Scheduler
                calculateSafeLocationCurrentWorld(player, location, boundary) { safeLocation ->
                    player.teleportAsync(safeLocation)
                }
                return
            }

            teleportToNearestWorld(player, location, worldName)
        }
    }

    // Function to check if location is outside world boundary
    private fun isOutsideBoundary(location: Location, boundary: WorldBoundary): Boolean {
        val x = location.blockX
        val z = location.blockZ
        return x < boundary.minX || x > boundary.maxX || z < boundary.minZ || z > boundary.maxZ
    }

    // Function to teleport player to nearest connected world
    private fun teleportToNearestWorld(player: Player, currentLocation: Location, currentWorldName: String) {
        val currentPoint = Point(currentLocation.blockX, currentLocation.blockZ)
        var closestWorld: String? = null
        var closestDistance = Double.MAX_VALUE

        // Find nearest world (excluding current)
        worlds.forEach { (worldName, boundary) ->
            if (worldName != currentWorldName) {
                val distance = calculateDistanceToWorld(currentPoint, boundary)
                if (distance < closestDistance) {
                    closestDistance = distance
                    closestWorld = worldName
                }
            }
        }

        // Teleport to the closest world
        closestWorld?.let { worldName ->
            calculateSafeLocation(currentPoint, worldName, worlds[worldName]!!) { targetLocation ->
                if (targetLocation != null) {
                    player.teleportAsync(targetLocation)
                    player.sendMessage("§a⛵ Crossing into ${getWorldDisplayName(worldName)}")
                } else {
                    // World not found
                    player.sendMessage("§cWorld '${getWorldDisplayName(worldName)}' not found!")
                    // Teleport to safe location in current world as fallback
                    calculateSafeLocationCurrentWorld(player, currentLocation, worlds[currentWorldName]!!) { safeLocation ->
                        player.teleportAsync(safeLocation)
                    }
                }
            }
        } ?: run {
            // No closest world found - teleport to safe location in current world
            val boundary = worlds[currentWorldName] ?: return
            calculateSafeLocationCurrentWorld(player, currentLocation, boundary) { safeLocation ->
                player.teleportAsync(safeLocation)
            }
        }
    }

    // Function to calculate distance from point to nearest edge of world boundary
    private fun calculateDistanceToWorld(point: Point, boundary: WorldBoundary): Double {
        val nearestX = point.x.coerceIn(boundary.minX, boundary.maxX)
        val nearestZ = point.z.coerceIn(boundary.minZ, boundary.maxZ)

        val dx = point.x - nearestX
        val dz = point.z - nearestZ

        return kotlin.math.sqrt((dx * dx + dz * dz).toDouble())
    }

    // Function to calculate safe teleport location inside target world using Scheduler
    private fun calculateSafeLocation(fromPoint: Point, targetWorldName: String, targetBoundary: WorldBoundary, callback: (Location?) -> Unit) {
        val targetWorld = Bukkit.getWorld(targetWorldName)
        if (targetWorld == null) {
            callback(null)
            return
        }

        // Find nearest point inside target boundary
        val safeX = fromPoint.x.coerceIn(targetBoundary.minX + 5, targetBoundary.maxX - 5)
        val safeZ = fromPoint.z.coerceIn(targetBoundary.minZ + 5, targetBoundary.maxZ - 5)

        val tempLocation = Location(targetWorld, safeX.toDouble(), 64.0, safeZ.toDouble())

        // Use Scheduler to get safe Y coordinate in the target world region
        Scheduler.task(
            type = Scheduler.SchedulerType.REGION,
            location = tempLocation,
            task = {
                try {
                    val safeY = findSafeYCoordinate(targetWorld, safeX, safeZ)
                    val finalLocation = Location(targetWorld, safeX.toDouble(), safeY.toDouble(), safeZ.toDouble())
                    callback(finalLocation)
                } catch (e: Exception) {
                    NationsEvent.INSTANCE.logger.warning("Error finding safe location at $safeX, $safeZ in ${targetWorld.name}: ${e.message}")
                    // Fallback to spawn height
                    val fallbackLocation = Location(targetWorld, safeX.toDouble(), targetWorld.spawnLocation.blockY.toDouble(), safeZ.toDouble())
                    callback(fallbackLocation)
                }
            }
        )
    }

    // Function to calculate safe location in current world using Scheduler
    private fun calculateSafeLocationCurrentWorld(player: Player, currentLocation: Location, boundary: WorldBoundary, callback: (Location) -> Unit) {
        val world = currentLocation.world!!

        // Find nearest point inside current world boundary
        val safeX = currentLocation.blockX.coerceIn(boundary.minX + 5, boundary.maxX - 5)
        val safeZ = currentLocation.blockZ.coerceIn(boundary.minZ + 5, boundary.maxZ - 5)

        val tempLocation = Location(world, safeX.toDouble(), 64.0, safeZ.toDouble())

        // Use Scheduler to get safe Y coordinate in the current world region
        Scheduler.task(
            type = Scheduler.SchedulerType.REGION,
            location = tempLocation,
            task = {
                try {
                    val safeY = findSafeYCoordinate(world, safeX, safeZ)
                    val finalLocation = Location(world, safeX.toDouble(), safeY.toDouble(), safeZ.toDouble())
                    callback(finalLocation)
                } catch (e: Exception) {
                    NationsEvent.INSTANCE.logger.warning("Error finding safe location at $safeX, $safeZ in ${world.name}: ${e.message}")
                    // Fallback to current location Y
                    val fallbackLocation = Location(world, safeX.toDouble(), currentLocation.y, safeZ.toDouble())
                    callback(fallbackLocation)
                }
            }
        )
    }

    // Function to find safe Y coordinate (highest solid block + 1)
    private fun findSafeYCoordinate(world: org.bukkit.World, x: Int, z: Int): Int {
        // Start from world height and go down
        for (y in world.maxHeight - 1 downTo world.minHeight) {
            val block = world.getBlockAt(x, y, z)
            if (block.type.isSolid && !world.getBlockAt(x, y + 1, z).type.isSolid) {
                return y + 1
            }
        }

        // Fallback to world spawn height
        return world.spawnLocation.blockY
    }

    // Function to get display name for world
    private fun getWorldDisplayName(worldName: String): String {
        return when {
            worldName.contains("plains") -> "Plains"
            worldName.contains("desert") -> "Desert"
            worldName.contains("snow") -> "Snow"
            else -> worldName.replace("world_ns7_", "").replace("_", " ").replaceFirstChar { it.uppercase() }
        }
    }

    // Function to clean up old cooldown entries (call this periodically if needed)
    fun cleanupCooldowns() {
        val now = System.currentTimeMillis()
        playerCooldowns.entries.removeIf { (_, lastTeleport) ->
            now - lastTeleport > cooldownTime
        }
    }
}
