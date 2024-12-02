package com.jjkay03.nationsevent.utils

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.MessageDigest
import java.net.URL

class ApplyResourcepack : Listener {
    companion object {
        // variables
        val PLUGIN = NationsEvent.INSTANCE
        val CONFIG = PLUGIN.config
        val RESOURCEPACK_ENABLED: Boolean = CONFIG.getBoolean("resourcepack-enable")
        val RESOURCEPACK_URL: String? = CONFIG.getString("resourcepack-url")
        var RESOURCEPACK_HASH: String? = CONFIG.getString("resourcepack-hash")
        private val EXEMPT_PLAYERS: List<String> = CONFIG.getStringList("resourcepack-players-exempt")

        // Apply server pack
        fun applyPack(player: Player) {
            if (!RESOURCEPACK_URL.isNullOrBlank() && !RESOURCEPACK_HASH.isNullOrBlank()) {
                player.setResourcePack(RESOURCEPACK_URL, RESOURCEPACK_HASH!!, true)
            } else {
                PLUGIN.logger.warning("Error sending resourcepack: URL or hash is missing in the configuration!")
            }
        }
    }

    init {
        // Download the resource pack if it's enabled
        if (RESOURCEPACK_ENABLED) downloadResourcePack()
    }

    // apply resourcepack on join
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (!RESOURCEPACK_ENABLED) return  // Cancel if resource pack is disabled
        val player = event.player
        if (EXEMPT_PLAYERS.contains(player.name)) return  // Skip if player is in the exempt list
        applyPack(player)  // Apply pack to player
    }

    // Function to download resourcepack to generate hash
    private fun downloadResourcePack() {
        // Path to save the downloaded resource pack
        val resourcepackFile = PLUGIN.dataFolder.resolve("downloads/resourcepack.zip")

        // Check if the URL is valid
        if (RESOURCEPACK_URL.isNullOrBlank()) { PLUGIN.logger.severe("Resource pack URL is missing in the configuration!"); return }

        try {
            // Download the resource pack from the URL
            val url = URL(RESOURCEPACK_URL)
            url.openStream().use { inputStream ->
                Files.createDirectories(resourcepackFile.parentFile.toPath())  // Create directories if they don't exist
                Files.copy(inputStream, resourcepackFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
            PLUGIN.logger.info("Resource pack downloaded successfully.")
            generateHash(resourcepackFile)  // Generate hash after downloading the resource pack
        } catch (e: Exception) {
            PLUGIN.logger.severe("Error downloading resource pack: ${e.message}")
        }
    }

    // Function that generates hash from pack
    private fun generateHash(resourcepackFile: File) {
        // Check if the resource pack file exists
        if (!resourcepackFile.exists()) { PLUGIN.logger.severe("Resource pack file not found at: ${resourcepackFile.absolutePath}"); return }

        try {
            // Generate the SHA-1 hash
            val digest = MessageDigest.getInstance("SHA-1")
            val fileBytes = Files.readAllBytes(resourcepackFile.toPath())
            val hashBytes = digest.digest(fileBytes)

            // Save the generated hash back to the configuration
            RESOURCEPACK_HASH = hashBytes.joinToString("") { "%02x".format(it) }
            CONFIG.set("resourcepack-hash", RESOURCEPACK_HASH)
            PLUGIN.saveConfig()

            PLUGIN.logger.info("Generated resource pack hash: $RESOURCEPACK_HASH")
        } catch (e: Exception) {
            PLUGIN.logger.severe("Error generating resource pack hash: ${e.message}")
        }
    }
}
