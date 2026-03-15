package com.jjkay03.nationsevent.specific

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.integrations.luckperms.LuckPermsManager
import com.jjkay03.nationsevent.specific.island_both.*
import com.jjkay03.nationsevent.specific.island_cold.*
import com.jjkay03.nationsevent.specific.island_hot.*
import com.jjkay03.nationsevent.specific.ns8.*
import com.jjkay03.nationsevent.specific.ns8.commands.*
import com.jjkay03.nationsevent.utils.Config
import net.luckperms.api.model.group.Group
import org.bukkit.Bukkit
import org.bukkit.World

class EventSpecific {

    companion object {
        const val SPECIFIC_EVENT_CODENAME: String = "NS8"
        var SPECIFIC_EVENT_LOADED: Boolean = false

        // LuckPerms groups
        val LP_GROUP_NS8_HOT: Group? = LuckPermsManager.LP_GROUP_MANAGER.getGroup("ns8-hot")
        val LP_GROUP_NS8_COLD: Group? = LuckPermsManager.LP_GROUP_MANAGER.getGroup("ns8-cold")

        // Worlds (lazy loaded when first accessed)
        val WORLD_NS8_HOT: World? by lazy { Bukkit.getWorld("world_ns8_hot") }
        val WORLD_NS8_COLD: World? by lazy { Bukkit.getWorld("world_ns8_cold") }
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
        QueueToIslandCommand("queuetoisland")

        // FEATURES
        AssignRandomTeam()

        // HOT FIX
        IllegalStructuresHotFix()

        // FEATURES ISLANDS - BOTH
        IslandBothFishing()
        IslandBothPlayerMeat()
        // FEATURES ISLANDS - HOT
        IslandHotBucket()
        IslandHotPlants()
        IslandHotMob()
        IslandHotFire()
        // FEATURES ISLANDS - COLD
        IslandColdBucket()
        IslandColdPlants()
        IslandColdMobs()
        IslandColdWater()
    }
}