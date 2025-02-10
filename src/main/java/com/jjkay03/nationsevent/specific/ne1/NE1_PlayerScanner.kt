package com.jjkay03.nationsevent.specific.ne1

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.scheduler.BukkitRunnable

class NE1_PlayerScanner : Listener, CommandExecutor {

    private val useScannerPerm = "nationsevent.ne1.playerscanner"
    private val ignoredScanPlayersPerms = listOf(Saves.PERM_STAFF, Saves.PERM_SPECTATOR)
    private val activeScans = mutableListOf<Player>() // Track active scans
    private val scannerMaterial = Material.ENDER_EYE
    private val scannerCMD = 100


    // Listener
    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        // End if no perm
        if (!event.player.hasPermission(useScannerPerm)) {
            event.player.sendMessage("§cYou don't have permission to use the player scanner (PLEASE RETURN IT AN ADMIN)!")
            return
        }

        if (!event.action.toString().contains("RIGHT_CLICK")) return // End if not right-click

        // Get the item based on the interaction hand
        val item = when (event.hand) {
            EquipmentSlot.HAND -> event.player.inventory.itemInMainHand
            EquipmentSlot.OFF_HAND -> event.player.inventory.itemInOffHand
            else -> return
        }

        if (!isScannerItem(item)) return // End if item not scanner
        event.isCancelled = true // Cancel to prevent Ender Eye from floating
        scan(event.player, 25) // Run scan
    }


    // Command
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        // Check if the sender is a player
        if (sender !is Player) { sender.sendMessage("§cOnly players can use this command."); return true }

        // Give the player the scanner item
        val scannerItem: ItemStack = scannerItem()
        sender.inventory.addItem(scannerItem)
        sender.sendMessage("§aYou have been given a Player Scanner")
        return true
    }


    // Function to create scanner item
    fun scannerItem(): ItemStack {
        val item = ItemStack(scannerMaterial)
        val meta: ItemMeta = item.itemMeta
        meta.setCustomModelData(scannerCMD)
        meta.displayName(Component.text("§2Player Scanner"))
        meta.lore(listOf(Component.text("§7Right-click to scan for nearby players")))
        item.itemMeta = meta
        return item
    }


    // Function to check if an ItemStack is the scanner item
    fun isScannerItem(item: ItemStack?): Boolean {
        if (item == null || item.type != scannerMaterial) return false
        val meta: ItemMeta = item.itemMeta ?: return false
        return meta.hasCustomModelData() && meta.customModelData == scannerCMD
    }


    // Function to perform nearby player scan ignoring staff players
    fun scan(usingPlayer: Player, blockRadius: Int) {
        if (usingPlayer in activeScans) return // Check if the player is already being scanned
        activeScans.add(usingPlayer) // Add the player to the active scans list

        // Perform the scan and get the result
        val nearbyPlayers = usingPlayer.world.getNearbyPlayers(
            usingPlayer.location, // Center location
            blockRadius.toDouble(), // X
            blockRadius.toDouble(), // Y
            blockRadius.toDouble(), // Z
        ).filter { it != usingPlayer } // Ignore usingPlayer
            .filter { player ->
                ignoredScanPlayersPerms.none { perm -> player.hasPermission(perm) }
            }

        startScanAnimation(usingPlayer, nearbyPlayers.size) // Start the scanning animation
    }


    // Function to display scan animation in action bar
    private fun startScanAnimation(usingPlayer: Player, playerCount: Int) {
        object : BukkitRunnable() {
            var step = 0
            override fun run() {
                when (step) {
                    0 -> usingPlayer.sendActionBar(Component.text("§2Scanning"))
                    1 -> usingPlayer.sendActionBar(Component.text("§2Scanning ."))
                    2 -> usingPlayer.sendActionBar(Component.text("§2Scanning . ."))
                    3 -> usingPlayer.sendActionBar(Component.text("§2Scanning . . ."))
                    else -> {
                        usingPlayer.sendActionBar(Component.text(if (playerCount > 0) "§aNEARBY PLAYERS: $playerCount" else "§cNO NEARBY PLAYERS"))
                        activeScans.remove(usingPlayer) // Remove the player from the active scans list
                        cancel()
                        return
                    }
                }
                step++
            }
        }.runTaskTimer(NationsEvent.INSTANCE, 0L, 20L) // Run every 20 ticks (1 second)
    }
}