package com.jjkay03.nationsevent.chat.group_chat_players

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.Bukkit
import java.io.File
import java.util.UUID

// Same as PlayerGroupChat data class but using UUID to be able to be saved
data class SerializablePlayerGroupChat(
    val id: Int,
    val ownerUUID: UUID,
    val playerUUIDs: List<UUID>,
    val name: String,
    val prefix: String
)

object PlayerGroupChatSave {

    // Function to serialize PlayerGroupChat
    fun PlayerGroupChat.toSerializable(): SerializablePlayerGroupChat {
        return SerializablePlayerGroupChat(
            id,
            owner.uniqueId,
            playerList.map { it.uniqueId },
            name,
            prefix
        )
    }

    // Function to un-serialize PlayerGroupChat
    fun SerializablePlayerGroupChat.toPlayerGroupChat(): PlayerGroupChat {
        return PlayerGroupChat(
            id,
            Bukkit.getOfflinePlayer(ownerUUID),
            playerList = playerUUIDs.map { Bukkit.getOfflinePlayer(it) }.toMutableList(),
            invites = mutableListOf(),
            spies = mutableListOf(),
            name = name,
            prefix = prefix
        )
    }

    // Function to save all group chats to json
    fun saveGCToFile(file: File, groupChatsList: List<PlayerGroupChat>) {
        NationsEvent.INSTANCE.logger.info("Saving ${groupChatsList.size} player group chats to file (${file.name})")
        val gson = GsonBuilder().setPrettyPrinting().create()
        val serializableList = groupChatsList.map { it.toSerializable() }
        val json = gson.toJson(serializableList)
        file.writeText(json)
    }

    // Function to load all group chats from json
    fun importGCFromFile(file: File): List<PlayerGroupChat> {
        if (!file.exists()) return emptyList()
        val gson = Gson()
        val reader = file.bufferedReader()
        val type = object : TypeToken<List<SerializablePlayerGroupChat>>() {}.type
        val serializedList: List<SerializablePlayerGroupChat> = gson.fromJson(reader, type) ?: emptyList()
        return serializedList.map { it.toPlayerGroupChat() }
    }

}