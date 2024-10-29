package com.jjkay03.nationsevent.gui.admin_gui

import org.bukkit.Material
import org.bukkit.entity.Player
import org.ipvp.canvas.mask.BinaryMask
import org.ipvp.canvas.type.ChestMenu

class AdminGUI {

    // Creating adminGUI as an instance variable
    val adminGUI: ChestMenu = ChestMenu.builder(3).title("\uD83D\uDD25 ADMIN GUI").redraw(true).build()

    // Function to update GUI items
    fun updateGUI(player: Player) {
        // Filler item - for future settings
        val fillerItemsFutureSettings = BinaryMask.builder(adminGUI.dimensions)
            .item(AdminGUI_Items.fillerItem(Material.BLACK_STAINED_GLASS_PANE))
            .pattern("000000011")
            .pattern("000000011")
            .pattern("000000011")
            .build()
        fillerItemsFutureSettings.apply(adminGUI)

        // Filler item - for empty spots
        val fillerItemsNone = BinaryMask.builder(adminGUI.dimensions)
            .item(AdminGUI_Items.fillerItem(Material.GRAY_STAINED_GLASS_PANE))
            .pattern("000000000")
            .pattern("000000000")
            .pattern("011100100")
            .build()
        fillerItemsNone.apply(adminGUI)

        // GUI Items Status
        adminGUI.getSlot(0).item = AdminGUI_Items.statusItem("announce_session", player)
        adminGUI.getSlot(1).item = AdminGUI_Items.statusItem("voicechat_perms", player)
        adminGUI.getSlot(2).item = AdminGUI_Items.statusItem("frozen_players", player)
        adminGUI.getSlot(3).item = AdminGUI_Items.statusItem("pvp", player)
        adminGUI.getSlot(4).item = AdminGUI_Items.statusItem("locked_votes", player)
        adminGUI.getSlot(5).item = AdminGUI_Items.statusItem("permanent_message", player)
        adminGUI.getSlot(6).item = AdminGUI_Items.statusItem("hide_staff", player)

        // GUI Items Buttons
        adminGUI.getSlot(9).item = AdminGUI_Items.announceSessionItem()
        adminGUI.getSlot(18).item = AdminGUI_Items.sessionTimeItem()
    }
}

