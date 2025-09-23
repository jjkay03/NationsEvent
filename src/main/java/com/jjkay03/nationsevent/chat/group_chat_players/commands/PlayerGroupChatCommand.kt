package com.jjkay03.nationsevent.chat.group_chat_players.commands

import com.jjkay03.nationsevent.chat.group_chat_players.PlayerGroupChat
import com.jjkay03.nationsevent.chat.group_chat_players.PlayerGroupChatLog
import com.jjkay03.nationsevent.chat.group_chat_players.PlayerGroupChatUtils
import com.jjkay03.nationsevent.chat.group_chat_players.PlayerGroupChatManager
import com.jjkay03.nationsevent.utils.Config
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.Bukkit
import org.bukkit.command.*
import org.bukkit.entity.Player
import org.bukkit.permissions.Permission
import kotlin.text.startsWith

class PlayerGroupChatCommand : CommandExecutor, TabCompleter {

    // SUBCOMMANDS - names and perms
    enum class SubCommand(val cmd: String) {
        COORDS("coords"),
        CHAT("chat"),
        CREATE("create"),
        DELETE("delete"),
        INVITE("invite"),
        JOIN("join"),
        KICK("kick"),
        LEAVE("leave"),
        LIST("list"),
        SELECT("select"),
        SETOWNER("setowner");
        val perm: Permission get() = Permission("nationsevent.command.groupchat.$cmd")
    }

    companion object {
        private val OPTIONS = listOf(SubCommand.CREATE, SubCommand.JOIN)
        private val GROUP_OPTIONS = listOf(SubCommand.CHAT, SubCommand.COORDS, SubCommand.LEAVE, SubCommand.LIST, SubCommand.SELECT)
        private val OWNER_OPTIONS = listOf(SubCommand.DELETE, SubCommand.INVITE, SubCommand.KICK, SubCommand.SETOWNER)
    }

    // COMMAND
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // Checks
        if (sender is ConsoleCommandSender) {sender.sendMessage("§eOnly players can use this command!"); return true;}
        if (args.isEmpty()) {sender.sendMessage("§cUsage: /$label <coords/chat/create/delete/invite/join/leave/select/setowner>"); return true;}

        val player = sender as Player

        // Deal with arguments
        when (args[0].lowercase()) {

            // COORDS
            SubCommand.COORDS.cmd -> {
                // Check - subcommand permission
                if (!checkSubCommandPerm(player, SubCommand.COORDS)) return true

                // Deal with argument or not
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
            SubCommand.CHAT.cmd, "c" -> {
                // Check - subcommand permission
                if (!checkSubCommandPerm(player, SubCommand.CHAT)) return true

                // Check - validate arguments
                if (args.size < 3) { player.sendMessage("§cUsage: /$label ${SubCommand.CHAT.cmd} <ID> <message>"); return true }

                // Check - get and validate group chat
                val groupChat = getAndValidateGC(args[1], player) ?: return true

                // Send the message
                val message = args.drop(2).joinToString(" ")
                PlayerGroupChatUtils.chat(groupChat, player, message)
            }

            // CREATE
            SubCommand.CREATE.cmd -> {
                // Check - subcommand permission
                if (!checkSubCommandPerm(player, SubCommand.CREATE)) return true

                // Check - validate group chat name
                val name = args.getOrNull(1) ?: ""
                if (!PlayerGroupChatUtils.validateGCName(name)) {
                    player.sendMessage("§c${PlayerGroupChatManager.PREFIX}Invalid group chat name, use only letters and max ${Config.PGC_NAME_CHARACTER_LIMIT} characters!")
                    return true
                }

                // Create group chat
                val groupChat = PlayerGroupChatUtils.createGC(player, listOf(), name)

                // Notify player
                if (groupChat == null) player.sendMessage("§c${PlayerGroupChatManager.PREFIX}You have reached the limit of group chats that you can be in!")
                else player.sendMessage(PlayerGroupChatUtils.formatHoverableMessage("§7${PlayerGroupChatManager.PREFIX}You §aCREATED §7group chat %gc%", "§2", groupChat))
            }

            // DELETE
            SubCommand.DELETE.cmd -> {
                // Check - subcommand permission
                if (!checkSubCommandPerm(player, SubCommand.DELETE)) return true

                // Check - validate arguments and confirmation
                if (args.size < 3 || args[2] != "CONFIRM") { player.sendMessage("§cUsage: /$label ${SubCommand.DELETE.cmd} <ID> CONFIRM"); return true }

                // Check - get and validate group chat
                val groupChat = getAndValidateGC(args[1], player) ?: return true

                // Check - if player is owner
                if (!isOwner(groupChat, player, "delete")) return true

                // Notify player
                player.sendMessage(PlayerGroupChatUtils.formatHoverableMessage("§7${PlayerGroupChatManager.PREFIX}You §cDELETED §7group chat %gc%", "§c", groupChat))

                // Alert group chat members and delete
                PlayerGroupChatUtils.chat(groupChat, null, "${groupChat.owner.name} deleted group chat")
                PlayerGroupChatUtils.deleteGC(groupChat)
            }

            // INVITE
            SubCommand.INVITE.cmd -> {
                // Check - subcommand permission
                if (!checkSubCommandPerm(player, SubCommand.INVITE)) return true

                // Check - validate arguments
                if (args.size < 3) { player.sendMessage("§cUsage: /$label invite <ID> <player>"); return true }

                // Get target player
                val targetPlayer = Bukkit.getPlayer(args[2]) ?: return player.sendMessage("§cInvalid player!").let { true }

                // Check - get and validate group chat
                val groupChat = getAndValidateGC(args[1], player) ?: return true

                // Check - if player is owner
                if (!isOwner(groupChat, player, "invite")) return true

                // Check - if player is already invited or member of the group chat
                if (groupChat.playerList.contains(targetPlayer)) { player.sendMessage("§c${PlayerGroupChatManager.PREFIX}${targetPlayer.name} is already a member of ${groupChat.prefix}!"); return true }
                if (groupChat.invites.contains(targetPlayer)) { player.sendMessage("§c${PlayerGroupChatManager.PREFIX}${targetPlayer.name} has already been invited to ${groupChat.prefix}!"); return true }

                // Invite player
                groupChat.invites.add(targetPlayer)

                // Notify players (sender and invited player with clickable invite message)
                player.sendMessage(PlayerGroupChatUtils.formatHoverableMessage("§7${PlayerGroupChatManager.PREFIX}You §eINVITED §7${targetPlayer.name} to %gc%", "§7", groupChat))
                targetPlayer.sendMessage(PlayerGroupChatUtils.formatHoverableMessage("§7${PlayerGroupChatManager.PREFIX}You have §eINVITED §7by ${player.name} to %gc%", "§7", groupChat))
                targetPlayer.sendMessage(
                    Component.text("§7➥ use §e/gc join ${groupChat.id} §7to join §a[ACCEPT]")
                        .clickEvent(ClickEvent.runCommand("/gc join ${groupChat.id}"))
                        .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("§eClick to join ${groupChat.prefix}")))
                )

                // Log
                PlayerGroupChatLog.invitePlayerToGC(groupChat, targetPlayer)
            }

            // JOIN
            SubCommand.JOIN.cmd -> {
                // Check - subcommand permission
                if (!checkSubCommandPerm(player, SubCommand.JOIN)) return true

                // Check - validate arguments
                if (args.size < 2) { player.sendMessage("§cUsage: /$label ${SubCommand.JOIN} <ID>"); return true }

                // Check - get and validate group chat
                val groupChat = getAndValidateGC(args[1], player, false) ?: return true

                // Check - if player has invite to group chat or already member of gc
                if (groupChat.playerList.contains(player)) { player.sendMessage("§c${PlayerGroupChatManager.PREFIX}You are already a member of ${groupChat.prefix}!"); return true }
                if (!groupChat.invites.contains(player)) { player.sendMessage("§c${PlayerGroupChatManager.PREFIX}You have not been invited to ${groupChat.prefix}!"); return true }

                // Accept invite
                PlayerGroupChatUtils.addPlayerToGC(groupChat, listOf(player))

                // Check if player is in group chat (could have reached the limit)
                if (!groupChat.playerList.contains(player)) { player.sendMessage("§c${PlayerGroupChatManager.PREFIX}Unable to join ${groupChat.prefix} you might of reached the group chat limit!"); return true }

                // Alert player
                player.sendMessage(PlayerGroupChatUtils.formatHoverableMessage("§7${PlayerGroupChatManager.PREFIX}You §aJOINED §7group chat %gc%", "§7", groupChat))

                // Send join message in group chat
                PlayerGroupChatUtils.chat(groupChat, null, "${player.name} joined group")
            }

            // KICK
            SubCommand.KICK.cmd -> {
                // Check - subcommand permission
                if (!checkSubCommandPerm(player, SubCommand.KICK)) return true

                // Check - validate arguments
                if (args.size < 3) { player.sendMessage("§cUsage: /$label ${SubCommand.KICK} <ID> <player>"); return true }

                // Get target player
                val targetPlayer = Bukkit.getPlayer(args[2]) ?: return player.sendMessage("§cInvalid player!").let { true }

                // Check - get and validate group chat
                val groupChat = getAndValidateGC(args[1], player) ?: return true

                // Check - if player is owner
                if (!isOwner(groupChat, player, "kick")) return true

                // Check - if player is a member of the group
                if (!groupChat.playerList.contains(targetPlayer)) { player.sendMessage("§c${PlayerGroupChatManager.PREFIX}${targetPlayer.name} is not a member of ${groupChat.prefix}!"); return true }

                // Remove player
                PlayerGroupChatUtils.removePlayerFromGC(groupChat, listOf(targetPlayer))

                // Notify players
                player.sendMessage(PlayerGroupChatUtils.formatHoverableMessage("§7${PlayerGroupChatManager.PREFIX}You §cKICKED §7${targetPlayer.name} from %gc%", "§7", groupChat))
                targetPlayer.sendMessage(PlayerGroupChatUtils.formatHoverableMessage("§7${PlayerGroupChatManager.PREFIX}You were §cKICKED §7from %gc%", "§7", groupChat))

                // Send kick message in group
                PlayerGroupChatUtils.chat(groupChat, null, "${targetPlayer.name} was kicked from group")
            }

            // LEAVE
            SubCommand.LEAVE.cmd -> {
                // Check - subcommand permission
                if (!checkSubCommandPerm(player, SubCommand.LEAVE)) return true

                // Check - validate arguments
                if (args.size < 2) { player.sendMessage("§cUsage: /$label ${SubCommand.LEAVE} <ID>"); return true }

                // Check - get and validate group chat
                val groupChat = getAndValidateGC(args[1], player) ?: return true

                // Remove player
                PlayerGroupChatUtils.removePlayerFromGC(groupChat, listOf(player))

                // Notify player
                player.sendMessage(PlayerGroupChatUtils.formatHoverableMessage("§7${PlayerGroupChatManager.PREFIX}You §cLEFT §7from group chat %gc%", "§7", groupChat))

                // Send leave message in group
                PlayerGroupChatUtils.chat(groupChat, null, "${player.name} left group")
            }

            // LIST
            SubCommand.LIST.cmd -> {
                // Check - subcommand permission
                if (!checkSubCommandPerm(player, SubCommand.LIST)) return true

                // Get all groups player is in
                val groupChats = PlayerGroupChatUtils.getPlayerGCs(player)

                // Check - if player is no groups
                if (groupChats.isEmpty()) { player.sendMessage("§c${PlayerGroupChatManager.PREFIX}You are not in any group chats!"); return true }

                // List groups
                player.sendMessage("")
                player.sendMessage("§e${PlayerGroupChatManager.PREFIX}Group chats you're in:")
                player.sendMessage ("§7(Hover GC for more info")
                for (group in groupChats) {
                    val line = if (group.owner == player) "  §f• %gc% §6👑" else "  §f• %gc%"
                    player.sendMessage(
                        PlayerGroupChatUtils.formatHoverableMessage(line, "§f", group,
                        underLined = false,
                        isWholeMessageHoverable = true
                    ))
                }
                player.sendMessage("")
            }

            // SELECT
            SubCommand.SELECT.cmd -> {
                // Check - subcommand permission
                if (!checkSubCommandPerm(player, SubCommand.SELECT)) return true

                // Check - validate arguments
                if (args.size < 2) { player.sendMessage("§cUsage: /$label <ID>"); return true }

                // Check - get and validate group chat
                val groupChat = getAndValidateGC(args[1], player) ?: return true

                // Select group chat
                PlayerGroupChatUtils.selectPlayerGC(groupChat, player)

                // Notify player
                player.sendMessage(PlayerGroupChatUtils.formatHoverableMessage("§7${PlayerGroupChatManager.PREFIX}Selected %gc%", "§7", groupChat))
            }

            // SETOWNER
            SubCommand.SETOWNER.cmd -> {
                // Check - subcommand permission
                if (!checkSubCommandPerm(player, SubCommand.SETOWNER)) return true

                // Check - validate arguments
                if (args.size < 3) { player.sendMessage("§cUsage: /$label invite <ID> <player>"); return true }

                // Get target player
                val targetPlayer = Bukkit.getPlayer(args[2]) ?: return player.sendMessage("§cInvalid player!").let { true }

                // Check - get and validate group chat
                val groupChat = getAndValidateGC(args[1], player) ?: return true

                // Check - if player is owner
                if (!isOwner(groupChat, player, "setowner")) return true

                // Check - if player is member of group chat or self
                if (!groupChat.playerList.contains(targetPlayer)) { player.sendMessage("§c${PlayerGroupChatManager.PREFIX}${targetPlayer.name} is not a member of ${groupChat.prefix}!"); return true }
                if (targetPlayer == player) { player.sendMessage("§c${PlayerGroupChatManager.PREFIX}You are already the owner of ${groupChat.prefix}!"); return true }

                // Set new owner
                PlayerGroupChatUtils.setGCOwner(groupChat, targetPlayer)

                // Notify players
                player.sendMessage(PlayerGroupChatUtils.formatHoverableMessage("§7${PlayerGroupChatManager.PREFIX}You §bTRANSFERRED §7group chat %gc% §7to ${targetPlayer.name}", "§7", groupChat))
                targetPlayer.sendMessage(PlayerGroupChatUtils.formatHoverableMessage("§7${PlayerGroupChatManager.PREFIX} ${player.name} §bTRANSFERRED §7group chat %gc% §7to you", "§7", groupChat))
            }

            // INVALID ARG - Send message in selected group chat
            else -> {
                // Check - subcommand permission (chat)
                if (!checkSubCommandPerm(player, SubCommand.CHAT)) return true

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
            // First argument - command list (show depending on perm and groups)
            1 -> {
                val availableOptions = mutableListOf<String>().apply {
                    addAll(OPTIONS.filter { sender.hasPermission(it.perm) }.map { it.cmd })
                    if (joinedGroups.isNotEmpty()) addAll(GROUP_OPTIONS.filter { sender.hasPermission(it.perm) }.map { it.cmd })
                    if (ownedGroups.isNotEmpty()) addAll(OWNER_OPTIONS.filter { sender.hasPermission(it.perm) }.map { it.cmd })
                }
                availableOptions.filter { it.startsWith(args[0], true) }
            }

            // Second argument - context-specific completions
            2 -> {
                val subCommand = args[0].lowercase()
                val currentInput = args[1].lowercase()

                when (subCommand) {
                    // Show joined groups
                    SubCommand.COORDS.cmd, SubCommand.CHAT.cmd, "c", SubCommand.LEAVE.cmd, SubCommand.SELECT.cmd ->
                        PlayerGroupChatUtils.tabCompletePlayerGCsList(joinedGroups)
                            .filter { it.lowercase().startsWith(currentInput) }

                    // Show owned groups
                    SubCommand.DELETE.cmd, SubCommand.KICK.cmd, SubCommand.SETOWNER.cmd, SubCommand.INVITE.cmd ->
                        PlayerGroupChatUtils.tabCompletePlayerGCsList(ownedGroups)
                            .filter { it.lowercase().startsWith(currentInput) }

                    // Show invited to groups
                    SubCommand.JOIN.cmd ->
                        PlayerGroupChatUtils.tabCompletePlayerGCsList(PlayerGroupChatUtils.getPlayerInvitedToGCs(player))
                            .filter { it.lowercase().startsWith(currentInput) }

                    // Show argument
                    SubCommand.CREATE.cmd -> listOf("<name (optional)>")

                    else -> emptyList()
                }
            }

            // Third argument for player-specific commands
            3 -> {
                val currentInput = args[2].lowercase()

                when (args[0].lowercase()) {

                    // List players in group chat
                    SubCommand.KICK.cmd, SubCommand.SETOWNER.cmd -> {
                        PlayerGroupChatUtils.tabCompleteInputGCGet(args[1])?.playerList?.map { it.name }
                            ?.filter { playerName -> playerName?.lowercase()?.startsWith(currentInput) ?: false }
                            ?.filterNotNull()
                            ?: emptyList()
                    }

                    // List all online players
                    SubCommand.INVITE.cmd -> Bukkit.getOnlinePlayers().map { it.name }.filter { it.lowercase().startsWith(currentInput) }

                    else -> emptyList()
                }
            }

            else -> emptyList()
        }

    }

    // Helper function to check if sender has permission to use subcommand
    private fun checkSubCommandPerm(sender: CommandSender, subCommand: SubCommand): Boolean {
        if (sender.hasPermission(subCommand.perm)) return true
        else {
            sender.sendMessage("§cYou do not have permission to use '${subCommand.cmd}' subcommand!")
            return false
        }
    }

    // Function use to get, validate and make sure 'player' is part of 'groupChat' (used in multiple sub commands)
    private fun getAndValidateGC(groupChatArgument: String, player: Player, playerIsMember: Boolean = true): PlayerGroupChat? {
        val groupChat = PlayerGroupChatUtils.tabCompleteInputGCGet(groupChatArgument)
        if (groupChat == null) { player.sendMessage("§c${PlayerGroupChatManager.PREFIX}Invalid group chat ID!"); return null }
        if (playerIsMember && !PlayerGroupChatUtils.isPlayerInGC(player, groupChat)) { player.sendMessage("§c${PlayerGroupChatManager.PREFIX}You are not a member of GC${groupChat.id}!"); return null }
        else return groupChat
    }

    // Helper function to check if 'player' is owner of 'groupChat'
    private fun isOwner(groupChat: PlayerGroupChat, player: Player, action: String): Boolean {
        if (groupChat.owner == player) return true
        player.sendMessage(
            PlayerGroupChatUtils.formatHoverableMessage(
            "§c${PlayerGroupChatManager.PREFIX}Only ${groupChat.owner.name} (group chat owner) can perform $action action in %gc%", "§c", groupChat)
        )
        return false
    }
}