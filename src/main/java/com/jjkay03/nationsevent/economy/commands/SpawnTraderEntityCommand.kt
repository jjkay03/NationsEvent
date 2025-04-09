package com.jjkay03.nationsevent.economy.commands

import com.jjkay03.nationsevent.economy.EconomyTraderEntity
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SpawnTraderEntityCommand : CommandExecutor {

    // COMMAND
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) { sender.sendMessage("§cOnly players can use this command!"); return true }
        EconomyTraderEntity.spawn(sender)
        sender.sendMessage("§aSpawned trader entity")
        return true
    }

}