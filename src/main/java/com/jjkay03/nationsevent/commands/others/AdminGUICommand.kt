package com.jjkay03.nationsevent.commands.others

import com.jjkay03.nationsevent.gui.admin_gui.AdminGUI
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class AdminGUICommand: CommandExecutor {

    // COMMAND
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {

        // End command if sender is not a player
        if (sender !is Player) { sender.sendMessage("§cOnly players can run this command!"); return true }

        val adminGUIInstance = AdminGUI()
        adminGUIInstance.updateGUI(sender)
        adminGUIInstance.adminGUI.open(sender)

        return true
    }

}