package com.jjkay03.nationsevent.group_chat_players.commands

import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatUtils
import com.jjkay03.nationsevent.utils.Classes
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class DebugGroupChatCommand : CommandExecutor, TabCompleter {

    companion object {
        private val CLAZZ = PlayerGroupChatUtils.javaClass
        private val METHODS = CLAZZ.declaredMethods.filter { !it.name.contains("$") }.toList()
        private val OPTIONS = METHODS.map { it.name }.plus("help")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (!OPTIONS.contains(args[0])) { sender.sendMessage("§cInvalid method chosen!"); return true }

        // Prints out the parameter types
        if (args[0] == "help") {
            sender.sendMessage(
                Component.text("§2Debug group chat variable syntax§7 (hover)").hoverEvent(HoverEvent.showText(
                Component.text("§a§l§nDebug variable syntax:\n§r")
                    .append(Component.text("§a\nBoolean:§f 0b or 1b"))
                    .append(Component.text("§a\nInt:§f <int>"))
                    .append(Component.text("§a\nString:§f \"<text>\""))
                    .append(Component.text("§a\nList<?>:§f [<e1>,<e2>,...]"))
                    .append(Component.text("§a\nGroupChat:§f GC-<id>"))
                    .append(Component.text("§a\nOfflinePlayer:§f OP-<player_name>"))
                ))
            )
            return true
        }

        // Gets the method
        val method = METHODS.find { it.name == args[0] }
        if (method == null) { sender.sendMessage("§cNo method named \"${args[0]}\" found!"); return true }

        // Checks if the player gave enough arguments for the method
        if (args.size-1 < method.parameters.size) { sender.sendMessage("§cNot enough parameters given!"); return true }

        // Get all the given parameters
        val params = args.toList().minus(args.first())
        for (i in 0..<params.size) {
            val argType = argToType(params[i])
            if (argType == null) { sender.sendMessage("§cArgument at index $i is not a registered type or is null! Use /dgc help"); return true }

            val argClass = argType.javaClass
            val targetClass = Classes.primitiveToWrapper(method.parameters[i].type)

            if (argClass != targetClass && !Classes.isInterfaceOf(argClass, targetClass)) {
                sender.sendMessage("§cInvalid argument type at index $i!\nargToType=$argClass | requiredClass=$targetClass")
                return true
            }
        }

        // Calls the desired method with the right amount of arguments
        val result = when (params.size) {
            0 -> method.invoke(PlayerGroupChatUtils)
            1 -> method.invoke(PlayerGroupChatUtils, argToType(params[0]))
            2 -> method.invoke(PlayerGroupChatUtils, argToType(params[0]), argToType(params[1]))
            3 -> method.invoke(PlayerGroupChatUtils, argToType(params[0]), argToType(params[1]), argToType(params[2]))
            4 -> method.invoke(PlayerGroupChatUtils, argToType(params[0]), argToType(params[1]), argToType(params[2]), argToType(params[3]))
            else -> null
        }

        // Sends the returned value to the commandSender
        sender.sendMessage(result.toString())
        return true
    }

    // TAB COMPLETE
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String> {
        return when (args.size) {
            1 -> OPTIONS.filter { it.startsWith(args[0]) }
            else -> {
                val method = METHODS.find { it.name == args[0] } ?: return listOf()
                if (args.size-1 > method.parameters.size) return listOf()

                val param = method.parameters[args.size-2]
                val type = param.type.toString().substringAfterLast('.')
                val name = param.name

                return listOf("<$name: $type>")
            }
        }
    }

    // Takes a string with the format at the top of the class and returns the corresponding object
    private fun argToType(arg: String): Any? {
        if (arg.isEmpty()) return null

        // Int type
        if (arg.toIntOrNull() != null) return arg.toInt()

        // String type
        if (arg.first() == '"' && arg.last() == '"') return arg.substring(1, arg.length)

        // Boolean type
        if (arg.length == 2 && (arg[0].code == 48 || arg[0].code == 49) && arg[1] == 'b') return arg[0].code == 49

        // List<?> type
        if (arg.first() == '[' && arg.last() == ']') {
            val args = arg.substring(1, arg.length-1).split(",")
            val listArgType = argToType(args.first())?.javaClass ?: return null

            return args.map {
                val result = argToType(it) ?: return null
                if (result.javaClass == listArgType) return@map result else return null
            }
        }

        val typeKey = arg.substring(0..2)
        val rest = arg.substring(3)

        // GroupChat type
        if (typeKey == "GC-" && argToType(rest) is Int) return PlayerGroupChatUtils.getGCfromID(rest.toInt())

        // OfflinePlayer type
        if (typeKey == "OP-") return Bukkit.getOfflinePlayerIfCached(rest)

        // Non-registered type
        return null
    }
}