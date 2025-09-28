package com.jjkay03.nationsevent.commands.player_assistance

import com.jjkay03.nationsevent.NationsEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class NeedStaffManageCommand(private val commandName: String) : CommandExecutor, TabCompleter {

    // INITIALIZATION (Register command)
    init {
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
        NationsEvent.INSTANCE.getCommand(commandName)?.tabCompleter = this
    }

    // COMMAND
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // End if not player
        if (sender !is Player) { sender.sendMessage("§cOnly players can use this command!"); return true }

        // Check if arguments provided
        if (args.isEmpty()) { sender.sendMessage("§cInvalid argument, usage: /$label <list|remove>"); return true }

        // Arguments
        when (args[0].lowercase()) {
            "list" -> {
                // Handle list command
                if (PlayerAssistance.NEED_STAFF_REQUESTS.isEmpty()) {
                    sender.sendMessage("§cNo players are requesting staff assistance")
                    return true
                }

                // Send title
                sender.sendMessage("") // Empty line
                sender.sendMessage("§e⚑ §l§nNEED STAFF ASSISTANCE") // Title

                // List all requests with clickable buttons
                PlayerAssistance.NEED_STAFF_REQUESTS.forEach { (needingPlayer, reason) ->
                    val tpButton = Component.text("[TP]")
                        .color(NamedTextColor.YELLOW)
                        .clickEvent(ClickEvent.runCommand("/tp ${needingPlayer.name}"))
                        .hoverEvent(HoverEvent.showText(Component.text("Click to teleport to ${needingPlayer.name}").color(NamedTextColor.YELLOW)))

                    val removeButton = Component.text("[REM]")
                        .color(NamedTextColor.RED)
                        .clickEvent(ClickEvent.runCommand("/needstaffmanage remove ${needingPlayer.name}"))
                        .hoverEvent(HoverEvent.showText(Component.text("Click to remove ${needingPlayer.name}").color(NamedTextColor.RED)))

                    val message = Component.text("- ${needingPlayer.name} ", NamedTextColor.WHITE)
                        .append(Component.text("(${reason.name})", NamedTextColor.GRAY))
                        .append(Component.text(" "))
                        .append(tpButton)
                        .append(Component.text(" "))
                        .append(removeButton)

                    sender.sendMessage(message)
                }
                sender.sendMessage("") // Ending empty line
            }

            "remove" -> {
                // Handle remove command - check arguments
                if (args.size != 2) { sender.sendMessage("§cInvalid argument, usage: /$label remove <player|@a>"); return true }

                // Remove all players if @a specified
                if (args[1] == "@a") {
                    if (PlayerAssistance.NEED_STAFF_REQUESTS.isEmpty()) {
                        sender.sendMessage("§cNo players are currently requesting staff assistance")
                        return true
                    }
                    PlayerAssistance.NEED_STAFF_REQUESTS.clear()
                    sender.sendMessage("§aRemoved all players from the staff assistance list")
                    return true
                }

                // Find target player
                val targetPlayer = Bukkit.getPlayer(args[1])
                if (targetPlayer == null) { sender.sendMessage("§cPlayer not found!"); return true }

                // Remove player from requests
                if (PlayerAssistance.NEED_STAFF_REQUESTS.remove(targetPlayer) != null) {
                    sender.sendMessage("§aRemoved ${targetPlayer.name} from the staff assistance list")
                } else {
                    sender.sendMessage("§cPlayer ${targetPlayer.name} is not on the staff assistance list")
                }
            }

            else -> {
                // Invalid subcommand
                sender.sendMessage("§cUnknown subcommand, use /$label <list|remove>")
            }
        }

        return true
    }

    // TAB COMPLETER
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String>? {
        return when (args.size) {
            1 -> listOf("list", "remove").filter { it.startsWith(args[0], ignoreCase = true) }
            2 -> {
                if (!args[0].equals("remove", ignoreCase = true)) return emptyList()

                val playerNames = PlayerAssistance.NEED_STAFF_REQUESTS.keys.map { it.name }.toMutableList()
                playerNames.add("@a")
                playerNames.filter { it.startsWith(args[1], ignoreCase = true) }
            }
            else -> emptyList()
        }
    }
}
