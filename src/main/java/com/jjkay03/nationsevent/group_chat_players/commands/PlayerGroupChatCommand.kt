package com.jjkay03.nationsevent.group_chat_players.commands

import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChat
import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatManager.Companion.NAME_CHARACTER_LIMIT
import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatManager.Companion.PREFIX
import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatUtils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.Bukkit
import org.bukkit.command.*
import org.bukkit.entity.Player
import kotlin.text.startsWith

class PlayerGroupChatCommand : CommandExecutor, TabCompleter {

    companion object {
        private val OPTIONS = listOf("create", "join")
        private val GROUP_OPTIONS = listOf("chat", "coords", "leave", "list")
        private val OWNER_OPTIONS = listOf("delete", "invite", "kick", "setowner")
    }

    /*

    ❌ /gc coords <gc (optional, if not provided use selected one)>
    ✅ /gc chat <gc> <message>
    ✅ /gc create <name (optional)>
    ✅ /gc delete <gc>
    ✅ /gc invite <gc> <all player>
    ✅ /gc join <gc>
    ✅ /gc kick <gc> <group player>
    ❌ /gc leave <gc>
    ❌ /gc list -> (list all gc you in, hoverable list, maybe also show what gc is selected)
    ❌ /gc select <gc>
    ❌ /gc setowner <gc> <group player>

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
                val groupChat = if (args.size < 2 || args[1].isEmpty()) {
                    // No group provided, use selected group
                    val selected = PlayerGroupChatUtils.getSelectedPlayerGC(player)
                    if (selected == null) { player.sendMessage("§cYou need to have a selected group: /$label select <ID>, or provide one: /$label coords <ID>"); return true }
                    selected
                } else {
                    // Group ID provided
                    getAndValidateGC(args[1], player) ?: return true
                }

                // Send player coords in group
                PlayerGroupChatUtils.chatCoords(groupChat, player)
            }

            // CHAT
            "chat", "c" -> {
                // Check - validate arguments
                if (args.size < 3) { player.sendMessage("§cUsage: /$label chat <ID> <message>"); return true }

                // Check - get and validate group chat
                val groupChat = getAndValidateGC(args[1], player) ?: return true

                // Send the message
                val message = args.drop(2).joinToString(" ")
                PlayerGroupChatUtils.chat(groupChat, player, message)
            }

            // CREATE
            "create" -> {
                // Check - validate group chat name
                val name = args.getOrNull(1) ?: ""
                if (!PlayerGroupChatUtils.validateGCName(name)) {
                    player.sendMessage("§c${PREFIX}Invalid group chat name, use only letters and max $NAME_CHARACTER_LIMIT characters!")
                    return true
                }

                // Create group chat
                val groupChat = PlayerGroupChatUtils.createGC(player, listOf(), name)

                // Notify player
                if (groupChat == null) player.sendMessage("§c${PREFIX}You have reached the limit of group chats that you can be in!")
                else player.sendMessage(PlayerGroupChatUtils.formatHoverableMessage("§a${PREFIX}Created group chat %gc%", "§2", groupChat))
            }

            // DELETE
            "delete" -> {
                // Check - validate arguments and confirmation
                if (args.size < 3 || args[2] != "CONFIRM") { player.sendMessage("§cUsage: /$label delete <ID> CONFIRM"); return true }

                // Check - get and validate group chat
                val groupChat = getAndValidateGC(args[1], player) ?: return true

                // Check - if player is owner
                if (!isOwner(groupChat, player, "delete")) return true

                // Alert group chat members and delete
                PlayerGroupChatUtils.chat(groupChat, null, "${groupChat.owner.name} deleted group chat")
                PlayerGroupChatUtils.deleteGC(groupChat)
            }

            // INVITE
            "invite" -> {
                // Check - validate arguments
                if (args.size < 3) { player.sendMessage("§cUsage: /$label invite <ID> <player>"); return true }

                // Get target player
                val targetPlayer = Bukkit.getPlayer(args[2]) ?: return player.sendMessage("§cInvalid player!").let { true }

                // Check - get and validate group chat
                val groupChat = getAndValidateGC(args[1], player) ?: return true

                // Check - if player is owner
                if (!isOwner(groupChat, player, "invite")) return true

                // Check - if player is already invited or member of the group chat
                if (groupChat.playerList.contains(targetPlayer)) { player.sendMessage("§c${PREFIX}${targetPlayer.name} is already a member of ${groupChat.prefix}!"); return true }
                if (groupChat.invites.contains(targetPlayer)) { player.sendMessage("§c${PREFIX}${targetPlayer.name} has already been invited to ${groupChat.prefix}!"); return true }

                // Invite player
                groupChat.invites.add(targetPlayer)

                // Notify players (sender and invited player with clickable invite message)
                player.sendMessage(PlayerGroupChatUtils.formatHoverableMessage("§a${PREFIX}You invited ${targetPlayer.name} to %gc%", "§a", groupChat))
                targetPlayer.sendMessage(PlayerGroupChatUtils.formatHoverableMessage("§e${PREFIX}You have invited by ${player.name} to %gc%", "§e", groupChat))
                targetPlayer.sendMessage(
                    Component.text("§7➥ use §e/gc join ${groupChat.id} §7to join §a[ACCEPT]")
                        .clickEvent(ClickEvent.runCommand("/gc join ${groupChat.id}"))
                        .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("§eClick to join ${groupChat.prefix}")))
                )
            }

            // JOIN
            "join" -> {
                // Check - validate arguments
                if (args.size < 2) { player.sendMessage("§cUsage: /$label join <ID>"); return true }

                // Check - get and validate group chat
                val groupChat = getAndValidateGC(args[1], player, false) ?: return true

                // Check - if player has invite to group chat or already member of gc
                if (groupChat.playerList.contains(player)) { player.sendMessage("§c${PREFIX}You are already a member of ${groupChat.prefix}!") }
                if (!groupChat.invites.contains(player)) { player.sendMessage("§c${PREFIX}You have not been invited to ${groupChat.prefix}!") }

                // Accept invite
                PlayerGroupChatUtils.addPlayerToGC(groupChat, listOf(player))

                // Check if player is in group chat (could have reached the limit)
                if (!groupChat.playerList.contains(player)) { player.sendMessage("§c${PREFIX}Unable to join ${groupChat.prefix} you might of reached the group chat limit!"); return true }

                // Alert player
                player.sendMessage(PlayerGroupChatUtils.formatHoverableMessage("§a${PREFIX}You joined %gc%", "§a", groupChat))

                // Send join message in group chat
                PlayerGroupChatUtils.chat(groupChat, null, "${player.name} joined group")
            }

            // KICK
            "kick" -> {
                // Check - validate arguments
                if (args.size < 3) { player.sendMessage("§cUsage: /$label kick <ID> <player>"); return true }

                // Get target player
                val targetPlayer = Bukkit.getPlayer(args[2]) ?: return player.sendMessage("§cInvalid player!").let { true }

                // Check - get and validate group chat
                val groupChat = getAndValidateGC(args[1], player) ?: return true

                // Check - if player is owner
                if (!isOwner(groupChat, player, "kick")) return true

                // Check - if player is a member of the group
                if (!groupChat.playerList.contains(targetPlayer)) { player.sendMessage("§c${PREFIX}${targetPlayer.name} is not a member of ${groupChat.prefix}!"); return true }

                // Remove player
                PlayerGroupChatUtils.removePlayerFromGC(groupChat, listOf(targetPlayer))

                // Notify players
                player.sendMessage(PlayerGroupChatUtils.formatHoverableMessage("§a${PREFIX}You kicked ${targetPlayer.name} from %gc%", "§a", groupChat))
                targetPlayer.sendMessage(PlayerGroupChatUtils.formatHoverableMessage("§c${PREFIX}You were kicked from %gc%", "§c", groupChat))

                // Send kick message in group
                PlayerGroupChatUtils.chat(groupChat, null, "${targetPlayer.name} was kicked from group")
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

            // INVALID ARG - Send message in selected group chat
            else -> {
                // Get selected group chat
                val groupChat = PlayerGroupChatUtils.getSelectedPlayerGC(player)
                if (groupChat == null) { player.sendMessage("§cYou need to have a select group chat to chat in using: /$label select <ID>"); return true}

                // Send the message
                val message = args.drop(0).joinToString(" ")
                PlayerGroupChatUtils.chat(groupChat, player, message)
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
                    "coords", "chat", "c", "leave", "select" ->
                        PlayerGroupChatUtils.tabCompletePlayerGCsList(joinedGroups)
                            .filter { it.lowercase().startsWith(currentInput) }

                    // Show owned groups
                    "delete", "kick", "setowner", "invite" ->
                        PlayerGroupChatUtils.tabCompletePlayerGCsList(ownedGroups)
                            .filter { it.lowercase().startsWith(currentInput) }

                    // Show invited to groups
                    "join" ->
                        PlayerGroupChatUtils.tabCompletePlayerGCsList(PlayerGroupChatUtils.getPlayerInvitedToGCs(player))
                            .filter { it.lowercase().startsWith(currentInput) }

                    // Show argument
                    "create" -> listOf("<name (optional)>")

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

                    // List all online players
                    "invite" -> Bukkit.getOnlinePlayers().map { it.name }.filter { it.lowercase().startsWith(currentInput) }

                    else -> emptyList()
                }
            }

            else -> emptyList()
        }

    }

    // Helper function use to get, validate and make sure 'player' is part of 'groupChat' (used in multiple sub commands)
    private fun getAndValidateGC(groupChatArgument: String, player: Player, playerIsMember: Boolean = true): PlayerGroupChat? {
        val groupChat = PlayerGroupChatUtils.tabCompleteInputGCGet(groupChatArgument)
        if (groupChat == null) { player.sendMessage("§c${PREFIX}Invalid group chat ID!"); return groupChat }
        if (playerIsMember && !PlayerGroupChatUtils.isPlayerInGC(player, groupChat)) { player.sendMessage("§c${PREFIX}You are not a member of this group chat!"); return groupChat }
        else return groupChat
    }

    // Helper function to check if 'player' is owner of 'groupChat'
    private fun isOwner(groupChat: PlayerGroupChat, player: Player, action: String): Boolean {
        if (groupChat.owner == player) return true
        player.sendMessage(PlayerGroupChatUtils.formatHoverableMessage(
            "§c${PREFIX}Only ${groupChat.owner.name} (group chat owner) can perform $action action in %gc%", "§c", groupChat)
        )
        return false
    }
}