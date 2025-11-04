package com.jjkay03.nationsevent.specific

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.specific.ns7.AssignRandomTeam
import com.jjkay03.nationsevent.specific.ns7.commands.*
import com.jjkay03.nationsevent.utils.Config
import net.luckperms.api.model.group.Group
import org.bukkit.Bukkit
import org.bukkit.World

class EventSpecific {

    companion object {
        const val SPECIFIC_EVENT_CODENAME: String = "NS7"
        var SPECIFIC_EVENT_LOADED: Boolean = false

        // Worlds (lazy loaded when first accessed)
        val WORLD_NS7_PLAINS: World? by lazy { Bukkit.getWorld("world_ns7_1_plains") }
        val WORLD_NS7_DESERT: World? by lazy { Bukkit.getWorld("world_ns7_2_desert") }
        val WORLD_NS7_SNOW: World? by lazy { Bukkit.getWorld("world_ns7_3_snow") }

        // LuckPerms groups
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
        GoToIslandCommand("gotoisland")

        // FEATURES
        AssignRandomTeam()

    }
}