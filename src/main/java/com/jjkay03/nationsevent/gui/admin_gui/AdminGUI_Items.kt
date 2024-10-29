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
                "§e/announcesession <number>",
                "§r"
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
            setDisplayName("§6Session Time")
            lore = listOf(
                "§r",
                if (NationsEvent.SESSION_STARTED) "§a⌚ ${SessionTimeCommand.getTimeElapsed()}"
                else "§cSession did not start yet!",
                "§r",
                "§7Click to run:",
                "§e/sessiontime",
                "§r"
            )
            item.itemMeta = this
        }
        return item
    }

    // ITEM: voicechat
    fun voicechatItem(): ItemStack {
        val item = ItemStack(Material.HOPPER)
        val itemMeta = item.itemMeta
        itemMeta?.apply {
            setDisplayName("§6Voicechat Permission")
            lore = listOf(
                "§r",
                "§7Click to run:",
                "§e/voicechatperms",
                "§r"
            )
            item.itemMeta = this
        }
        return item
    }

    // ITEM: freeze players
    fun freezePlayersItems(): ItemStack {
        val item = ItemStack(Material.ARMOR_STAND)
        val itemMeta = item.itemMeta
        itemMeta?.apply {
            setDisplayName("§6Freeze Players")
            lore = listOf(
                "§r",
                "§7Click to run:",
                "§e/freezeall",
                "§r"
            )
            item.itemMeta = this
        }
        return item
    }

    // ITEM: pvp
    fun pvpItems(): ItemStack {
        val item = ItemStack(Material.IRON_SWORD)
        val itemMeta = item.itemMeta
        itemMeta?.apply {
            setDisplayName("§6PVP")
            lore = listOf(
                "§r",
                "§7Click to run:",
                "§e/pvptoggle",
                "§r"
            )
            item.itemMeta = this
        }
        return item
    }

    // ITEM: lock votes
    fun lockVotesItems(): ItemStack {
        val item = ItemStack(Material.MAP)
        val itemMeta = item.itemMeta
        itemMeta?.apply {
            setDisplayName("§6Lock Votes")
            lore = listOf(
                "§r",
                "§7Click to run:",
                "§e/lockvotes",
                "§r"
            )
            item.itemMeta = this
        }
        return item
    }

    // ITEM: clear votes
    fun clearVotesItem(): ItemStack {
        val item = ItemStack(Material.CAULDRON)
        val itemMeta = item.itemMeta
        itemMeta?.apply {
            setDisplayName("§6Clear Votes")
            lore = listOf(
                "§r",
                "§c⚠ Don't forget to export votes: /exportvotes",
                "§r",
                "§7Click to run:",
                "§e/clearvotes",
                "§r"
            )
            item.itemMeta = this
        }
        return item
    }

    // ITEM: perm message
    fun permMessageItems(): ItemStack {
        val item = ItemStack(Material.MANGROVE_SIGN)
        val itemMeta = item.itemMeta
        itemMeta?.apply {
            setDisplayName("§6Permanent Message")
            lore = listOf(
                "§r",
                if (PermanentMessageCommand.PERMANENT_MESSAGE == null) "§7Perm Message: §8X"
                else "§7Perm Message:",
                if (PermanentMessageCommand.PERMANENT_MESSAGE != null) "§c${PermanentMessageCommand.PERMANENT_MESSAGE}" else null,
                "§r",
                "§7Click to prompt:",
                "§e/permanentmessage <message>",
                "§r"
            )
            item.itemMeta = this
        }
        return item
    }

    // ITEM: perm message clear
    fun permMessageClearItems(): ItemStack {
        val item = ItemStack(Material.CAULDRON)
        val itemMeta = item.itemMeta
        itemMeta?.apply {
            setDisplayName("§6Clear Permanent Message")
            lore = listOf(
                "§r",
                "§7Click to run:",
                "§e/permanentmessage",
                "§r"
            )
            item.itemMeta = this
        }
        return item
    }

    // ITEM: hide staff
    fun hideStaffItems(): ItemStack {
        val item = ItemStack(Material.ENDER_EYE)
        val itemMeta = item.itemMeta
        itemMeta?.apply {
            setDisplayName("§6Hide Staff")
            lore = listOf(
                "§r",
                "§7Click to run:",
                "§e/hidestaff",
                "§r"
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