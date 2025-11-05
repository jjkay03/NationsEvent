package com.jjkay03.nationsevent.commands.utility

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.commands.management.FreezeAllCommand
import com.jjkay03.nationsevent.commands.management.PVPToggleCommand
import com.jjkay03.nationsevent.commands.management.SessionTimeCommand
import com.jjkay03.nationsevent.specific.ns7.AssignRandomTeam
import com.jjkay03.nationsevent.utils.LuckPermsUtils
import com.jjkay03.nationsevent.worlds.WorldsBridge
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class ListStatesCommand(private val commandName: String) : CommandExecutor {

    // INITIALIZATION
    init {
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
    }

    // COMMAND EXECUTION
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val announceSession = Saves.SESSION_STARTED
        val sessionTime = SessionTimeCommand.getTimeElapsed()
        val pvp = PVPToggleCommand.PVP
        val freezeAll = FreezeAllCommand.ENABLED
        val voicechat = LuckPermsUtils.groupHasPermission(Saves.LP_GROUP_DEFAULT, Saves.PERM_SIMPLE_VOICECHAT_SPEAK)
        val globalChat = LuckPermsUtils.groupHasPermission(Saves.LP_GROUP_DEFAULT, Saves.PERM_USE_CHAT)
        val worldBridge = WorldsBridge.ALLOW_CROSS     // NS7
        val assignRadomTeam = AssignRandomTeam.ENABLED // NS7

        // Header
        sender.sendMessage("§r")
        sender.sendMessage("§6Current States:")

        // List all states
        sender.sendMessage("§6\uD83D\uDCE2 §f- Announce Session: ${state(announceSession)}")
        sender.sendMessage("§6⌚ §f- Session Time: §a$sessionTime")
        sender.sendMessage("§6\uD83D\uDDE1 §f- PVP: ${state(pvp)}")
        sender.sendMessage("§6❄ §f- Freeze All: ${state(freezeAll)}")
        sender.sendMessage("§6🔊 §f- Voicechat: ${state(voicechat)}")
        sender.sendMessage("§6💬 §f- Global Chat: ${state(globalChat)}")
        sender.sendMessage("§6⛵ §f- Worlds Bridge §7(NS7)§f: ${state(worldBridge)}")
        sender.sendMessage("§6🎲 §f- Assign Random Team §7(NS7)§f: ${state(assignRadomTeam)}")

        sender.sendMessage("§r")

        return true
    }

    // Helper to format state
    private fun state(enabled: Boolean): String { return if (enabled) "§a✔ ON" else "§c❌ OFF" }

}
