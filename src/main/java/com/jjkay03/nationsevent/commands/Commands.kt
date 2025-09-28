package com.jjkay03.nationsevent.commands

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.commands.announce.*
import com.jjkay03.nationsevent.commands.management.*
import com.jjkay03.nationsevent.commands.others.HideStaffCommand
import com.jjkay03.nationsevent.commands.others.SmiteCommand
import com.jjkay03.nationsevent.commands.player_assistance.*
import com.jjkay03.nationsevent.commands.player_scale.*
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
            RandomPlayerTPCommand("randomplayertp"),
            PVPToggleCommand("pvptoggle"),
            FreezeAllCommand("freezeall"),
            GlobalChatCommand("globalchat"),

            // COMMANDS : OTHERS
            SmiteCommand("smite"),
            HideStaffCommand("hidestaff"),

            // COMMANDS : PLAYER ASSISTANCE
            NeedStaffCommand("needstaff"),
            NeedStaffManageCommand("needstaffmanage"),
            NeedRecordCommand("needrecord"),
            NeedRecordManageCommand("needrecordmanage"),

            // COMMANDS : PLAYER SCALE
            PlayerScaleCommand("playerscale"),
            PlayerScaleRestAllCommand("playerscalerestall"),

            // COMMANDS: VOICE CHAT
            VoiceChatPermsCommand("voicechatperms"),
            GroupVoiceChatPermsCommand("groupvoicechatperms")
        )

        // Feedback
        NationsEvent.INSTANCE.logger.info("- Register all commands (${commands.size})")
    }

}
