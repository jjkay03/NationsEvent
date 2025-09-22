package com.jjkay03.nationsevent

import com.jjkay03.nationsevent.utils.ServerType
import org.bukkit.Bukkit

object Utils {

    // Function to display plugin welcome message
    fun pluginWelcomeMessage(color: String) {
        val welcomeMessage = mutableListOf(
            "$color  _   _       _   _                 ______               _   ",
            "$color | \\ | |     | | (_)               |  ____|             | |  ",
            "$color |  \\| | __ _| |_ _  ___  _ __  ___| |____   _____ _ __ | |_ ",
            "$color | . ` |/ _` | __| |/ _ \\| '_ \\/ __|  __\\ \\ / / _ \\ '_ \\| __|",
            "$color | |\\  | (_| | |_| | (_) | | | \\__ \\ |___\\ V /  __/ | | | |_ ",
            "$color |_| \\_|\\__,_|\\__|_|\\___/|_| |_|___/______\\_/ \\___|_| |_|\\__|"
        )
        if (ServerType.SERVER_TYPE == ServerType.ThreadingType.MULTI_THREADED_FOLIA) welcomeMessage.add("$color THREADED VERSION §2(FOLIA)")
        welcomeMessage.add("")
        welcomeMessage.forEach { line -> Bukkit.getConsoleSender().sendMessage(line) }
        NationsEvent.INSTANCE.logger.info("NationsEvent is running!")
        NationsEvent.INSTANCE.logger.info("Plugin version: ${NationsEvent.INSTANCE.description.version}")
    }

}