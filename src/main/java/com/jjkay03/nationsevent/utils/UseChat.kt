package com.jjkay03.nationsevent.utils

import com.jjkay03.nationsevent.Saves
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class UseChat : Listener {

    private val bypassPermissions = listOf(Saves.PERM_ADMIN, Saves.PERM_PROD)

    @EventHandler
    fun onPlayerChat(event: AsyncChatEvent) {
        val player = event.player

        // Allow chat if the player has the use chat permission
        if (player.hasPermission(Saves.PERM_USE_CHAT)) return

        // Allow chat if the player has any of the bypass permissions
        if (bypassPermissions.any { player.hasPermission(it) }) return

        // Cancel chat event and send message
        event.isCancelled = true
        player.sendMessage("§cChat is disabled!")
    }
}
