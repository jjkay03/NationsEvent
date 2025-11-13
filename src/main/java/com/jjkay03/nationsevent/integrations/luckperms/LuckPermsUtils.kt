package com.jjkay03.nationsevent.integrations.luckperms

import net.luckperms.api.model.group.Group
import net.luckperms.api.node.Node
import net.luckperms.api.node.NodeEqualityPredicate
import net.luckperms.api.node.matcher.NodeMatcher
import net.luckperms.api.node.types.InheritanceNode
import org.bukkit.entity.Player
import java.util.UUID

object LuckPermsUtils {

    // -------------------------
    //  GROUP RELATED FUNCTIONS
    // -------------------------

    // Function that gets all groups (sorted by weight, highest first)
    fun getAllGroups(): List<Group> {
        return LuckPermsManager.LP_GROUP_MANAGER.loadedGroups
            .sortedByDescending { it.weight.orElse(0) }
            .toList()
    }

    // Function that gets a group by name (returns null if not found)
    fun getGroup(name: String): Group? {
        return LuckPermsManager.LP_GROUP_MANAGER.getGroup(name)
    }

    // Function that checks if a group has a permission
    fun groupHasPermission(group: Group?, permission: String): Boolean {
        if (group == null) return false // Return false if the group is null
        return group.nodes.any { it.key == permission && it.value }
    }

    // Function that gets all users in a group (returns list of UUIDs)
    fun groupGetAllUsers(group: Group?): List<UUID> {
        if (group == null) return emptyList()
        return LuckPermsManager.LP_USER_MANAGER.searchAll(NodeMatcher.key(InheritanceNode.builder(group.name).build()))
            .join()
            .map { it.key }
            .toList()
    }

    // Function that adds a permission to a group (returns true if perm is added)
    fun groupAddPermission(group: Group?, permission: String, value: Boolean = true): Boolean {
        if (group == null) return false // Return false if the group is null
        val node = Node.builder(permission).value(value).build()
        if (group.nodes.contains(node)) return false // Return false if already has node
        group.data().add(node)
        LuckPermsManager.LP_GROUP_MANAGER.saveGroup(group)
        return true
    }

    // Function that removes a permission from a group (returns true if perm was removed)
    fun groupRemovePermission(group: Group?, permission: String, value: Boolean = true): Boolean {
        if (group == null) return false // Return false if the group is null
        val node = Node.builder(permission).value(value).build()
        if (!group.nodes.contains(node)) return false // Return false if already has node
        group.data().remove(node)
        LuckPermsManager.LP_GROUP_MANAGER.saveGroup(group)
        return true
    }


    // --------------------------
    //  PLAYER RELATED FUNCTIONS
    // --------------------------

    // Function to get player primary group (the one with the height weight)
    fun playerGetPrimaryGroup(player: Player): Group? {
        val lpPlayer = LuckPermsManager.LP_USER_MANAGER.getUser(player.uniqueId) ?: return null
        val primaryGroupName = lpPlayer.primaryGroup
        return LuckPermsManager.LP_GROUP_MANAGER.getGroup(primaryGroupName)
    }

    // Function that give a player a permission
    fun playerAddPermission(player: Player, permission: String, value: Boolean = true) {
        val lpPlayer = LuckPermsManager.LP_USER_MANAGER.getUser(player.uniqueId) ?: return
        val node = Node.builder(permission).value(value).build()
        lpPlayer.data().add(node)
        LuckPermsManager.LP_USER_MANAGER.saveUser(lpPlayer)
    }

    // Function that remove a player permission
    fun playerRemovePermission(player: Player, permission: String) {
        val lpPlayer = LuckPermsManager.LP_USER_MANAGER.getUser(player.uniqueId) ?: return
        val node = Node.builder(permission).value(true).build()
        lpPlayer.data().remove(node)
        LuckPermsManager.LP_USER_MANAGER.saveUser(lpPlayer)
    }

    // Function to check if a player is in a specific group (returns true if player in group)
    fun isPlayerInGroup(player: Player, group: Group?): Boolean {
        val lpPlayer = LuckPermsManager.LP_USER_MANAGER.getUser(player.uniqueId) ?: return false
        if (group == null) return false
        val inheritanceNode = InheritanceNode.builder(group.name).build()
        return lpPlayer.data().contains(inheritanceNode, NodeEqualityPredicate.IGNORE_EXPIRY_TIME).asBoolean()
    }

    // Function that adds a player to a group
    fun playerSetGroup(player: Player, group: Group?) {
        val lpPlayer = LuckPermsManager.LP_USER_MANAGER.getUser(player.uniqueId) ?: return
        if (group == null) return
        if (isPlayerInGroup(player, group)) return

        // Add the group as a parent to the player and save
        val inheritanceNode = InheritanceNode.builder(group.name).build()
        lpPlayer.data().add(inheritanceNode)
        LuckPermsManager.LP_USER_MANAGER.saveUser(lpPlayer)
    }

    // Function that removes a player from a group
    fun playerRemoveGroup(player: Player, group: Group?) {
        val lpPlayer = LuckPermsManager.LP_USER_MANAGER.getUser(player.uniqueId) ?: return
        if (group == null) return
        if (!isPlayerInGroup(player, group)) return

        // Remove the group from the player and save
        val inheritanceNode = InheritanceNode.builder(group.name).build()
        lpPlayer.data().remove(inheritanceNode)
        LuckPermsManager.LP_USER_MANAGER.saveUser(lpPlayer)
    }

    // Function that return a player prefix
    fun playerPrefix(player: Player): String {
        val lpPlayer = LuckPermsManager.LP_USER_MANAGER.getUser(player.uniqueId)
        return lpPlayer?.cachedData?.metaData?.prefix ?: ""
    }

    // Function that return a player suffix
    fun playerSuffix(player: Player): String {
        val lpPlayer = LuckPermsManager.LP_USER_MANAGER.getUser(player.uniqueId)
        return lpPlayer?.cachedData?.metaData?.suffix ?: ""
    }

}