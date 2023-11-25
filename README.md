# Lunar Core
A game server reimplementation for version 1.5.0 of a certain turn based anime game for educational purposes. For any extra support, questions, or discussions, check out our [discord](https://discord.gg/cfPKJ6N5hw).

### Notable features
- Basic game features: Logging in, team setup, inventory, basic scene/entity management
- Monster battles working
- Natural world monster/prop/npc spawns
- Most character techniques are handled
- Npc shops handled
- Gacha system
- Mail system
- Friend system (Assists are not working yet)
- Forgotten hall (with 1.4.0 features)
- Simulated universe (Runs can be finished, but many features are missing)

# Running the server and client

### Prerequisites
* [Java 17 JDK](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)

### Recommended
* [MongoDB 4.0+](https://www.mongodb.com/try/download/community)

### Starting up the server
1. Compile the server with `./gradlew jar`
2. Create a folder named `resources` in your server directory, you will need to download the `TextMap` and `ExcelBin` folders which you can get from a repo like [https://github.com/Dimbreath/StarRailData](https://github.com/Dimbreath/StarRailData) into your resources folder.
3. Run the server with `java -jar LunarCore.jar`. Lunar Core comes with a built in internal mongo server for its database, so no Mongodb installation is required. However, it is highly recomended to install Mongodb anyways.
4. Create an account if you haven't already using the `/account` command

### Connecting with the client (Fiddler)
1. Login with the client to an official server and hoyo account **at least once** to download game data.
2. Install and have [Fiddler Classic](https://www.telerik.com/fiddler) running.
3. Set fiddler to decrypt https traffic. (Tools -> Options -> HTTPS -> Decrypt HTTPS traffic) Make sure `ignore server certificate errors` is checked as well.
4. Copy and paste the following code into the fiddlerscript tab of fiddler classic:

```
import System;
import System.Windows.Forms;
import Fiddler;
import System.Text.RegularExpressions;

class Handlers
{
    static function OnBeforeRequest(oS: Session) {
        if (oS.host.EndsWith(".starrails.com") || oS.host.EndsWith(".hoyoverse.com") || oS.host.EndsWith(".mihoyo.com") || oS.host.EndsWith(".bhsr.com")) {
            oS.host = "localhost"; // This can also be replaced with another IP address.
        }
    }
};
```

### Server commands
Server commands can be run in the server console or in-game. There is a dummy user named "Server" in every player's friends list that you can message to use in-game commands.

```
/account {create | delete} [username] (reserved player uid). Creates or deletes an account.
/avatar lv(level) p(ascension) r(eidolon) s(skill levels). Sets the current avatar's properties
/clear {relics | lightcones | materials | items}. Removes filtered items from the player inventory.
/gender {male | female}. Sets the player gender.
/give [item id] x[amount]. Gives the targetted player an item.
/giveall {materials | avatars}. Gives the targeted player items.
/help. Displays a list of available commands.
/mail [content]. Sends the targeted player a system mail.
/permission {add | remove | clear} [permission]. Gives/removes a permission from the targeted player.
/reload. Reloads the server config.
/scene [scene id] [floor id]. Teleports the player to the specified scene.
/spawn [monster/prop id] x[amount] s[stage id]. Spawns a monster or prop near the targeted player.
/unstuck @[player id]. Unstucks an offline player if theyre in a scene that doesnt load.
/worldlevel [world level]. Sets the targeted player's equilibrium level.
```