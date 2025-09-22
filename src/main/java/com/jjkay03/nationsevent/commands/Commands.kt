package com.jjkay03.nationsevent.commands

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.commands.announce.*
import com.jjkay03.nationsevent.commands.management.*
import com.jjkay03.nationsevent.commands.player_scale.*

class Commands {

    init {
        val commands = listOf(
            // COMMANDS : ANNOUNCE
            AnnounceSessionCommand("announcesession"),
            JoinStageCommand("joinstage"),
            JoinVCCommand("joinvc"),

            // COMMANDS : MANAGEMENT
            SessionTimeCommand("sessiontime"),

            // COMMANDS : PLAYER SCALE
            PlayerScaleCommand("playerscale"),
            PlayerScaleRestAllCommand("playerscalerestall")
        )

        // Feedback
        NationsEvent.INSTANCE.logger.info("- Register all commands (${commands.size})")
    }

}
