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
import java.util.*

class ApplyResourcepack : Listener {
    companion object {
        // variables
        val PLUGIN = NationsEvent.INSTANCE
        val CONFIG = PLUGIN.config
        val RESOURCEPACKS_ENABLED: Boolean = CONFIG.getBoolean("resourcepacks-enable")
        val RESOURCEPACKS_URLS: List<String> = CONFIG.getStringList("resourcepacks-urls")
        var RESOURCEPACKS_HASHES: List<String> = CONFIG.getStringList("resourcepacks-hashes")
        private val EXEMPT_PLAYERS: List<String> = CONFIG.getStringList("resourcepacks-players-exempt")

        // Apply server pack
        fun applyPack(player: Player) {
            if (RESOURCEPACKS_URLS.isNotEmpty() && RESOURCEPACKS_HASHES.isNotEmpty()) {
                //player.setResourcePack(RESOURCEPACK_URL[0], RESOURCEPACK_HASH[0], true)
                for (i in RESOURCEPACKS_URLS.indices) {
                    player.addResourcePack(
                        UUID.randomUUID(),
                        RESOURCEPACKS_URLS[i],
                        hexStringToByteArray(RESOURCEPACKS_HASHES[i]),
                        "REQUIRED PACK",
                        true
                    )
                }
            } else {
                PLUGIN.logger.warning("Error sending resourcepack: URL or hash is missing in the configuration!")
            }
        }

        private fun hexStringToByteArray(hexString: String): ByteArray {
            val len = hexString.length
            val data = ByteArray(len / 2)
            for (i in 0 until len step 2) {
                val byteValue = hexString.substring(i, i + 2).toInt(16)
                data[i / 2] = byteValue.toByte()
            }
            return data
        }
    }

    init {
        // Download the resource pack if it's enabled
        if (RESOURCEPACKS_ENABLED) {
            downloadResourcePacks()
            val downloadedPacks = getDownloadedResourcePacks()
            generateHashes(downloadedPacks)
        }
    }

    // apply resourcepack on join
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (!RESOURCEPACKS_ENABLED) return  // Cancel if resource pack is disabled
        val player = event.player
        if (EXEMPT_PLAYERS.contains(player.name)) return  // Skip if player is in the exempt list
        applyPack(player)  // Apply pack to player
    }

    // Function to download resourcepack to generate hash
    private fun downloadResourcePacks() {
        // Ensure the URLs list is not empty
        if (RESOURCEPACKS_URLS.isEmpty()) { PLUGIN.logger.severe("Resource pack URL list is missing or empty in the configuration!"); return }

        RESOURCEPACKS_URLS.forEachIndexed { index, urlString ->
            val resourcepackFile = PLUGIN.dataFolder.resolve("downloads/resourcepack${index + 1}.zip")

            try {
                // Validate the URL
                val url = URL(urlString)

                // Download the resource pack
                url.openStream().use { inputStream ->
                    Files.createDirectories(resourcepackFile.parentFile.toPath()) // Ensure directory exists
                    Files.copy(inputStream, resourcepackFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                }
                PLUGIN.logger.info("Resource pack ${index + 1} downloaded successfully.")
            } catch (e: Exception) {
                PLUGIN.logger.severe("Error downloading resource pack ${index + 1} from $urlString: ${e.message}")
            }
        }
    }

    // Function that generates hash from pack
    private fun generateHashes(resourcepackFiles: List<File>) {
        resourcepackFiles.forEachIndexed { index, resourcepackFile ->
            // Check if the resource pack file exists
            if (!resourcepackFile.exists()) {
                PLUGIN.logger.severe("Resource pack file not found at: ${resourcepackFile.absolutePath}")
                return@forEachIndexed
            }

            // Clear hash list before creating new hashes
            CONFIG.set("resourcepacks-hashes", emptyList<String>()); PLUGIN.saveConfig()

            try {
                // Generate the SHA-1 hash
                val digest = MessageDigest.getInstance("SHA-1")
                val fileBytes = Files.readAllBytes(resourcepackFile.toPath())
                val hashBytes = digest.digest(fileBytes)
                val hashString = hashBytes.joinToString("") { "%02x".format(it) }

                // Add the hash to the configuration
                RESOURCEPACKS_HASHES = RESOURCEPACKS_HASHES.toMutableList().apply { add(hashString) }
                CONFIG.set("resourcepacks-hashes", RESOURCEPACKS_HASHES); PLUGIN.saveConfig()

                PLUGIN.logger.info("Generated resource pack hash for pack ${index + 1}: $hashString")
            } catch (e: Exception) {
                PLUGIN.logger.severe("Error generating resource pack hash for file ${resourcepackFile.name}: ${e.message}")
            }
        }
    }

    // Function that get a list of all downloaded resourcepack
    private fun getDownloadedResourcePacks(): List<File> {
        val resourcePackDirectory = PLUGIN.dataFolder.resolve("downloads")
        return List(RESOURCEPACKS_URLS.size) { index ->
            resourcePackDirectory.resolve("resourcepack${index + 1}.zip")
        }
    }

}
