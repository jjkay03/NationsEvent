package com.jjkay03.nationsevent.commands

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.commands.announce.*
import com.jjkay03.nationsevent.commands.management.*
import com.jjkay03.nationsevent.commands.others.SmiteCommand
import com.jjkay03.nationsevent.commands.player_scale.*

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

            // COMMANDS : PLAYER SCALE
            PlayerScaleCommand("playerscale"),
            PlayerScaleRestAllCommand("playerscalerestall")
        )

        // Feedback
        NationsEvent.INSTANCE.logger.info("- Register all commands (${commands.size})")
    }

}
