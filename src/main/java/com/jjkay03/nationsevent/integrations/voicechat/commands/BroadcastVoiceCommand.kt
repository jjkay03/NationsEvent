package com.jjkay03.nationsevent.integrations.voicechat.commands

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.integrations.voicechat.SimpleVoiceChat
import com.jjkay03.nationsevent.integrations.voicechat.SimpleVoiceChatUtils
import de.maxhenkel.voicechat.api.Group
import de.maxhenkel.voicechat.api.VoicechatConnection
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.Listener

class BroadcastVoiceCommand(private val commandName: String) : CommandExecutor, Listener {

    private val broadcastGroupName = "broadcast"

    init {
        // Register command
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
        // Register voice chat events
        SimpleVoiceChat.SVC_EVENT_REGISTRATION?.registerEvent(MicrophonePacketEvent::class.java, ::onMicrophonePacket)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // Check if sender is player
        if (sender !is Player) { sender.sendMessage("§cOnly players can use this command!"); return true }

        // Check API
        val api = SimpleVoiceChat.SVC_SERVER_API
        if (api == null) { sender.sendMessage("§cVoice chat API not available!"); return true }

        // Check player connected to voice chat
        val playerUuid = sender.uniqueId
        val connection = api.getConnectionOf(playerUuid) ?: run { sender.sendMessage("§cYou are not connected to voicechat!"); return true }

        // Get or create broadcast group
        val broadcastGroup = SimpleVoiceChatUtils.createOrGetGroup(
            broadcastGroupName,
            persistent = false,
            hidden = false,
            ignoreCase = true,
            type = Group.Type.OPEN
        ) ?: return true

        // Toggle broadcast
        if (connection.group?.id == broadcastGroup.id) {
            // Disable broadcast
            connection.group = null
            sender.sendMessage("§7🔊 Simple Voice Chat broadcast §cDISABLED")
        } else {
            // Enable broadcast
            connection.group = broadcastGroup
            sender.sendMessage("§7🔊 Simple Voice Chat broadcast §aENABLED §7(everyone can hear you!)")
        }

        return true
    }

    // Event on microphone packet
    private fun onMicrophonePacket(event: MicrophonePacketEvent) {
        // Get connection
        val senderConnection = event.senderConnection ?: return

        // End if player not broadcasting
        if (!isPlayerBroadcasting(senderConnection)) return

        // Check if player has permission to broadcast
        val player = Bukkit.getPlayer(senderConnection.player.uuid) ?: return
        if (!player.hasPermission(Saves.PERM_COMMAND_BROADCAST_VOICE)) {
            player.sendActionBar(Component.text("§cYou don't have permission to voicechat broadcast!"))
            senderConnection.group = null // Remove player from group
            return
        }

        // Cancel the default packet so nearby players don't hear it twice
        event.cancel()

        // Broadcast sound to all players except the sender
        broadcastToAllPlayers(event, senderConnection)
    }

    // Helper function to check if player is broadcasting (in "broadcast" group)
    private fun isPlayerBroadcasting(connection: VoicechatConnection): Boolean {
        val broadcastGroup = SimpleVoiceChatUtils.createOrGetGroup(broadcastGroupName, ignoreCase = true) ?: return false
        return connection.group?.id == broadcastGroup.id
    }

    // Helper function to broadcast sound to all players except the broadcaster
    private fun broadcastToAllPlayers(event: MicrophonePacketEvent, senderConnection: VoicechatConnection) {
        val api = SimpleVoiceChat.SVC_SERVER_API ?: return
        val packet = event.packet.staticSoundPacketBuilder().build()

        // Send packet to everyone except the broadcaster
        Bukkit.getOnlinePlayers().forEach { player ->
            if (player.uniqueId == senderConnection.player.uuid) return@forEach // Skip the broadcaster to avoid echo
            val connection = api.getConnectionOf(player.uniqueId) ?: return@forEach
            api.sendStaticSoundPacketTo(connection, packet)
        }
    }

}