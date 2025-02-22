package com.jjkay03.nationsevent.specific.ne1

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.scheduler.BukkitRunnable

class NE1_PlayerTracker : Listener, CommandExecutor {

    private val useTrackerPerm = "nationsevent.ne1.playertracker"
    private val ignoredTrackPlayersPerms = listOf(Saves.PERM_STAFF, Saves.PERM_SPECTATOR)
    private val trackerMaterial = Material.COMPASS
    private val trackerCMD = 100

    // Listener
    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (!event.action.toString().contains("RIGHT_CLICK")) return // End if not right-click

        // Get the item based on the interaction hand
        val item = when (event.hand) {
            EquipmentSlot.HAND -> event.player.inventory.itemInMainHand
            EquipmentSlot.OFF_HAND -> event.player.inventory.itemInOffHand
            else -> return
        }

        if (!isScannerItem(item)) return // End if item not scanner

        // End if no perm
        if (!event.player.hasPermission(useTrackerPerm)) {
            event.player.sendMessage("§cYou don't have permission to use the player tracker (PLEASE RETURN IT AN ADMIN)!")
            return
        }

        // Track the closest player
        track(event.player, item)
    }

    // Command
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        // Check if the sender is a player
        if (sender !is Player) { sender.sendMessage("§cOnly players can use this command."); return true }

        // Give the player the tracker item
        sender.inventory.addItem(trackerItem())
        sender.sendMessage("§aYou have been given a Player Tracker")
        return true
    }

    // Function to create scanner item
    private fun trackerItem(): ItemStack {
        val item = ItemStack(trackerMaterial)
        val meta: ItemMeta = item.itemMeta
        meta.setCustomModelData(trackerCMD)
        meta.displayName(Component.text("§4Player Tracker"))
        meta.lore(listOf(Component.text("§7Right-click to track closest player")))
        meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true)
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        item.itemMeta = meta
        return item
    }

    // Function to check if an ItemStack is the scanner item
    private fun isScannerItem(item: ItemStack?): Boolean {
        if (item == null || item.type != trackerMaterial) return false
        val meta: ItemMeta = item.itemMeta ?: return false
        return meta.hasCustomModelData() && meta.customModelData == trackerCMD
    }

    // Function to track the closest player
    private fun track(usingPlayer: Player, item: ItemStack) {
        val trackingPlayer = selectClosestPlayer(usingPlayer)
        if (trackingPlayer != null) {
            // Display a message in the action bar
            usingPlayer.sendActionBar(Component.text("§cPointing ➡ ${trackingPlayer.name}"))

            // Point compass to trackingPlayer
            usingPlayer.compassTarget = trackingPlayer.location
        } else {
            usingPlayer.sendActionBar(Component.text("§7No player to track"))
        }
    }

    // Function to select the closest player
    private fun selectClosestPlayer(usingPlayer: Player): Player? {
        // Get all online players
        val onlinePlayers = Bukkit.getOnlinePlayers().toList()

        // Filter out the usingPlayer, players with ignored permissions, and players in different worlds
        val eligiblePlayers = onlinePlayers.filter {
            it != usingPlayer &&
                    !ignoredTrackPlayersPerms.any { perm -> it.hasPermission(perm) } &&
                    it.world == usingPlayer.world
        }

        // Return null if no eligible players are found
        if (eligiblePlayers.isEmpty()) return null

        // Find and return the closest player
        return eligiblePlayers.minByOrNull { it.location.distanceSquared(usingPlayer.location) }
    }
}