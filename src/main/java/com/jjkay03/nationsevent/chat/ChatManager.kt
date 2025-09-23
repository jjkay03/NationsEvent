package com.jjkay03.nationsevent.chat

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.utils.LuckPermsUtils
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ChatManager : Listener {

    // INITIALIZATION (Register events)
    init {
        NationsEvent.INSTANCE.server.pluginManager.registerEvents(this, NationsEvent.INSTANCE)
        NationsEvent.INSTANCE.logger.info("- Loading Chat Manager")
    }

    @EventHandler
    fun onPlayerChat(event: AsyncChatEvent) {
        // Get player
        val player = event.player

        // Cancel the default chat
        event.isCancelled = true

        // End if player doesn't have chat permission
        if (!player.hasPermission(Saves.PERM_USE_CHAT)) { player.sendMessage("§cChat is disabled!"); return }

        // Get message
        val message = MiniMessage.miniMessage().serialize(event.message())

        // Get player prefix color from LuckPerms
        // Use prefix and suffix example: "<gold>" for chat color
        val lpPrefix = LuckPermsUtils.playerPrefix(player)
        val lpSuffix = LuckPermsUtils.playerSuffix(player)

        // Format message
        val formattedMessage = "$lpPrefix${player.name}<white>: $lpSuffix$message"

        // Send to all recipients
        event.viewers().forEach { recipient ->
            recipient.sendMessage(MiniMessage.miniMessage().deserialize(formattedMessage))
        }
    }
}
