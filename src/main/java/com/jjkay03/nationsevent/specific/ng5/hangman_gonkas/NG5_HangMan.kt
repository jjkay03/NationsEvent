package com.jjkay03.nationsevent.specific.ng5.hangman_gonkas

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.damage.DamageSource
import org.bukkit.damage.DamageType
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
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
import org.bukkit.scheduler.BukkitTask

class NG5_HangMan : Listener {

    companion object {

        var HANGED: ArrayList<NG5_Hanged> = ArrayList()
        var TASKS: HashMap<Player, BukkitTask> = HashMap()

        // Registers a new 'Hanged' object
        fun hang(player: Player, leashed: Player, stand: Zombie): NG5_Hanged {
            val hanged = NG5_Hanged(player, leashed, stand)

            HANGED.add(hanged)
            registerTask(player)

            return hanged
        }

        // Removes and DELETES the 'Hanged' object associated with this player
        fun unHang(player: Player) {
            HANGED.forEach { if (it.getOwner() == player || it.getLeashed() == player) { HANGED.remove(it); it.delete(); return; } }
            cancelTask(player)
        }

        // Returns the Hanged object associated with the player. null if none found.
        fun getHanged(player: Player): NG5_Hanged? {
            HANGED.forEach { if (it.getOwner() == player || it.getLeashed() == player) { return it } }
            return null
        }

        // Returns whether or not this players has a leash attached to them by another player
        fun isHanged(player: Player): Boolean {
            return getHanged(player) != null
        }

        // Registers a BukkitTask responsible for handling a 'Hanged' object's lifetime associated with 'player'
        fun registerTask(player: Player) {
            TASKS[player] = Bukkit.getScheduler().runTaskTimer(NationsEvent.INSTANCE, Runnable { getHanged(player)?.tick() }, 0, 1)
        }

        // Cancels the BukkitTask responsible for handling a 'Hanged' object's lifetime associated with 'player'
        fun cancelTask(player: Player) {
            TASKS[player]!!.cancel()
            TASKS.remove(player)
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

        // Unhangs the player
        unHang(event.player)
    }

    @EventHandler
    fun onPlayerLogOut(event: PlayerQuitEvent) {

        if (!isHanged(event.player)) return
        event.player.damage(1000.toDouble())
    }

    // Returns entity if it is a player or if 'player' is the owner of 'hanged' and 'entity' its leashed
    private fun getLeashTarget(entity: Entity, player: Player, hanged: NG5_Hanged?): Player? {
        if (entity.type == EntityType.PLAYER) { return entity as Player }
        if (hanged != null && hanged.getOwner() == player) { return hanged.getLeashed() }
        return null
    }
}