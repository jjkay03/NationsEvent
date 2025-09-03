package com.jjkay03.nationsevent.features

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Villager
import org.bukkit.scheduler.BukkitRunnable

class Feature_RestockVillagers : CommandExecutor {
    private val config = NationsEvent.INSTANCE.config
    private val featureEnabled: Boolean = config.getBoolean("feature-auto-restock-villagers")

    init {
        if (featureEnabled) {
            // Restock villagers automatically if feature is enabled (8 minutes)
            object : BukkitRunnable() { override fun run() { restockVillagers() } }.runTaskTimer(NationsEvent.INSTANCE, 0L, 9600L)
        }
    }

    // Command
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        sender.sendMessage("§a🔄 Restocking trades on all villagers on the server")
        restockVillagers()
        return true
    }

    // Function that restocks trades of all villagers on the server
    private fun restockVillagers() {
        var villagerCount = 0
        Bukkit.getWorlds().forEach { world ->
            world.getEntitiesByClass(Villager::class.java).forEach { villager ->
                val recipes = villager.recipes
                for (recipe in recipes) {
                    // Restock the trade by resetting the uses and max uses
                    recipe.uses = 0
                    recipe.maxUses = recipe.maxUses
                }
                villager.recipes = recipes
                villagerCount++
            }
        }
        NationsEvent.INSTANCE.logger.info("Restocked trades for villagers ($villagerCount)")
    }
}