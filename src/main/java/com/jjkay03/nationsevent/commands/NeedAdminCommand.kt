package com.jjkay03.nationsevent.commands

import com.jjkay03.nationsevent.NationsEvent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class NeedAdminCommand : CommandExecutor, TabCompleter {

    private val playersNeedingAdmin = mutableListOf<Player>()
    private val controlCommandPerm = NationsEvent.PERM_PROD

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) { sender.sendMessage("§cOnly players can use this command!"); return true }

        val player = sender as Player

        // Handle subcommands if arguments are provided
        if (args.isNotEmpty()) {
            if (!player.hasPermission(controlCommandPerm)) { player.sendMessage("§cYou don't have permission to use this!"); return true }

            when (args[0].lowercase()) {
                "remove" -> {
                    if (args.size != 2) { player.sendMessage("§cUsage: /needadmin remove <player>"); return true }
                    if (args[1] == "@a") { // Remove all players if @a is provided
                        if (playersNeedingAdmin.isNotEmpty()) { playersNeedingAdmin.clear(); player.sendMessage("§aRemoved all players from the admin assistance list") }
                        else { player.sendMessage("§cNo players are currently requesting admin assistance") }
                        return true
                    }
                    val targetPlayer = Bukkit.getPlayer(args[1])
                    if (targetPlayer == null) { player.sendMessage("§cPlayer not found!"); return true }
                    if (playersNeedingAdmin.remove(targetPlayer)) { player.sendMessage("§aRemoved ${targetPlayer.name} from the admin assistance list") }
                    else { player.sendMessage("§cPlayer ${targetPlayer.name} is not on the admin assistance list") }
                }

                "list" -> {
                    if (playersNeedingAdmin.isEmpty()) { player.sendMessage("§cNo players are requesting admin assistance"); return true }
                    player.sendMessage("") // Empty line
                    player.sendMessage("§c\uD83C\uDFF4 §l§nNEED ADMIN HELP") // Title
                    playersNeedingAdmin.forEach { needingPlayer ->
                        val tpMessage = TextComponent("§e[TP]")
                        tpMessage.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp ${needingPlayer.name}")
                        tpMessage.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, arrayOf(TextComponent("§eClick to teleport to ${needingPlayer.name}")))
                        val removeMessage = TextComponent("§c[REM]")
                        removeMessage.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/needadmin remove ${needingPlayer.name}")
                        removeMessage.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, arrayOf(TextComponent("§cClick to remove ${needingPlayer.name}")))
                        val playerMessage = TextComponent("§f- ${needingPlayer.name} ")
                        playerMessage.addExtra(tpMessage)
                        playerMessage.addExtra(TextComponent(" "))
                        playerMessage.addExtra(removeMessage)
                        player.spigot().sendMessage(playerMessage)
                    }
                    player.sendMessage("") // Ending empty line
                }

                else -> {
                    player.sendMessage("§cUnknown subcommand. Use /needadmin remove <player> or /needadmin list")
                }
            }

            return true
        }

        // If no arguments are provided, add the player to the admin assistance list
        if (playersNeedingAdmin.contains(player)) { player.sendMessage("§cYou have already requested admin assistance!"); return true }

        // Add player to the list
        playersNeedingAdmin.add(player)
        player.sendMessage("§a\uD83C\uDFF4 You have been added to the admin assistance list §c(DO NOT ABUSE OF THAT, ONLY USE IT WHEN YOU REALLY NEED IT)")

        // Notify all online admins with clickable message to teleport
        Bukkit.getOnlinePlayers().forEach { onlinePlayer ->
            if (onlinePlayer.hasPermission(controlCommandPerm)) {
                val message = TextComponent("§6\uD83C\uDFF4 §lNEED ADMIN§6: §e${player.name} §6needs admin assistance!")
                message.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, arrayOf(TextComponent("§eClick to teleport to ${player.name}")))
                message.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp ${player.name}")
                onlinePlayer.spigot().sendMessage(message)
            }
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String>? {
        if (sender !is Player) return emptyList() // Only players can use the command

        val player = sender as Player
        if (!player.hasPermission(controlCommandPerm)) return emptyList() // Only provide suggestions if they have the required permission

        return when (args.size) {
            1 -> listOf("remove", "list").filter { it.startsWith(args[0], ignoreCase = true) } // Suggest subcommands
            2 -> if (args[0].equals("remove", ignoreCase = true)) {
                val playerNames = playersNeedingAdmin.map { it.name }.toMutableList()
                playerNames.add("@a") // Add "@a" to the suggestions for removing all players
                playerNames.filter { it.startsWith(args[1], ignoreCase = true) } // Suggest player names
            } else {
                emptyList()
            }
            else -> emptyList()
        }
    }

}
