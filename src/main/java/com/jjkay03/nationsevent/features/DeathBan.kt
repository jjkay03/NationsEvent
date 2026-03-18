package com.jjkay03.nationsevent.features

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.Utils
import com.jjkay03.nationsevent.utils.Config
import com.jjkay03.nationsevent.utils.Scheduler
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

class DeathBan : Listener {

    // INITIALIZATION
    init {
        load(Config.FEATURES_DEATH_BAN_ENABLE)
    }

    // LOAD (If enabled in config)
    fun load(enabled: Boolean) {
        // End if feature disabled
        if (!enabled) return

        // Register event
        Bukkit.getPluginManager().registerEvents(this, NationsEvent.INSTANCE)

        // Console message
        NationsEvent.INSTANCE.logger.info("- Loading feature: ${this::class.simpleName}")
    }

    // Detect and handler player death
    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.entity
        val originalDeathMessage = event.deathMessage

        // Strike fake lightning if enabled
        if (Config.FEATURES_DEATH_BAN_LIGHTNING) player.world.strikeLightningEffect(player.location)

        // Ban after delay to ensure death message and item drop
        Scheduler.taskDelayed(
            type = Scheduler.SchedulerType.PLAYER,
            delayTicks = 20L,
            task = { banPlayer(player) },
            player = player
        )

        // Death message
        event.deathMessage(Component.text("§4☠ ${player.name} died"))

        // Send real death message to staff with clickable teleport
        val staffMessage = Component.text("ℹ ", NamedTextColor.GRAY)
            .append(Component.text(originalDeathMessage ?: "${player.name} died", NamedTextColor.GRAY))
            .clickEvent(ClickEvent.runCommand("/tpdeath ${player.name}"))
            .hoverEvent(HoverEvent.showText(Component.text("Click to teleport to ${player.name}'s death location").color(NamedTextColor.YELLOW)))

        Utils.messagePlayerWithPerm(staffMessage, Saves.PERM_STAFF)
    }

    // Helper function to ban player if they don't have bypass perm
    private fun banPlayer(player: Player) {
        if (player.hasPermission(Saves.PERM_DEATH_BAN_BYPASS)) return
        player.banPlayer(Config.FEATURES_DEATH_BAN_MESSAGE)
    }
}
