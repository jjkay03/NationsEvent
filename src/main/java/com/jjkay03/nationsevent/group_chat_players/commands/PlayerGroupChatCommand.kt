package com.jjkay03.nationsevent.group_chat_players.commands

import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatManager.Companion.NAME_CHARACTER_LIMIT
import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatUtils
import org.bukkit.command.*
import org.bukkit.entity.Player

class PlayerGroupChatCommand : CommandExecutor, TabCompleter {

    companion object {
        private val OPTIONS = listOf("create", "join")
        private val GROUP_OPTIONS = listOf("chat", "coords", "leave", "list")
        private val OWNER_OPTIONS = listOf("delete", "invite", "kick", "setowner")
    }

    /*

    ❌ /gc coords <gc (optional, if not provided use selected one)>
    ❌ /gc chat <gc> <message>
    ✅ /gc create <name (optional)>
    ❌ /gc delete <gc>
    ❌ /gc join <gc>
    ❌ /gc kick <gc> <player>
    ❌ /gc leave <gc>
    ❌ /gc list -> (list all gc you in, hoverable list, maybe also show what gc is selected)
    ❌ /gc select <gc>
    ❌ /gc setowner <gc> <player>

     */

    // COMMAND
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // Checks
        if (sender is ConsoleCommandSender) {sender.sendMessage("§eUse /admingroupchat instead, this command is for players only!"); return true;}
        if (args.isEmpty()) {sender.sendMessage("§cUsage: /$label <coords/chat/create/delete/invite/join/leave/select/setowner>"); return true;}

        val player = sender as Player

        // Deal with arguments
        when (args[0].lowercase()) {

            // COORDS
            "coords" -> {
                TODO("Not yet implemented")
            }

            // CHAT
            "chat" -> {
                TODO("Not yet implemented")
            }

            // CREATE
            "create" -> {
                // Validate group chat name
                val name = args.getOrNull(1) ?: ""
                if (!PlayerGroupChatUtils.validateGroupChatName(name)) {
                    player.sendMessage("§cInvalid group chat name, use only letters and max $NAME_CHARACTER_LIMIT characters!")
                    return true
                }

                // Create group chat
                val groupChat = PlayerGroupChatUtils.createGC(player, listOf(), name)

                // Notify player
                if (groupChat == null) player.sendMessage("§cYou have reached the limit of group chats that you can be in!")
                else player.sendMessage(PlayerGroupChatUtils.formatHoverableMessage("§aCreated group chat %gc%", "§2", groupChat))
            }

            // DELETE
            "delete" -> {
                TODO("Not yet implemented")
            }

            // JOIN
            "join" -> {
                TODO("Not yet implemented")
            }

            // KICK
            "kick" -> {
                TODO("Not yet implemented")
            }

            // LEAVE
            "leave" -> {
                TODO("Not yet implemented")
            }

            // LIST
            "list" -> {
                TODO("Not yet implemented")
            }

            // SELECT
            "select" -> {
                TODO("Not yet implemented")
            }

            // SETOWNER
            "setowner" -> {
                TODO("Not yet implemented")
            }

        }

        return true
    }

    // TAB COMPLETER
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String> {
        if (sender !is Player) return emptyList()

        val player = sender
        val joinedGroups = PlayerGroupChatUtils.getPlayerGCs(player)
        val ownedGroups = PlayerGroupChatUtils.getPlayerGCs(player, true)

        return when (args.size) {
            // First argument - command list
            1 -> {
                val availableOptions = mutableListOf<String>().apply {
                    addAll(OPTIONS)
                    if (joinedGroups.isNotEmpty()) addAll(GROUP_OPTIONS)
                    if (ownedGroups.isNotEmpty()) addAll(OWNER_OPTIONS)
                }
                availableOptions.filter { it.startsWith(args[0], true) }
            }

            // Second argument - context-specific completions
            2 -> {
                val subCommand = args[0].lowercase()
                val currentInput = args[1].lowercase()

                when (subCommand) {
                    // Show joined groups
                    "coords", "chat", "leave", "select" ->
                        PlayerGroupChatUtils.tabCompletePlayerGCsList(joinedGroups)
                            .filter { it.lowercase().startsWith(currentInput) }

                    // Show owned groups
                    "delete", "kick", "setowner" ->
                        PlayerGroupChatUtils.tabCompletePlayerGCsList(ownedGroups)
                            .filter { it.lowercase().startsWith(currentInput) }

                    // Show invited to groups
                    "join" ->
                        PlayerGroupChatUtils.tabCompletePlayerGCsList(PlayerGroupChatUtils.getPlayerInvitedToGCs(player))
                            .filter { it.lowercase().startsWith(currentInput) }

                    else -> emptyList()
                }
            }

            // Third argument for player-specific commands
            3 -> {
                val currentInput = args[2].lowercase()

                when (args[0].lowercase()) {

                    // List players in group chat
                    "kick", "setowner" -> {
                        PlayerGroupChatUtils.tabCompleteInputGCGet(args[1])?.playerList?.map { it.name }
                            ?.filter { playerName -> playerName?.lowercase()?.startsWith(currentInput) ?: false }
                            ?.filterNotNull()
                            ?: emptyList()
                    }

                    else -> emptyList()
                }
            }

            else -> emptyList()
        }

    }
}