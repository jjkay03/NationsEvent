package com.jjkay03.nationsevent.specific

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.integrations.luckperms.LuckPermsManager
import com.jjkay03.nationsevent.specific.ne5.commands.*
import com.jjkay03.nationsevent.utils.Config
import net.luckperms.api.model.group.Group
import org.bukkit.Bukkit
import org.bukkit.World

class EventSpecific {

    companion object {
        const val SPECIFIC_EVENT_CODENAME: String = "NE5"
        var SPECIFIC_EVENT_LOADED: Boolean = false

        // LuckPerms groups
        val LP_GROUP_NE5_SOLDIER: Group? = LuckPermsManager.LP_GROUP_MANAGER.getGroup("ne5-soldier")
        // NS8 STUFF LEFT FOR FUTURE MIGRATION
        val LP_GROUP_NS8_HOT: Group? = LuckPermsManager.LP_GROUP_MANAGER.getGroup("ns8-hot")
        val LP_GROUP_NS8_COLD: Group? = LuckPermsManager.LP_GROUP_MANAGER.getGroup("ns8-cold")

        // Worlds (lazy loaded when first accessed)
        val WORLD_NE5: World? by lazy { Bukkit.getWorld("nations_ne5") }
        // NS8 STUFF LEFT FOR FUTURE MIGRATION
        val WORLD_NS8_HOT: World? by lazy { Bukkit.getWorld("world_ns8_hot") }
        val WORLD_NS8_COLD: World? by lazy { Bukkit.getWorld("world_ns8_cold") }

        // Perms
        //const val PERM_NS8_BYPASS_FROSTBITE = "nationsevent.ns8.bypass.frostbite"
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
        //PreferredTeamCommand("preferredteam")
        //ApplyPreferredTeamCommand("applypreferredteam")
        //AssignRandomTeamCommand("assignrandomteam")
        GoToIslandCommand("gotoisland")
        QueueToIslandCommand("queuetoisland")
        MakeSoldierCommand("makesoldier")

        // FEATURES
        //AssignRandomTeam()
    }
}