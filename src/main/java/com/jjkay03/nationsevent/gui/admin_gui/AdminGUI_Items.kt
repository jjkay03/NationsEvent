package com.jjkay03.nationsevent.gui.admin_gui

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.commands.HideStaffCommand
import com.jjkay03.nationsevent.commands.PermanentMessageCommand
import com.jjkay03.nationsevent.commands.SessionTimeCommand
import com.jjkay03.nationsevent.commands.voting.VoteCommand
import com.jjkay03.nationsevent.utils.FreezeAll
import com.jjkay03.nationsevent.utils.PVPToggle
import org.bukkit.Material
import org.bukkit.entity.Player
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
            setDisplayName("§6Announce Session")
            lore = listOf(
                "§r",
                "§7Click to prompt:",
                "§e/announcesession"
            )
            item.itemMeta = this
        }
        return item
    }

    // ITEM: session time
    fun sessionTimeItem(): ItemStack {
        val item = ItemStack(Material.CLOCK)
        val itemMeta = item.itemMeta
        itemMeta?.apply {
            setDisplayName("§fSession Time")
            lore = listOf(
                "§r",
                if (NationsEvent.SESSION_STARTED) "§a⌚ ${SessionTimeCommand.getTimeElapsed()}" else "§cSession did not start yet!",
                "§r",
                "§7Click to prompt:",
                "§e/announcesession"
            )
            item.itemMeta = this
        }
        return item
    }

    // ITEM: status item
    fun statusItem(entry: String, player: Player): ItemStack {
        // Items (Enabled/Disabled)
        val enabledItem = ItemStack(Material.LIME_STAINED_GLASS_PANE).apply { itemMeta = itemMeta?.apply { setDisplayName("§a✔ §lENABLED") } }
        val disabledItem = ItemStack(Material.RED_STAINED_GLASS_PANE).apply { itemMeta = itemMeta?.apply { setDisplayName("§c❌ §lDISABLED") } }

        // Entries
        return when (entry) {
            "announce_session" -> {
                if (NationsEvent.SESSION_STARTED) enabledItem
                else disabledItem
            }
            "voicechat_perms" -> {
                ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
            }
            "frozen_players" -> {
                if (FreezeAll.FREEZE_ALL_ENABLED) enabledItem
                else disabledItem
            }
            "pvp" -> {
                if (PVPToggle.PVP_ENABLED) enabledItem
                else disabledItem
            }
            "locked_votes" -> {
                if (VoteCommand.LOCKED_VOTES) enabledItem
                else disabledItem
            }
            "permanent_message" -> {
                if (PermanentMessageCommand.PERMANENT_MESSAGE != null) enabledItem
                else disabledItem
            }
            "hide_staff" -> {
                if (HideStaffCommand.HIDE_STAFF_PLAYERS.contains(player.uniqueId.toString())) enabledItem
                else disabledItem
            }
            else -> ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
        }
    }


}