package com.jjkay03.nationsevent.integrations.voicechat

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.integrations.voicechat.commands.BroadcastVoiceCommand
import de.maxhenkel.voicechat.api.BukkitVoicechatService
import de.maxhenkel.voicechat.api.VoicechatApi
import de.maxhenkel.voicechat.api.VoicechatPlugin
import de.maxhenkel.voicechat.api.VoicechatServerApi
import de.maxhenkel.voicechat.api.events.EventRegistration
import org.bukkit.Bukkit

class SimpleVoiceChat {

    // COMPANIONS
    companion object {
        var SVC_API: VoicechatApi? = null
        var SVC_SERVER_API: VoicechatServerApi? = null
        var SVC_PLUGIN: VoicechatPlugin? = null
        var SVC_EVENT_REGISTRATION: EventRegistration? = null
    }

    // INITIALIZATION
    init { load() }

    // LOAD
    fun load() {
        // End if no voice chat API
        if (!getAPI()) { NationsEvent.INSTANCE.logger.warning("- Failed to load integration: ${this::class.simpleName}"); return }

        // Log in console
        NationsEvent.INSTANCE.logger.info("- Loading integration: ${this::class.simpleName}")
    }

    // Helper function to get voice chat api
    private fun getAPI(): Boolean {
        val service = Bukkit.getServicesManager().load(BukkitVoicechatService::class.java)
        if (service == null) { return false }

        // Create plugin instance
        val plugin = object : VoicechatPlugin {
            override fun getPluginId(): String = "nationsevent"
            override fun initialize(api: VoicechatApi) {
                SVC_API = api
                if (api is VoicechatServerApi) SVC_SERVER_API = api
            }

            // Register voice chat events
            override fun registerEvents(registration: EventRegistration) {
                SVC_EVENT_REGISTRATION = registration

                // Commands
                BroadcastVoiceCommand("broadcastvoice")
            }
        }

        // Register the plugin
        service.registerPlugin(plugin)
        SVC_PLUGIN = plugin

        return true
    }

}
