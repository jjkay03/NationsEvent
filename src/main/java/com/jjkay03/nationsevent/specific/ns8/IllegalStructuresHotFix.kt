package com.jjkay03.nationsevent.specific.ns8

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.utils.Scheduler
import org.bukkit.Material
import org.bukkit.block.CreatureSpawner
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.SpawnerSpawnEvent
import org.bukkit.event.entity.TrialSpawnerSpawnEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.ItemStack

class IllegalStructuresHotFix : Listener {

    // INITIALIZATION
    init {
        NationsEvent.INSTANCE.server.pluginManager.registerEvents(this, NationsEvent.INSTANCE)
    }

    // List of banned items
    private val bannedItems = listOf(
        Material.TRIAL_KEY,
        Material.OMINOUS_TRIAL_KEY,
        Material.BREEZE_ROD,
        Material.WIND_CHARGE,
        Material.HEAVY_CORE,
        Material.MACE,
        Material.COBWEB,
        Material.OMINOUS_BOTTLE,
        Material.TRIDENT,
        Material.TIPPED_ARROW,
        Material.POTION,
        Material.SPLASH_POTION,
        Material.LINGERING_POTION,
        Material.ENCHANTED_GOLDEN_APPLE,
        Material.FIRE_CHARGE
    )

    // Delete trial spawners when they try to spawn
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onSpawnerSpawn(event: SpawnerSpawnEvent) {
        val spawner = event.spawner ?: return
        val block = spawner.block
        event.isCancelled = true
        block.type = Material.AIR
    }

    // Delete trial spawners when they try to spawn
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onTrialSpawnerSpawn(event: TrialSpawnerSpawnEvent) {
        val block = event.trialSpawner.block
        event.isCancelled = true
        block.type = Material.AIR
    }

    // Prevent spawners from spawning and remove the spawner block
    @EventHandler(priority = EventPriority.HIGH)
    fun onCreatureSpawn(event: CreatureSpawnEvent) {
        if (event.spawnReason == CreatureSpawnEvent.SpawnReason.SPAWNER ||
            event.spawnReason == CreatureSpawnEvent.SpawnReason.TRIAL_SPAWNER) {
            event.isCancelled = true
        }
    }

    // Remove vaults when players interact with them
    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        // Check held items first and cancel event if banned item detected
        val mainHandBanned = checkItem(event.player.inventory.itemInMainHand, event.player)
        val offHandBanned = checkItem(event.player.inventory.itemInOffHand, event.player)

        if (mainHandBanned || offHandBanned) {
            event.isCancelled = true
            return
        }

        // Check vault blocks
        val block = event.clickedBlock ?: return
        if (block.type == Material.VAULT) {
            block.type = Material.AIR
            event.isCancelled = true
        }
    }

    // Prevent cobwebs from dropping when mined
    @EventHandler
    fun onBlockDrop(event: BlockDropItemEvent) {
        if (event.blockState.type == Material.COBWEB) {
            event.items.clear()
        }
    }

    // Check item when held
    @EventHandler
    fun onItemHeld(event: PlayerItemHeldEvent) {
        checkItem(event.player.inventory.itemInMainHand, event.player)
    }

    // Check when swapping hand items
    @EventHandler
    fun onSwapHandItems(event: PlayerSwapHandItemsEvent) {
        checkItem(event.mainHandItem, event.player)
        checkItem(event.offHandItem, event.player)
    }

    // Check when shooting arrows (prevents tipped arrows from being shot)
    @EventHandler
    fun onShootBow(event: EntityShootBowEvent) {
        val player = event.entity as? Player ?: return

        // Check if player has bypass permissions
        if (player.hasPermission(Saves.PERM_ADMIN) || player.hasPermission(Saves.PERM_PROD)) return

        val arrow = event.consumable
        if (arrow != null && arrow.type == Material.TIPPED_ARROW) {
            event.isCancelled = true
            arrow.amount = 0
            player.sendMessage("§8✖ Deleted illegal item TIPPED_ARROW")
        }
    }

    // Helper function to check and delete banned items
    // Returns true if a banned item was found and deleted
    private fun checkItem(item: ItemStack?, player: Player): Boolean {
        if (item == null || item.type == Material.AIR) return false

        // Check if player has bypass permissions
        if (player.hasPermission(Saves.PERM_ADMIN) || player.hasPermission(Saves.PERM_PROD)) return false

        if (item.type in bannedItems) {
            val itemName = item.type.name
            item.amount = 0
            player.sendMessage("§8✖ Deleted illegal item $itemName")
            return true
        }

        return false
    }

}