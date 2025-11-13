package com.jjkay03.nationsevent.integrations.voicechat

import de.maxhenkel.voicechat.api.Group

object SimpleVoiceChatUtils {

    // Function to create or get a group
    fun createOrGetGroup(
        groupName: String,
        persistent: Boolean = false,
        hidden: Boolean = false,
        ignoreCase: Boolean = false,
        type: Group.Type = Group.Type.NORMAL
    ): Group? {
        val api = SimpleVoiceChat.SVC_SERVER_API ?: return null

        // Try to find existing group
        val existingGroup = api.groups.find {
            if (ignoreCase) it.name.equals(groupName, ignoreCase = true)
            else it.name == groupName
        }

        if (existingGroup != null) return existingGroup

        // Create new group if not found
        return api.groupBuilder()
            .setName(groupName)
            .setPersistent(persistent)
            .setHidden(hidden)
            .setType(type)
            .build()
    }

}
