package com.jjkay03.nationsevent.settings

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.utils.Config
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDispenseArmorEvent
import org.bukkit.event.inventory.InventoryClickEvent

class DisableEquipPlayerHead: Listener {

    // INITIALIZATION
    init {
        load(Config.SETTINGS_DISABLE_EQUIP_PLAYER_HEAD)
    }

    // LOAD (If enabled in config)
    fun load(enabled: Boolean) {
        if (!enabled) return
        Bukkit.getPluginManager().registerEvents(this, NationsEvent.INSTANCE)
        NationsEvent.INSTANCE.logger.info("- Loading setting: ${this::class.simpleName}")
    }

    // Prevent players from equipping a player head via inventory click
    @EventHandler
    fun onEquipPlayerHead(event: InventoryClickEvent) {
        val player = event.whoClicked

        // End if player has bypass perm (staff)
        if (player.hasPermission(Saves.PERM_STAFF)) return

        // Direct place into helmet slot
        if (event.rawSlot == 5 && event.cursor.type == Material.PLAYER_HEAD) event.isCancelled = true

        // Shift-click a player head from inventory
        if (event.isShiftClick && event.currentItem?.type == Material.PLAYER_HEAD) event.isCancelled = true
    }

    // Prevent players from equipping a player head via dispenser
    @EventHandler
    fun onDispenserEquipPlayerHead(event: BlockDispenseArmorEvent) {
        if (event.item.type == Material.PLAYER_HEAD) event.isCancelled = true
    }
}