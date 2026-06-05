package com.jjkay03.nationsevent.specific.ne5.commands

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.Utils
import com.jjkay03.nationsevent.specific.EventSpecific
import com.jjkay03.nationsevent.integrations.luckperms.LuckPermsUtils
import com.jjkay03.nationsevent.utils.Scheduler
import net.luckperms.api.model.group.Group
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class QueueToIslandCommand(private val commandName: String) : CommandExecutor, TabCompleter {

    companion object {
        private const val CHECK_INTERVAL = 10L // Ticks between checks
        private var CURRENT_TASK: Any? = null
        private var TARGET_GROUP: Group? = null
        private var TARGET_WORLD: World? = null
        private var TOTAL_PLAYERS = 0
        private var TELEPORTED_COUNT = 0
        private var START_TIME = 0L
    }

    // INITIALIZATION (Register command)
    init {
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
        NationsEvent.INSTANCE.getCommand(commandName)?.tabCompleter = this
    }

    // COMMAND
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            sender.sendMessage("§cUsage: /queuetoisland <send|status> [group]")
            return true
        }

        when (args[0].lowercase()) {
            "send" -> handleSend(sender, args)
            "status" -> handleStatus(sender)
            else -> sender.sendMessage("§cUsage: /queuetoisland <send|status> [group]")
        }

        return true
    }

    // HANDLE SEND COMMAND
    private fun handleSend(sender: CommandSender, args: Array<out String>) {
        if (args.size < 2) {
            sender.sendMessage("§cUsage: /queuetoisland send <group>")
            return
        }

        // Stop current task if running
        Scheduler.cancelTask(CURRENT_TASK)

        // Get group and world based on argument
        val (group, world) = when (args[1].lowercase()) {
            "default" -> Saves.LP_GROUP_DEFAULT to EventSpecific.WORLD_NE5
            else -> {
                sender.sendMessage("§cInvalid group!")
                return
            }
        }

        // World null
        if (world == null) { sender.sendMessage("§cWorld not found!"); return }

        // Initialize tracking
        TARGET_GROUP = group
        TARGET_WORLD = world
        TELEPORTED_COUNT = 0
        START_TIME = System.currentTimeMillis()

        // Count total players in group
        TOTAL_PLAYERS = Bukkit.getOnlinePlayers().count { LuckPermsUtils.isPlayerInGroup(it, group) }
        sender.sendMessage("§aStarted queue teleport for group '${group?.name}' ($TOTAL_PLAYERS players)")

        // Start repeating task
        CURRENT_TASK = Scheduler.taskRepeating(Scheduler.SchedulerType.GLOBAL, CHECK_INTERVAL, CHECK_INTERVAL, {
            val playersToTeleport = Bukkit.getOnlinePlayers().filter { player ->
                LuckPermsUtils.isPlayerInGroup(player, group) && player.world != world
            }

            // Teleport players not in correct world
            playersToTeleport.forEach { player ->
                Utils.teleportPlayerToWorldSpawnRadius(player, world, 200)
                player.sendMessage("§7⛵ Teleporting to your island '${world.name}'")
                TELEPORTED_COUNT++
            }

            // Stop task if all players are in correct world
            if (playersToTeleport.isEmpty()) {
                Scheduler.cancelTask(CURRENT_TASK)
                CURRENT_TASK = null
            }
        })
    }

    // HANDLE STATUS COMMAND
    private fun handleStatus(sender: CommandSender) {
        if (CURRENT_TASK == null || TARGET_GROUP == null) {
            sender.sendMessage("§cNo queue teleport is currently running!")
            return
        }

        val elapsedTime = (System.currentTimeMillis() - START_TIME) / 1000
        val remainingPlayers = TOTAL_PLAYERS - TELEPORTED_COUNT;sender.sendMessage("§r")
        sender.sendMessage("§6Queue Teleport Status:")
        sender.sendMessage("§f- Target Group: §e${TARGET_GROUP?.name}")
        sender.sendMessage("§f- Target World: §e${TARGET_WORLD?.name}")
        sender.sendMessage("§f- Teleported: §a$TELEPORTED_COUNT§f/§e$TOTAL_PLAYERS")
        sender.sendMessage("§f- Remaining: §e$remainingPlayers")
        sender.sendMessage("§f- Running Time: §e${elapsedTime}s")
        sender.sendMessage("§r")
    }

    // TAB COMPLETER
    override fun onTabComplete(sender: CommandSender, cmd: Command, alias: String, args: Array<out String>): List<String>? {
        if (args.size == 1) {
            val completions = listOf("send", "status")
            return completions.filter { it.startsWith(args[0], ignoreCase = true) }
        }
        if (args.size == 2 && args[0].equals("send", ignoreCase = true)) {
            val completions = listOf("ns8-hot", "ns8-cold")
            return completions.filter { it.startsWith(args[1], ignoreCase = true) }
        }
        return null
    }

}
