package com.jjkay03.nationsevent.commands.player_assistance

import com.jjkay03.nationsevent.Saves
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap

object PlayerAssistance {

    // Lists
    val NEED_STAFF_REQUESTS = ConcurrentHashMap<Player, NeedStaffReasons>()
    val NEED_RECORD_REQUESTS: MutableSet<Player> = ConcurrentHashMap.newKeySet()

    // Need staff reasons enum
    enum class NeedStaffReasons {
        BUG,
        HELP,
        QUESTION,
        REPORT,
        OTHER
    }

    // Function to alert staff of need staff
    fun alertNeedStaff(player: Player, reason: NeedStaffReasons) {
        Bukkit.getServer().onlinePlayers.forEach { staffPlayer ->
            if (staffPlayer.hasPermission(Saves.PERM_STAFF)) {
                val message = Component.text("⚑ [NS✋] Staff assistance: ", NamedTextColor.GOLD)
                    .append(Component.text(player.name, NamedTextColor.WHITE))
                    .append(Component.text(" (${reason.name})", NamedTextColor.GRAY))
                    .clickEvent(ClickEvent.runCommand("/tp ${player.name}"))
                    .hoverEvent(HoverEvent.showText(Component.text("Click to teleport to ${player.name}").color(NamedTextColor.YELLOW)))

                staffPlayer.sendMessage(message)
            }
        }
    }

    // Function to alert prod of need record
    fun alertNeedRecord(player: Player, playersAround: Int) {
        Bukkit.getServer().onlinePlayers.forEach { prodPlayer ->
            if (prodPlayer.hasPermission(Saves.PERM_STAFF)) {
                val message = Component.text("⚑ [NR📷] Record request: ", NamedTextColor.GOLD)
                    .append(Component.text(player.name, NamedTextColor.WHITE))
                    .append(Component.text(" ($playersAround)", NamedTextColor.GRAY))
                    .clickEvent(ClickEvent.runCommand("/tp ${player.name}"))
                    .hoverEvent(HoverEvent.showText(Component.text("Click to teleport to ${player.name}").color(NamedTextColor.YELLOW)))

                prodPlayer.sendMessage(message)
            }
        }
    }


}
