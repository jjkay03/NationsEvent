package com.jjkay03.nationsevent.worlds

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.utils.Config
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerMoveEvent

class WorldLobby : Listener {

    private var lobbyWorld: String? = null

    // Lobby spawn point
    private val lobbySpawn = Location(null, 0.0, 1.0,0.0)

    // Lobby platform boundaries
    private val platformMin = Location(null, -48.0, -2.0,-49.0)
    private val platformMax = Location(null, 50.0, 100.0, 49.0)

    // INITIALIZATION
    init {
        load(Config.WORLDS_LOBBY_ENABLE)
    }

    // LOAD (If enabled in config)
    fun load(enabled: Boolean) {
        if (!enabled) return
        lobbyWorld = Config.WORLDS_LOBBY_WORLD
        Bukkit.getPluginManager().registerEvents(this, NationsEvent.INSTANCE)
        NationsEvent.INSTANCE.logger.info("- Loading world manager: ${this::class.simpleName}")
    }

    // Prevent block breaking (unless admin)
    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        if (!isInLobby(event.block.world.name)) return
        if (event.player.hasPermission(Saves.PERM_ADMIN)) return
        event.isCancelled = true
    }

    // Prevent block placing (unless admin)
    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (!isInLobby(event.block.world.name)) return
        if (event.player.hasPermission(Saves.PERM_ADMIN)) return
        event.isCancelled = true
    }

    // Prevent damage in lobby
    @EventHandler
    fun onPlayerDamage(event: EntityDamageEvent) {
        if (event.entity !is Player) return
        if (!isInLobby(event.entity.world.name)) return
        event.isCancelled = true
    }

    // Prevent hunger loss in lobby
    @EventHandler
    fun onFoodLevelChange(event: FoodLevelChangeEvent) {
        if (!isInLobby(event.entity.world.name)) return
        event.isCancelled = true
    }

    // Handle player movement
    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player
        val location = event.to

        // End if player not in lobby
        if (!isInLobby(location.world?.name)) return

        // Teleport if fell below y -50
        if (location.y < -50) {
            player.teleportAsync(Location(location.world, lobbySpawn.x, lobbySpawn.y, lobbySpawn.z))
            return
        }

        // End if player is staff
        if (player.hasPermission(Saves.PERM_STAFF)) return

        // Check if trying to leave platform
        if (location.x < platformMin.x || location.x > platformMax.x ||
            location.y < platformMin.y || location.y > platformMax.y ||
            location.z < platformMin.z || location.z > platformMax.z) {
            event.isCancelled = true
        }
    }

    // Helper function to check if player is in lobby world
    private fun isInLobby(worldName: String?): Boolean {
        return worldName == lobbyWorld
    }
}
