package com.jjkay03.nationsevent.specific

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.specific.ns7.AssignRandomTeam
import com.jjkay03.nationsevent.specific.ns7.commands.ApplyPreferredTeamCommand
import com.jjkay03.nationsevent.specific.ns7.commands.AssignRandomTeamCommand
import com.jjkay03.nationsevent.specific.ns7.commands.PreferredTeamCommand
import com.jjkay03.nationsevent.utils.Config
import net.luckperms.api.model.group.Group

class EventSpecific {

    companion object {
        const val SPECIFIC_EVENT_CODENAME: String = "NS7"
        var SPECIFIC_EVENT_LOADED: Boolean = false

        // LUCKPERMS GROUPS
        val LP_GROUP_NS7_PLAINS: Group? = NationsEvent.LP_GROUP_MANAGER.getGroup("ns7-plains")
        val LP_GROUP_NS7_DESERT: Group? = NationsEvent.LP_GROUP_MANAGER.getGroup("ns7-desert")
        val LP_GROUP_NS7_SNOW: Group? = NationsEvent.LP_GROUP_MANAGER.getGroup("ns7-snow")
    }

    // INITIALIZATION
    init {
        if (Config.EVENT_CODENAME == SPECIFIC_EVENT_CODENAME) load()
    }

    // LOAD
    fun load() {
        SPECIFIC_EVENT_LOADED = true

        // Log season specific loading
        NationsEvent.INSTANCE.logger.info("- Loading event specific code: $SPECIFIC_EVENT_CODENAME")

        // COMMANDS
        PreferredTeamCommand("preferredteam")
        ApplyPreferredTeamCommand("applypreferredteam")
        AssignRandomTeamCommand("assignrandomteam")

        // FEATURES
        AssignRandomTeam()

    }
}