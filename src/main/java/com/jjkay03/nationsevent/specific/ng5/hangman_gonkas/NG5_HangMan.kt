package com.jjkay03.nationsevent.specific.ng5.hangman_gonkas

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import com.jjkay03.nationsevent.NationsEvent
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Zombie
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.scheduler.BukkitTask

class NG5_HangMan : Listener {

    companion object {

        var HANGED: ArrayList<NG5_Hanged> = ArrayList()
        private var TASK: BukkitTask? = null
        val GHOSTNAME: Component = Component.text("nationsevent_ng5hangman_roberto")

        // Function to registers a new 'Hanged' object
        fun hang(player: Player, leashed: Player, stand: Zombie): NG5_Hanged {
            val hanged = NG5_Hanged(player, leashed, stand)
            HANGED.add(hanged)
            updateTask()
            return hanged
        }

        // Function to removes and DELETES the 'Hanged' object associated with this player
        fun unHang(player: Player) {
            HANGED.forEach { if (it.getOwner() == player || it.getLeashed() == player) { HANGED.remove(it); it.delete(); return; } }
            updateTask()
        }

        // Function that returns the Hanged object associated with the player. null if none found.
        fun getHanged(player: Player): NG5_Hanged? {
            HANGED.forEach { if (it.getOwner() == player || it.getLeashed() == player) { return it } }
            return null
        }

        // Function that returns whether these players have a leash attached to them by another player
        fun isHanged(player: Player): Boolean { return getHanged(player) != null }

        // Function that returns whether the given entity can and is leashed
        private fun isLeashed(entity: Entity): Boolean {
            if (entity is LivingEntity) return entity.isLeashed
            return false
        }

        // Function that deletes Ghost Entities once the server starts.
        private fun deleteExpiredGhostEntites() {
            Bukkit.getWorlds().forEach { world ->
                var entityCount = 0
                world.loadedChunks.forEach { chunk -> chunk.entities.filter{it.name() == GHOSTNAME && !isLeashed(it)}.forEach { it.remove(); entityCount++; } }
                if (entityCount > 0) {Bukkit.getLogger().info("NG5 Hangman: removed " + entityCount + " expired Ghost entities in " + world.name + ".")}
            }
        }

        // Function to create task
        private fun createTask(): BukkitTask {
            return Bukkit.getScheduler().runTaskTimer(NationsEvent.INSTANCE, Runnable { HANGED.forEach { it.tick() } }, 0, 1)
        }

        // Function to update task
        private fun updateTask() {
            if (TASK == null) {TASK = createTask()}
            else if (HANGED.size == 0) { TASK!!.cancel(); TASK = null }
        }
    }

    @EventHandler
    fun onPlayerLeashPlayer(event: PlayerInteractEntityEvent) {

        // The Player that triggered the event
        val player: Player = event.player

        if (event.hand == EquipmentSlot.OFF_HAND) return               // Must use main hand
        if (!player.hasPermission(NationsEvent.PERM_ADMIN)) return     // Must have op

        // The Player or Ghost Entity that was right-clicked
        val target: Player = getLeashTarget(event.rightClicked, player, getHanged(player)) ?: return    // Returns if invalid leash target

        // If the target player is already Hanged, undo
        if (isHanged(target)) {
            target.allowFlight = false
            unHang(target)
            return
        }

        // The item stack the player is holding as the event is called
        val itemStack: ItemStack = player.inventory.getItem(event.hand)

        if (itemStack.type != Material.LEAD) return      // Must be holding a Lead to hang someone

        deleteExpiredGhostEntites()

        // Creates ghost entity that will serve as a pseudo-leashed Player
        val zombie: Zombie = player.world.spawnEntity(target.location, EntityType.ZOMBIE, CreatureSpawnEvent.SpawnReason.CUSTOM) {
            it as Zombie

            it.equipment.setItemInMainHand(null)
            it.equipment.setItemInOffHand(null)
            it.equipment.helmet = null
            it.equipment.chestplate = null
            it.equipment.leggings = null
            it.equipment.boots = null

            it.canPickupItems = false
            it.isAware = false              // Remove mob AI
            it.isCollidable = false
            it.isInvisible = true
            it.isInvulnerable = true
            it.isSilent = true

            it.customName(GHOSTNAME)

            it.setAdult()
            it.setShouldBurnInDay(false)    // Prevent the Zombie from burning during the day
            it.setLeashHolder(player)

        } as Zombie

        target.allowFlight = true           // Prevent player from getting kicked while hanging
        itemStack.amount -= 1

        // Register a 'Hanged' object associated with 'player', 'target' and 'zombie'
        hang(player, target, zombie)
    }

    @EventHandler
    fun onPlayerHangedDeath(event: PlayerDeathEvent) {
        if (!isHanged(event.player)) return                       // Make sure the player is hanged
        event.deathMessage = event.player.name + " was hung"      // Alter death message to '<player> was hung.'
        unHang(event.player)                                      // Unhangs the player
    }

    @EventHandler
    fun onPlayerLogOut(event: PlayerQuitEvent) {
        if (!isHanged(event.player)) return
        event.player.damage(1000.toDouble())
    }

    @EventHandler
    fun onEntityRemoval(event: EntityRemoveFromWorldEvent) {
        if (event.entity.type != EntityType.LEASH_KNOT) return
        HANGED.forEach { if (it.getGhost().leashHolder == event.entity) {it.delete()} }
    }

    // Function that returns entity if it is a player or if 'player' is the owner of 'hanged' and 'entity' its leashed
    private fun getLeashTarget(entity: Entity, player: Player, hanged: NG5_Hanged?): Player? {
        if (entity.type == EntityType.PLAYER) { return entity as Player }
        if (hanged != null && hanged.getOwner() == player) { return hanged.getLeashed() }
        return null
    }
}