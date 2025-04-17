package com.jjkay03.nationsevent.group_chat_players

import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.utils.LogsManager
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class PlayerGroupChatCommand : CommandExecutor, TabCompleter {

    companion object {
        private val OPTIONS = listOf("create", "join")
        private val GROUP_OPTIONS = listOf("chat", "coords", "leave", "list")
        private val OWNER_OPTIONS = listOf("delete", "invite", "kick", "setowner")
    }

    /*   /gc coords
     *   /gc chat <message>
     *   /gc create
     *   /gc delete
     *   /gc invite <player>
     *   /gc join <player>
     *   /gc kick <player>
     *   /gc leave
     *   /gc list
     *   /gc setowner <player>
     */

    // COMMAND
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        // Checks
        if (args.isEmpty()) {sender.sendMessage("§cUsage: /$label <chat/create/delete/invite/join/leave/setowner>"); return true;}
        if (sender is ConsoleCommandSender) {sender.sendMessage("§eUse /admingroupchat instead. This command is for players only"); return true;}

        val player = sender as Player

        // Deal with arguments
        when (args[0].lowercase()) {

            // COORDS
            "coords" -> PlayerGroupChatUtils.sendInGroupChat(player, "${player.location.blockX} / ${player.location.blockY} / ${player.location.blockZ}")

            // CREATE
            "create" -> {
                val result = PlayerGroupChatUtils.createGroupChat(player)
                if (!result.first) { player.sendMessage("§cYou are already in a group chat! Leave it or delete it to make a new one"); return true }
                player.sendMessage("§aCreated group chat GC${result.second}")
                LogsManager.log(Saves.LOG_FILE_PLAYER_GC, "Player GC", "${player.name} CREATED group chat GC${result.second}")
            }

            // DELETE
            "delete" -> {
                val result = PlayerGroupChatUtils.deleteGroupChat(player)
                if (!result.first) { player.sendMessage("§cYou are not the owner of this group chat!") }
                player.sendMessage("§aDeleted group chat GC${result.second}")
                LogsManager.log(Saves.LOG_FILE_PLAYER_GC, "Player GC", "${player.name} DELETED group chat GC${result.second}")
            }

            // INVITE
            "invite" -> {
                if (args.size < 2) { player.sendMessage("§cUsage: /$label invite <player>"); return true }
                val invited = Bukkit.getPlayerExact(args[1])
                if (invited == null) { player.sendMessage("§cThis player is offline!"); return true }
                PlayerGroupChatUtils.inviteToGroupChat(player, invited)
                LogsManager.log(Saves.LOG_FILE_PLAYER_GC, "Player GC", "${player.name} INVITED ${invited.name} to GC${PlayerGroupChatUtils.getGroupChatID(player)}")
            }

            // JOIN
            "join" -> {
                if (args.size < 2) { player.sendMessage("§cUsage: /$label join <player>"); return true }

                val inviteSender = Bukkit.getPlayerExact(args[1])
                if (inviteSender == null) { player.sendMessage("§cThis player is offline!"); return true }

                val result = PlayerGroupChatUtils.joinGroupChat(player, inviteSender)
                if (!result.first) { player.sendMessage("§cYou have not received an invite to ${inviteSender.name}'s group chat ${PlayerGroupChatUtils.getGroupChatName(result.second)}!"); return true }

                player.sendMessage("§aYou have joined ${inviteSender.name}'s group chat ")
                LogsManager.log(Saves.LOG_FILE_PLAYER_GC, "Player GC", "${player.name} JOINED ${inviteSender.name}'s group GC${PlayerGroupChatUtils.getGroupChatID(inviteSender)}")
            }

            // KICK
            "kick" -> {
                if (args.size < 2) { player.sendMessage("§cUsage: /$label kick <player>"); return true }

                val toKick = Bukkit.getPlayerExact(args[1])
                if (toKick == null) { player.sendMessage("§cThis player is offline!"); return true }

                val result = PlayerGroupChatUtils.leaveGroupChat(toKick, player)

                if (!result.first) {
                    if (result.second != -1) { player.sendMessage("§cYou are not the owner of this group chat!") }
                    else { player.sendMessage("§cThis player is not in your group chat!") }
                    return true
                }

                toKick.sendMessage("§cYou have been kicked from group chat ${PlayerGroupChatUtils.getGroupChatName(result.second)}")
                LogsManager.log(Saves.LOG_FILE_PLAYER_GC, "Player GC", "${player.name} KICKED ${toKick.name} from GC${PlayerGroupChatUtils.getGroupChatID(player)}")
            }

            // LEAVE
            "leave" -> {
                val result = PlayerGroupChatUtils.leaveGroupChat(player)
                if (!result.first) { player.sendMessage("§cYou are not in a group chat!"); return true }
                player.sendMessage("§aYou have left group chat ${PlayerGroupChatUtils.getGroupChatName(result.second)}")
                LogsManager.log(Saves.LOG_FILE_PLAYER_GC, "Player GC", "${player.name} LEFT from GC${result.second}")
            }

            // LIST
            "list" -> {
                if (!PlayerGroupChatUtils.hasGroupChat(player)) { player.sendMessage("§cYou are not in a group chat!"); return true }

                val groupChatID = PlayerGroupChatUtils.getGroupChatID(player)

                player.sendMessage(Component
                    .text("§aList of §2§nGC$groupChatID§a members §8(hover)")
                    .hoverEvent(PlayerGroupChatUtils.createGroupChatMessageHover(groupChatID))
                )
            }

            // SETOWNER
            "setowner" -> {
                if (args.size < 2) { player.sendMessage("§cUsage: /$label setowner <player>"); return true }

                val newOwner = Bukkit.getPlayerExact(args[1])
                if (newOwner == null) { player.sendMessage("§cThis player is offline!"); return true }

                val result = PlayerGroupChatUtils.setOwner(player, newOwner)

                if (result.first) {
                    if (result.second != -1) {
                        player.sendMessage("§aSet ${newOwner.name} as the new group chat owner")
                        newOwner.sendMessage("§aYou are the new owner of your group chat")
                        LogsManager.log(Saves.LOG_FILE_PLAYER_GC, "Player GC", "${player.name} TRANSFERRED GC${result.second} to ${newOwner.name}")
                    }
                    else { player.sendMessage("§cYou are not the owner of this group chat!") }
                }
                else {
                    if (result.second != -1) { player.sendMessage("§cThis player is not in your group chat!") }
                    else { player.sendMessage("§cYou are not in a group chat!") }
                }
            }

            // CHAT and works the same if no args are provided
            else -> {
                val startIndex = if (args[0] == "chat") 1 else 0
                if (startIndex == 1 && args.size == 1) { player.sendMessage("§cUsage: /$label chat <message>");return true }

                val message = StringBuilder(args[startIndex])
                for (i in startIndex + 1..< args.size) {message.append(" ").append(args[i])}

                PlayerGroupChatUtils.sendInGroupChat(player, message.toString())
            }
        }

        return true
    }

    // TAB COMPLETER
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String> {
        if (sender !is Player) return listOf()
        return when (args.size) {
            1 -> OPTIONS.plus(if (PlayerGroupChatUtils.hasGroupChat(sender)) GROUP_OPTIONS else listOf())
                .plus(if (PlayerGroupChatUtils.isGroupChatOwner(sender)) OWNER_OPTIONS else listOf())
                .filter { it.startsWith(args[0], true) }.ifEmpty { listOf("<message>") }
            2 -> return when (args[0].lowercase()) {
                "invite" -> Bukkit.getOnlinePlayers().minus(sender).map { it.name }.filter { it.startsWith(args[1], true) }
                "join" -> PlayerGroupChatUtils.getInvites(sender).map { it.name!! }
                "kick", "setowner" -> PlayerGroupChatUtils.getPlayerList(sender).minus(sender).map { it.name!! }
                else -> listOf()
            }
            else -> listOf()
        }
    }
}