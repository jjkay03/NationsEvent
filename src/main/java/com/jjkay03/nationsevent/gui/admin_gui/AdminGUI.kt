package com.jjkay03.nationsevent.gui.admin_gui

import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.Utils
import org.bukkit.Material
import org.bukkit.Sound
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
        adminGUI.getSlot(2).item = AdminGUI_Items.statusItem("global_chat", player)
        adminGUI.getSlot(3).item = AdminGUI_Items.statusItem("frozen_players", player)
        adminGUI.getSlot(4).item = AdminGUI_Items.statusItem("pvp", player)
        adminGUI.getSlot(5).item = AdminGUI_Items.statusItem("locked_votes", player)
        adminGUI.getSlot(6).item = AdminGUI_Items.statusItem("permanent_message", player)
        adminGUI.getSlot(7).item = AdminGUI_Items.statusItem("hide_staff", player)

        // GUI Items Buttons
        val announceSessionSlot = adminGUI.getSlot(9).apply { item = AdminGUI_Items.announceSessionItem() }
        val sessionTimeSlot = adminGUI.getSlot(18).apply { item = AdminGUI_Items.sessionTimeItem() }
        val voicechatSlot = adminGUI.getSlot(10).apply { item = AdminGUI_Items.voicechatItem() }
        val globalChatSlot = adminGUI.getSlot(11).apply { item = AdminGUI_Items.globalChatItem() }
        val freezePlayersSlot = adminGUI.getSlot(12).apply { item = AdminGUI_Items.freezePlayersItems() }
        val pvpSlot = adminGUI.getSlot(13).apply { item = AdminGUI_Items.pvpItems() }
        val lockVotesSlot = adminGUI.getSlot(14).apply { item = AdminGUI_Items.lockVotesItems() }
        val clearVotesSlot = adminGUI.getSlot(23).apply { item = AdminGUI_Items.clearVotesItem() }
        val permMessageSlot = adminGUI.getSlot(15).apply { item = AdminGUI_Items.permMessageItems() }
        val permMessageClearSlot = adminGUI.getSlot(24).apply { item = AdminGUI_Items.permMessageClearItems() }
        val hideStaffSlot = adminGUI.getSlot(16).apply { item = AdminGUI_Items.hideStaffItems() }

        // Click Handlers
        announceSessionSlot.setClickHandler { clickPlayer, info -> buttonPromptCommand(clickPlayer, "announcesession") } // TODO: Change to promote
        sessionTimeSlot.setClickHandler { clickPlayer, info -> buttonRunCommand(clickPlayer, "sessiontime") }
        voicechatSlot.setClickHandler { clickPlayer, info -> buttonToggleVoicechatPerms(clickPlayer) }
        globalChatSlot.setClickHandler { clickPlayer, info -> buttonToggleGlobalChat(clickPlayer) }
        freezePlayersSlot.setClickHandler { clickPlayer, info -> buttonRunCommand(clickPlayer, "freezeall") }
        pvpSlot.setClickHandler { clickPlayer, info -> buttonRunCommand(clickPlayer, "pvptoggle") }
        lockVotesSlot.setClickHandler { clickPlayer, info -> buttonRunCommand(clickPlayer, "lockvotes") }
        clearVotesSlot.setClickHandler { clickPlayer, info -> buttonPromptCommand(clickPlayer, "clearvotes CONFIRM") } // TODO: Change to promote
        permMessageSlot.setClickHandler { clickPlayer, info -> buttonPromptCommand(clickPlayer, "permanentmessage") } // TODO: Change to promote
        permMessageClearSlot.setClickHandler { clickPlayer, info -> buttonRunCommand(clickPlayer, "permanentmessage") }
        hideStaffSlot.setClickHandler { clickPlayer, info -> buttonRunCommand(clickPlayer, "hidestaff") }
    }

    // Function to run command after button press
    private fun buttonRunCommand(player: Player, command: String) {
        player.performCommand(command)
        player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f)
        updateGUI(player)
    }

    // Function to prompt command after button press
    private fun buttonPromptCommand(player: Player, command: String) {
        player.sendMessage("§cPrompt command feature not available yet!")
        //player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f)
        updateGUI(player)
    }

    // Function to toggle voicechat perms
    private fun buttonToggleVoicechatPerms(player: Player) {
        if (Utils.luckPermsGroupHasPermission(Saves.LP_GROUP_DEFAULT, Saves.PERM_SIMPLE_VOICECHAT_SPEAK)) {
            player.performCommand("voicechatperms off")
            player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f)
            adminGUI.getSlot(1).item = AdminGUI_Items.DISABLED_ITEM // Flip status item
        }
        else {
            player.performCommand("voicechatperms on")
            player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f)
            adminGUI.getSlot(1).item = AdminGUI_Items.ENABLED_ITEM // Flip status item
        }
    }

    // Function to toggle voicechat perms
    private fun buttonToggleGlobalChat(player: Player) {
        if (Utils.luckPermsGroupHasPermission(Saves.LP_GROUP_DEFAULT, Saves.PERM_USE_CHAT)) {
            player.performCommand("globalchat off")
            player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f)
            adminGUI.getSlot(2).item = AdminGUI_Items.DISABLED_ITEM // Flip status item
        }
        else {
            player.performCommand("globalchat on")
            player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f)
            adminGUI.getSlot(2).item = AdminGUI_Items.ENABLED_ITEM // Flip status item
        }
    }

}

