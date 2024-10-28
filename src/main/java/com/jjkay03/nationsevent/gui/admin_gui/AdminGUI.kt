package com.jjkay03.nationsevent.gui.admin_gui

import org.bukkit.Material
import org.ipvp.canvas.mask.BinaryMask
import org.ipvp.canvas.type.ChestMenu

class AdminGUI {

    // Creating adminGUI as an instance variable
    val adminGUI: ChestMenu = ChestMenu.builder(3).title("\uD83D\uDD25 ADMIN GUI").redraw(true).build()

    // Function to update GUI items
    fun updateGUI() {
        // Filler item - for future settings
        val fillerItemsFutureSettings = BinaryMask.builder(adminGUI.dimensions)
            .item(AdminGUI_Items.fillerItem(Material.GRAY_STAINED_GLASS_PANE))
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

        // GUI Items
        adminGUI.getSlot(9).item = AdminGUI_Items.announceSessionItem()
        adminGUI.getSlot(18).item = AdminGUI_Items.sessionTimeItem()
    }
}
