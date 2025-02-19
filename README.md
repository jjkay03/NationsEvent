<div align="center">
  <img src="https://github.com/jjkay03/NationsEvent/assets/61110962/48a1349c-47a1-4087-aacc-b9e5b45f84d8" width="80"/>
  <img src="https://github.com/user-attachments/assets/5fa7a23b-fabf-4649-88b3-648c0813f51d" width="80"/>
</div>

# NATIONS EVENT PLUGIN
This plugin is designed for Nations Events, offering a comprehensive suite of features to streamline event management. It provides moderation tools, event host utilities, and a variety of essential commands. Additionally, depending on the plugin version or branch, it introduces unique, game-altering mechanics tailored to specific events.

In Nations Events most of the time death is permanent and all players only have one live, for this feature we use an other one of my plaugins [NationsDeathBan](https://github.com/jjkay03/NationsDeathBan) in combination with this one.

🔨 Before using/interacting with this project, please ensure you have read and agree to the terms outlined in the [TERMS.md](TERMS.md) file.

<br>

## COMMANDS
- `/joinvc <player>` : Propt the player to join VC on Discord to be interviewed.
- `/joinstage` : Prompt all players to join the stage channel on Discord.
- `/announcesession <number>` : Announce the start of the session and its number to everyone.
- `/sessiontime` : Shows for how long the session has been going.
- `/pvptoggle` : Enable/Disable PVP on the server.
- `/pvpalerts` : If toggled will alert staff when players are fighting.
- `/voicechatperms` : Enable/Disable simple voice chat permission on the server.
- `/bypassviewdistance` : Allows to bypass the default server max viewdistance.
- `/vote <player>` : Allow players to vote for another player.
- `/topvotes` : Display top player votes.
- `/clearvotes` : Clear all player votes.
- `/exportvotes` : Creates a .txt file with all the player votes.
- `/lockvotes` : Enable/Disable the ability to vote.
- `/playerscale <player> <float>` : Allows to change the scale of a player (scale attribute).
- `/playerscalerestall` : Reset the scale of all players online to default.
- `/needadmin [list/remove]` : Allows players to notify admins that they need assistance.
- `/freezeall` : Enable/Disable global freeze, not allowing players to move and interact.
- `/hidestaff` : Make all staff and spectators invisible for the player that runs it (useful for recording)
- `/fullmoon` : Sets the time in the wolf to night and a full moon (210000).
- `/permanentmessage <message>` : Displays a message to all players until cleared.
- `/admingui` : Open admin GUI - toggle on or off main plugin features.
- `/globalchat` : Enable/Disable the ability for players to use the chat.
- `/restockvillagers` : Restock trades of all villagers on the server.
- `/randomplayertp` : Teleports you to a random player.

_+ Season specific commands_

<br>

## FEATURES
_All features are toggleable in the plugin config._
- `Meet player death` : Make players drop meet on death.
- `Iron door` : Make it so player with right permission can open iron doors with hands. (Created for NG3)
- `Farm protection` : Make it so players and mobs can't trample crops. (Created for NG4)
- `Speedy Blocks` : Make players faster when they are standing on certain blocks. (Created for NG5)
- `Auto Restock Villagers` : Automatically restock villagers trades without day cycle. (Created for NE1)

<br>

## PATCHES
_All patches are toggleable in the plugin config._
- `Anti block glitching` : Prevent players from block glitching.
- `Anti ender pearl` : Prevent players from using ender pearls.

<br>

## UTILS
_Utility features of plugin._
- Server resourcepack with specific player bypass.
- Disabled specific items craft.
- Disable or limit enchantments.
- Allows admins to bypass max view distance.
- Save all IGNs of the players that participate in the event.
- Milk The Gator 👀

<br>

## GROUP CHATS
Custom group chats creation based on permissions.

The group chat are edited and created in the `GroupChats.kt` enum class:

```kotlin
// Exemple group chat (Staff chat)
STAFF_CHAT (
        "§4[SC] §c%player%: %message%",  // Formating
        "nationsevent.staffchat.send",  // Permission send
        "nationsevent.staffchat.view",  // Permission view
        setOf("staffchat", "sc")  // Commands
    )
```
![image](https://github.com/user-attachments/assets/21299d5a-3586-4197-a08a-427dba5cc04f)

- **Formatting:** Use `%player%` for were the player name show and `%message%` for were the message should show.
- **Permission Send:** This is the perm needed to send a message in that GC.
- **Permission View:** This is the perm needed to view messages in that GC. _(Send/Staff perm can also view)_
- **Commands:** This is a list of commands that can be used to message in the GC. _(Exemple: `/staffchat <message>`)_

<br>

## ADMIN GUI
Open using `/admingui` to toggle on or off main features of the plugin easily.

https://github.com/user-attachments/assets/e0b785fa-b1c3-444c-8634-699b73d88535

