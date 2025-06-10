package com.jjkay03.nationsevent.commands.others

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

// COMMAND USED TO RUN TESTS WHILE DEVELOPING PLUGIN

class NationsEventTestCommand: CommandExecutor, TabCompleter {

    // COMMAND
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {

        // Check if sender is player
        if (sender !is Player) { sender.sendMessage("§cOnly players can use this command!"); return true }

        when (args.getOrNull(0)?.uppercase()) {

            // Handle TEST 1 command
            "TEST_1" -> {
                sender.sendMessage("§aExecuting TEST 1")

                // TEST 1
                // ...
            }

            // Handle TEST 2 command
            "TEST_2" -> {
                sender.sendMessage("§aExecuting TEST 2")

                // TEST 2
                // ...
            }

            // Handle TEST 3 command
            "TEST_3" -> {
                sender.sendMessage("§aExecuting TEST 3")

                // TEST 3
                // ...
            }

            // Handle invalid arg
            else -> { sender.sendMessage("Usage: /$label <TEST_1|TEST_2|TEST_3>") }
        }

        return true
    }

    // TAB COMPLETER
    override fun onTabComplete(sender: CommandSender, cmd: Command, alias: String, args: Array<out String>): List<String>? {
        return when (args.size) {
            1 -> {
                val options = listOf("TEST_1", "TEST_2", "TEST_3")
                options.filter { it.startsWith(args[0].uppercase()) }
            }
            else -> emptyList()
        }
    }
}