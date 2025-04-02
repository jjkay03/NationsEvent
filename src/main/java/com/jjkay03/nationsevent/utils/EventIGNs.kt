package com.jjkay03.nationsevent.utils

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.Utils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.scheduler.BukkitTask
import java.io.File
import java.io.FileWriter
import java.util.concurrent.ConcurrentLinkedQueue

class EventIGNs : Listener {
    private val fileName = "${Saves.EVENT_CODENAME} IGNs.txt"
    private val playerQueue = ConcurrentLinkedQueue<String>()  // Concurrent queue for thread-safety
    private var task: BukkitTask? = null // To store the running task reference

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        addPlayerToQueue(event.player) // Add player to the queue on join
    }

    // Adds a player's name to the queue and starts processing if the task isn't running
    private fun addPlayerToQueue(player: Player) {
        val playerName = player.name
        playerQueue.add(playerName)
        // Start the task if it's not already running
        if (task == null || task?.isCancelled == true) { task = startQueueProcessing() }
    }

    // Starts a task that processes the queue asynchronously
    private fun startQueueProcessing(): BukkitTask {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(NationsEvent.INSTANCE, Runnable {
            while (playerQueue.isNotEmpty()) {
                val playerName = playerQueue.poll() ?: continue
                saveIGN(playerName)  // Process each player in the queue
            }
            // Cancel the task once the queue is empty
            if (playerQueue.isEmpty()) { task?.cancel(); task = null }
        }, 0L, 40L)
    }

    // Saves the player's IGN to a text file
    private fun saveIGN(playerName: String) {
        val file = File(Saves.DIR_EVENT_IGNS, fileName)

        // Check if the file exists; if not, create it with header and first IGN
        if (!file.exists()) {
            FileWriter(file).use { writer ->
                writer.write("IGNs of all players that joined the server during ${Saves.EVENT_CODENAME} :\n\n")
                writer.write(playerName)  // Add the first IGN
            }
            return
        }

        // If file exists, load current content to check for duplicates
        val content = file.readText()
        if (content.contains(playerName)) return // IGN already exists, do nothing

        // Append new IGN to the file
        FileWriter(file, true).use { writer -> writer.write(", $playerName") }
        Bukkit.getLogger().info("Added $playerName to ${Saves.EVENT_CODENAME} IGNs save file")
    }
}
