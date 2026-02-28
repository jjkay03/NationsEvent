package com.jjkay03.nationsevent.commands

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.commands.announce.*
import com.jjkay03.nationsevent.commands.management.*
import com.jjkay03.nationsevent.commands.others.*
import com.jjkay03.nationsevent.commands.player_assistance.*
import com.jjkay03.nationsevent.commands.player_scale.*
import com.jjkay03.nationsevent.commands.teleport.*
import com.jjkay03.nationsevent.commands.utility.ListLuckPermsGroupsCommand
import com.jjkay03.nationsevent.commands.utility.ListStatesCommand
import com.jjkay03.nationsevent.commands.utility.ListWorldsCommand
import com.jjkay03.nationsevent.commands.voice_chat.*

class Commands {

    init {
        val commands = listOf(
            // COMMANDS : ANNOUNCE
            AnnounceSessionCommand("announcesession"),
            JoinStageCommand("joinstage"),
            JoinVCCommand("joinvc"),
            PermanentMessageCommand("permanentmessage"),

            // COMMANDS : MANAGEMENT
            SessionTimeCommand("sessiontime"),
            PVPToggleCommand("pvptoggle"),
            FreezeAllCommand("freezeall"),
            GlobalChatCommand("globalchat"),

            // COMMANDS : OTHERS
            SmiteCommand("smite"),
            HideStaffCommand("hidestaff"),
            GamemodeCommand("gamemode"),
            FlyCommand("fly"),
            FlySpeedCommand("flyspeed"),
            GodCommand("god"),
            SudoCommand("sudo"),
            HealCommand("heal"),

            // COMMANDS : PLAYER ASSISTANCE
            NeedStaffCommand("needstaff"),
            NeedStaffManageCommand("needstaffmanage"),
            NeedRecordCommand("needrecord"),
            NeedRecordManageCommand("needrecordmanage"),

            // COMMANDS : PLAYER SCALE
            PlayerScaleCommand("playerscale"),
            PlayerScaleRestAllCommand("playerscalerestall"),

            // COMMANDS : TELEPORT
            TeleportBackCommand("teleportback"),
            TeleportHereCommand("teleporthere"),
            TeleportJumpCommand("teleportjump"),
            TeleportPlayerCommand("teleportplayer"),
            TeleportPositionCommand("teleportposition"),
            TeleportRandomPlayerCommand("teleportrandomplayer"),
            TeleportWorldCommand("teleportworld"),
            TeleportTopCommand("teleporttop"),
            TeleportOfflineCommand("teleportoffline"),

            // COMMANDS : UTILITY
            ListLuckPermsGroupsCommand("listluckpermsgroups"),
            ListWorldsCommand("listworlds"),
            ListStatesCommand("liststates"),

            // COMMANDS: VOICE CHAT
            VoiceChatPermsCommand("voicechatperms"),
            GroupVoiceChatPermsCommand("groupvoicechatperms")
        )

        // Feedback
        NationsEvent.INSTANCE.logger.info("- Register all commands (${commands.size})")
    }

}
