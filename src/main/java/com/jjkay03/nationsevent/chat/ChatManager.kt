package com.jjkay03.nationsevent.chat

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.utils.LuckPermsUtils
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ChatManager : Listener {

    // REGISTER CHAT MANAGER
    init {
        NationsEvent.INSTANCE.server.pluginManager.registerEvents(this, NationsEvent.INSTANCE)
        NationsEvent.INSTANCE.logger.info("- Loading Chat Manager")
    }

    @EventHandler
    fun onPlayerChat(event: AsyncChatEvent) {
        // Cancel the default chat format
        event.isCancelled = true

        // Get player and message
        val player = event.player
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
