package com.jjkay03.nationsevent.worlds

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.commands.others.DisabledCommands
import com.jjkay03.nationsevent.utils.Config
import com.jjkay03.nationsevent.utils.Scheduler
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Entity
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

class WorldsBridge : CommandExecutor, TabCompleter, Listener {

    companion object {
        var ALLOW_CROSS = false
    }

    // Command
    private val commandName = "worldsbridge"

    // Player cooldown tracking
    private val cooldownTime = 60000L // 60 second cooldown
    private val playerCooldowns = mutableMapOf<UUID, Long>()

    // WORLD CONFIGURATION
    private val worlds = mapOf(
        "world_ns8_hot" to WorldBoundary(
            pos1 = Point(-2048, -2048),
            pos2 = Point(0, 2048)
        ),
        "world_ns8_cold" to WorldBoundary(
            pos1 = Point(0, -2048),
            pos2 = Point(2048, 2048)
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
        // Enabled
        if (enabled) {
            NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
            NationsEvent.INSTANCE.getCommand(commandName)?.tabCompleter = this
            Bukkit.getPluginManager().registerEvents(this, NationsEvent.INSTANCE)
            NationsEvent.INSTANCE.logger.info("- Loading world manager: ${this::class.simpleName}")
        }
        // Disabled
        else {
            NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(DisabledCommands("World Bridge is disabled!"))
        }
    }

    // COMMAND
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        val invalidArguments = "§cInvalid argument, usage: /$label on/off"
        if (args.isEmpty()) { sender.sendMessage(invalidArguments); return true }
        when (args[0].lowercase()) {
            "on" -> { ALLOW_CROSS = true; sender.sendMessage("§7⛵ Worlds Bridge has been §aENABLED") }
            "off" -> { ALLOW_CROSS = false; sender.sendMessage("§7⛵ Worlds Bridge has been §cDISABLED") }
            else -> sender.sendMessage(invalidArguments)
        }
        return true
    }

    // TAB COMPLETER
    override fun onTabComplete(sender: CommandSender, cmd: Command, alias: String, args: Array<out String>): List<String>? {
        if (args.size == 1) {
            val completions = mutableListOf("on", "off")
            return completions.filter { it.startsWith(args[0], ignoreCase = true) }
        }
        return null
    }

    // PLAYER MOVE EVENT
    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player
        val from = event.from
        val location = event.to
        val worldName = location.world?.name ?: return

        // Ignore if only head movement (pitch/yaw)
        if (from.x == location.x && from.y == location.y && from.z == location.z) return

        // End if player is not in a monitored world
        val boundary = worlds[worldName] ?: return

        // End if player is not outside boundary
        if (!isOutsideBoundary(location, boundary)) return

        // Check if crossing is allowed
        if (!ALLOW_CROSS) {
            // Allow movement if player is moving towards the boundary (back inside)
            if (isMovingTowardsBoundary(from, location, boundary)) return

            // Notify player
            player.sendMessage("§c⛵ You can't cross at this moment!")

            // Dismount player from vehicle if they're riding one
            val vehicle = player.vehicle
            vehicle?.removePassenger(player)

            // Cancel event
            event.isCancelled = true
            return
        }

        // End if player is on cooldown
        if (handleCooldown(player, worldName, location, event)) return

        // Set cooldown
        playerCooldowns[player.uniqueId] = System.currentTimeMillis()

        // Teleport player
        teleportToNearestWorld(
            player,
            location,
            worldName,
            Config.WORLDS_BRIDGE_CARRY_VEHICLE
        )
    }

    // Function to check if location is outside world boundary
    private fun isOutsideBoundary(location: Location, boundary: WorldBoundary): Boolean {
        val x = location.blockX
        val z = location.blockZ
        return x < boundary.minX || x > boundary.maxX || z < boundary.minZ || z > boundary.maxZ
    }

    // Function to check if player is moving towards the boundary (back inside)
    private fun isMovingTowardsBoundary(from: Location, to: Location, boundary: WorldBoundary): Boolean {
        fun distanceOutside(x: Double, z: Double): Double {
            val dx = when {
                x < boundary.minX -> boundary.minX - x
                x > boundary.maxX -> x - boundary.maxX
                else -> 0.0
            }
            val dz = when {
                z < boundary.minZ -> boundary.minZ - z
                z > boundary.maxZ -> z - boundary.maxZ
                else -> 0.0
            }
            return kotlin.math.sqrt(dx * dx + dz * dz)
        }

        val fromDistance = distanceOutside(from.x, from.z)
        val toDistance = distanceOutside(to.x, to.z)
        return toDistance < fromDistance
    }

    //  Function to teleport player to nearest connected world
    private fun teleportToNearestWorld(player: Player, currentLocation: Location, currentWorldName: String, carryVehicle: Boolean = false) {
        // Get the closest world
        val currentPoint = Point(currentLocation.blockX, currentLocation.blockZ)
        val closestWorld = findClosestWorld(currentPoint, currentWorldName)
        if (closestWorld == null) return

        // Store and dismount vehicle if player is riding one
        val vehicle = if (carryVehicle) player.vehicle else null
        vehicle?.removePassenger(player)

        // Teleport player to world
        teleportToWorld(player, currentLocation, closestWorld)

        // Teleport vehicle and remount if carrying vehicle
        if (vehicle != null) {
            teleportToWorld(vehicle, currentLocation, closestWorld, sendMessage = false)

            // Schedule remount after short delay
            Scheduler.taskDelayed(
                type = Scheduler.SchedulerType.ENTITY,
                delayTicks = 5L,
                task = { vehicle.addPassenger(player) },
                entity = player
            )
        }
    }

    // Function to find the closest world
    private fun findClosestWorld(currentPoint: Point, currentWorldName: String): String? {
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
        return closestWorld
    }

    // Function to teleport to a specific world (works for any entity)
    private fun teleportToWorld(entity: Entity, currentLocation: Location, targetWorldName: String, sendMessage: Boolean = true) {
        val currentPoint = Point(currentLocation.blockX, currentLocation.blockZ)
        calculateSafeLocation(currentPoint, targetWorldName, worlds[targetWorldName]!!) { targetLocation ->
            // Teleport
            if (targetLocation != null) {
                targetLocation.yaw = currentLocation.yaw
                targetLocation.pitch = currentLocation.pitch
                entity.teleportAsync(targetLocation)

                // Send message if entity is a player
                if (sendMessage && entity is Player) entity.sendMessage("§a⛵ Crossing into ${getWorldDisplayName(targetWorldName)}")
            }
            // World not found
            else {
                if (entity is Player) entity.sendMessage("§cWorld '${getWorldDisplayName(targetWorldName)}' not found!")
            }
        }
    }

    // Helper Function to calculate distance from point to nearest edge of world boundary
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
        if (targetWorld == null) { callback(null); return }

        // Find nearest point inside target boundary
        val safeX = fromPoint.x.coerceIn(targetBoundary.minX + 5, targetBoundary.maxX - 5)
        val safeZ = fromPoint.z.coerceIn(targetBoundary.minZ + 5, targetBoundary.maxZ - 5)
        val tempLocation = Location(targetWorld, safeX.toDouble(), 64.0, safeZ.toDouble())

        // Use Scheduler to get safe Y coordinate in the target world region
        Scheduler.task(
            type = Scheduler.SchedulerType.REGION,
            location = tempLocation,
            task = {
                val finalLocation = findSafeY(targetWorld, safeX, safeZ)
                callback(finalLocation)
            }
        )
    }

    // Helper function to find safe Y coordinate (highest solid/water block + 1) with fallback
    private fun findSafeY(world: World, x: Int, z: Int): Location {
        val safeY =
            try {
                // Start from world height and go down
                var foundY: Int? = null
                for (y in world.maxHeight - 1 downTo world.minHeight) {
                    val block = world.getBlockAt(x, y, z)
                    val blockAbove = world.getBlockAt(x, y + 1, z)
                    val isSafeGround = block.type.isSolid || block.type == Material.WATER            // Safe ground: solid or water
                    val hasSafeSpace = !blockAbove.type.isSolid || blockAbove.type == Material.WATER // Safe space above: air or water
                    if (isSafeGround && hasSafeSpace) { foundY = y + 1; break }
                }
                // Fallback to world spawn height if no safe Y found
                foundY ?: world.spawnLocation.blockY
            }

            // Fallback to spawn height
            catch (e: Exception) {
                NationsEvent.INSTANCE.logger.warning("WorldBridge - Error finding safe location at $x, $z in ${world.name}: ${e.message}")
                world.spawnLocation.blockY
            }

        return Location(world, x.toDouble(), safeY.toDouble(), z.toDouble())
    }

    // Helper function to get display name for world
    private fun getWorldDisplayName(worldName: String): String {
        return when {
            worldName.contains("hot") -> "Hot"
            worldName.contains("cold") -> "Cold"
            else -> worldName.replace("world_ns8_", "").replace("_", " ").replaceFirstChar { it.uppercase() }
        }
    }

    // Helper function to handle cooldown logic
    private fun handleCooldown(player: Player, worldName: String, location: Location, event: PlayerMoveEvent): Boolean {
        val now = System.currentTimeMillis()
        val lastTeleport = playerCooldowns[player.uniqueId] ?: 0

        // Staff get 3s cooldown, others get full cooldown
        val playerCooldown = if (player.hasPermission(Saves.PERM_STAFF)) 3000 else cooldownTime
        val isOnCooldown = now - lastTeleport < playerCooldown

        if (!isOnCooldown) return false

        // Check if player is in a monitored world and outside boundary
        val boundary = worlds[worldName] ?: return false
        if (isOutsideBoundary(location, boundary)) {
            val cooldownLeft = ((playerCooldown - (now - lastTeleport)) / 1000.0)
            player.sendMessage("§c⛵ Wait ${cooldownLeft.toInt()}s before crossing again!")
            event.isCancelled = true // Cancel event
        }
        return true
    }

}
