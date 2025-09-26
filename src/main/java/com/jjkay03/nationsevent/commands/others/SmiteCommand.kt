package com.jjkay03.nationsevent.commands.others

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.utils.Scheduler
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class SmiteCommand(private val commandName: String) : CommandExecutor, TabCompleter {

    // INITIALIZATION (Register command)
    init {
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
        NationsEvent.INSTANCE.getCommand(commandName)?.tabCompleter = this
    }

    // COMMAND
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        // End if not player
        if (sender !is Player) { sender.sendMessage("§cOnly players can use this command!"); return true }

        // No arguments smite target block
        if (args.isEmpty()) {
            smiteTargetBlock(sender)
            return true
        }

        // Adjust/set lightning power
        val power = if (args.size > 1) args[1].toIntOrNull() ?: 5 else 5

        // Smite everyone
        if (args[0].equals("@a", ignoreCase = true)) {
            sender.sendMessage("§b⚡ Smiting EVERYONE")
            smiteAllPlayers(sender, power)
        }

        // Smite player
        else {
            val targetPlayer = Bukkit.getPlayer(args[0])
            if (targetPlayer != null) {
                sender.sendMessage("§b⚡ Smiting §r${targetPlayer.name}")
                smitePlayer(sender, targetPlayer, power)
            }
        }

        return true
    }

    // TAB COMPLETE
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String> {
        return when (args.size) {
            1 -> Bukkit.getOnlinePlayers().map { it.name } + "@a"
            2 -> listOf("5")
            else -> emptyList()
        }
    }

    // Helper function to smite the block the player is looking at
    private fun smiteTargetBlock(player: Player) {
        val targetBlock = player.getTargetBlock(null, 600)
        Scheduler.task(
            type = Scheduler.SchedulerType.ENTITY,
            entity = player,
            task = {
                player.world.strikeLightning(targetBlock.location)
            }
        )
    }

    // Helper function to smite all online players
    private fun smiteAllPlayers(sender: Player, power: Int) {
        Bukkit.getOnlinePlayers().forEach { targetPlayer ->
            Scheduler.task(
                type = Scheduler.SchedulerType.ENTITY,
                entity = targetPlayer,
                task = {
                    targetPlayer.world.strikeLightningEffect(targetPlayer.location)
                    if (!targetPlayer.isInvulnerable) {
                        targetPlayer.damage(power.toDouble())
                    }
                }
            )
        }
    }

    // Helper function to smite a specific player
    private fun smitePlayer(sender: Player, targetPlayer: Player, power: Int) {
        Scheduler.task(
            type = Scheduler.SchedulerType.ENTITY,
            entity = targetPlayer,
            task = {
                targetPlayer.world.strikeLightningEffect(targetPlayer.location)
                if (!targetPlayer.isInvulnerable) {
                    targetPlayer.damage(power.toDouble())
                }
            }
        )
    }
}
