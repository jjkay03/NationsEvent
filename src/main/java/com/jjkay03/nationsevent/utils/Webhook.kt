package com.jjkay03.nationsevent.utils

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.Bukkit
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import com.google.gson.Gson
import java.nio.charset.StandardCharsets

object Webhook {

    // Function to send webhook message to discord
    fun send(webhookUrl: String, message: String) {
        Bukkit.getScheduler().runTaskAsynchronously(NationsEvent.INSTANCE, Runnable {
            try {
                val url = URL(webhookUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                connection.doOutput = true

                // Use Gson or another library to serialize JSON properly
                val gson = Gson()
                val payload = gson.toJson(mapOf("content" to message))

                // Specify UTF-8
                OutputStreamWriter(connection.outputStream, StandardCharsets.UTF_8).use { it.write(payload) }

                // Check response
                val responseCode = connection.responseCode
                if (responseCode !in 200..299) {
                    Bukkit.getLogger().warning("Webhook failed: HTTP $responseCode - ${connection.responseMessage}")
                }

                connection.disconnect()

            } catch (e: Exception) {
                Bukkit.getLogger().warning("Failed to send Discord webhook! Reason: ${e.message}")
            }
        })
    }


}
