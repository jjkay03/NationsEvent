package com.jjkay03.nationsevent.gui.admin_gui

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object AdminGUI_Items {

    // ITEM: filler item
    fun fillerItem(itemMaterial: Material): ItemStack {
        val item = ItemStack(itemMaterial)
        val itemMeta = item.itemMeta
        itemMeta?.apply {
            setDisplayName("§r")
            item.itemMeta = this
        }
        return item
    }

    // ITEM: announce session
    fun announceSessionItem(): ItemStack {
        val item = ItemStack(Material.WRITABLE_BOOK)
        val itemMeta = item.itemMeta
        itemMeta?.apply {
            setDisplayName("§eAnnounce Session")
            item.itemMeta = this
        }
        return item
    }

    // ITEM: session time
    fun sessionTimeItem(): ItemStack {
        val item = ItemStack(Material.CLOCK)
        val itemMeta = item.itemMeta
        itemMeta?.apply {
            setDisplayName("§7Session Time")
            item.itemMeta = this
        }
        return item
    }



}