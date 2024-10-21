package com.jjkay03.nationsevent.specific.ng5.hangman

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.damage.DamageSource
import org.bukkit.damage.DamageType
import org.bukkit.entity.EntityType
import org.bukkit.entity.Pig
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitTask

class NG5_HangMan : Listener {

    companion object {

        var HANGED: ArrayList<NG5_Hanged> = ArrayList()
        var TASKS: HashMap<Player, BukkitTask> = HashMap()

        // Registers a new 'Hanged' object
        fun hang(player: Player, leashed: Player, stand: Pig) {
            HANGED.add(NG5_Hanged(player, leashed, stand))
        }

        // Removes and DELETES the 'Hanged' object associated with this player
        fun unHang(player: Player) {
            HANGED.forEach { if (it.getOwner() == player || it.getLeashed() == player) { HANGED.remove(it); it.delete(); return; } }
        }

        // Returns the Hanged object associated with the player. null if none found.
        fun getHanged(player: Player): NG5_Hanged? {
            HANGED.forEach { if (it.getOwner() == player) { return it } }
            return null
        }

        // Returns whether or not this players has a leash attached to them by another player
        fun isHanged(player: Player): Boolean {
            HANGED.forEach { if (it.getLeashed() == player) {return it.getLeashed() == player} }
            return false
        }

        // Returns whether or not this player has a leash attached to another player
        fun isLeashing(player: Player): Boolean {
            HANGED.forEach { if (it.getOwner() == player) {return it.getOwner() == player} }
            return false
        }

        // Registers a BukkitTask responsible for handling a 'Hanged' object's lifetime associated with 'player'
        fun registerTask(player: Player, task: BukkitTask) {
            TASKS.put(player, task)
        }

        // Cancels the BukkitTask responsible for handling a 'Hanged' object's lifetime associated with 'player'
        fun cancelTask(player: Player) {
            TASKS[player]?.cancel()
            TASKS.remove(player)
        }

        // Custom damage source used to damage hanged players
        fun getHangedDamageSource(): DamageSource {
            return DamageSource.builder(DamageType.OUT_OF_WORLD).build()
        }

        // Checker for custom damage source
        fun isHangedDamageSource(damageSource: DamageSource): Boolean {
            return damageSource.damageType == DamageType.OUT_OF_WORLD
        }
    }

    @EventHandler
    fun onPlayerLeashPlayer(event: PlayerInteractEntityEvent) {

        // The Player that triggered the event
        val player: Player = event.player

        if (!player.hasPermission(NationsEvent.PERM_ADMIN)) return     // Must have op
        if (event.rightClicked !is Player) return                      // Right-clicked entity must be a player

        // The Player that was right-clicked
        val target: Player = event.rightClicked as Player

        // The item stack the player is holding as the event is called
        val itemStack: ItemStack = player.inventory.getItem(event.hand)

        // Must be holding a Lead as the event is called
        if (itemStack.type != Material.LEAD) return

        // Creates ghost entity that will serve as a pseudo-leashed Player (should change to a Player Model entity)
        val pig: Pig = player.world.spawnEntity(target.location, EntityType.PIG, CreatureSpawnEvent.SpawnReason.CUSTOM) as Pig

        pig.isAware = false       // Remove mob AI
        pig.isCollidable = false
        pig.isInvisible = true
        pig.isInvulnerable = true
        pig.isSilent = true

        pig.setAdult()
        pig.setLeashHolder(player)

        target.allowFlight = true
        itemStack.amount -= 1

        // If the target player is already Hanged, undo
        if (!isHanged(target)) { hang(player, target, pig) }
        else { unHang(target) }
    }

    @EventHandler
    fun onPlayerTieLeashOnFence(event: PlayerInteractEvent) {

        if (event.action != Action.RIGHT_CLICK_BLOCK) return                                           // Make sure action was a right click on a block
        if (event.hand?.let { event.player.inventory.getItem(it).type } != Material.LEAD) return       // Make sure the player is holding a leash

        // All fences in the game
        val fences: Array<Material> = Array(12) {
            Material.ACACIA_FENCE
            Material.BAMBOO_FENCE
            Material.BIRCH_FENCE
            Material.CHERRY_FENCE
            Material.CRIMSON_FENCE
            Material.DARK_OAK_FENCE
            Material.JUNGLE_FENCE
            Material.MANGROVE_FENCE
            Material.NETHER_BRICK_FENCE
            Material.OAK_FENCE
            Material.SPRUCE_FENCE
            Material.WARPED_FENCE
        }

        if (event.clickedBlock?.type !in fences) return     // Make sure the block clicked was a fence
        if (!isLeashing(event.player)) return               // Make sure the player has a leash on another player

        // Ticks the hanged entity until 'getHanged' returns 'null'
        registerTask(event.player, Bukkit.getScheduler().runTaskTimer(NationsEvent.INSTANCE, { getHanged(event.player)?.tick() } as Runnable, 0, 20))
    }

    @EventHandler
    fun onPlayerHangedDeath(event: PlayerDeathEvent) {

        if (!isHanged(event.player)) return                        // Make sure the player is hanged
        if (!isHangedDamageSource(event.damageSource)) return      // Make sure the death reason is the hanged custom death reason

        event.deathMessage = event.player.name + " was hung."      // Alter death message to '<player> was hung.'

        // Cancels the ticking of the player's associated 'Hanged' object
        cancelTask(event.player)
    }
}