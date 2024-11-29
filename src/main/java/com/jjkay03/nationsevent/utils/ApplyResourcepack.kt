package com.jjkay03.nationsevent.utils

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.event.Listener
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.MessageDigest
import java.net.URL

class ApplyResourcepack : Listener {
    private val plugin = NationsEvent.INSTANCE
    private val config = plugin.config
    private val resourcepackEnabled: Boolean = config.getBoolean("resourcepack-enable")
    private val resourcepackUrl: String? = config.getString("resourcepack-url")
    private var resourcepackHash: String? = config.getString("resourcepack-hash")
    private val exemptPlayers: List<String> = config.getStringList("resourcepack-players-exempt")

    init {
        // Download the resource pack if it's enabled
        if (resourcepackEnabled) downloadResourcePack()
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        // Cancel if resource pack is disabled
        if (!resourcepackEnabled) return

        val player = event.player

        // Skip if player is in the exempt list
        if (exemptPlayers.contains(player.name)) return

        // Send the resource pack
        if (!resourcepackUrl.isNullOrBlank() && !resourcepackHash.isNullOrBlank()) {
            player.setResourcePack(resourcepackUrl, resourcepackHash!!, true)
        } else {
            plugin.logger.warning("Error sending resourcepack: URL or hash is missing in the configuration!")
        }
    }

    // Function to download resourcepack to generate hash
    private fun downloadResourcePack() {
        // Path to save the downloaded resource pack
        val resourcepackFile = plugin.dataFolder.resolve("downloads/resourcepack.zip")

        // Check if the URL is valid
        if (resourcepackUrl.isNullOrBlank()) { plugin.logger.severe("Resource pack URL is missing in the configuration!"); return }

        try {
            // Download the resource pack from the URL
            val url = URL(resourcepackUrl)
            url.openStream().use { inputStream ->
                Files.createDirectories(resourcepackFile.parentFile.toPath())  // Create directories if they don't exist
                Files.copy(inputStream, resourcepackFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
            plugin.logger.info("Resource pack downloaded successfully.")
            generateHash(resourcepackFile)  // Generate hash after downloading the resource pack
        } catch (e: Exception) {
            plugin.logger.severe("Error downloading resource pack: ${e.message}")
        }
    }

    // Function that generates hash from pack
    private fun generateHash(resourcepackFile: File) {
        // Check if the resource pack file exists
        if (!resourcepackFile.exists()) { plugin.logger.severe("Resource pack file not found at: ${resourcepackFile.absolutePath}"); return }

        try {
            // Generate the SHA-1 hash
            val digest = MessageDigest.getInstance("SHA-1")
            val fileBytes = Files.readAllBytes(resourcepackFile.toPath())
            val hashBytes = digest.digest(fileBytes)

            // Save the generated hash back to the configuration
            resourcepackHash = hashBytes.joinToString("") { "%02x".format(it) }
            config.set("resourcepack-hash", resourcepackHash)
            plugin.saveConfig()

            plugin.logger.info("Generated resource pack hash: $resourcepackHash")
        } catch (e: Exception) {
            plugin.logger.severe("Error generating resource pack hash: ${e.message}")
        }
    }
}
