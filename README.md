# Lunar Rail
A WIP server emulator for version 1.4.0 of a certain turn based anime game.

# Running the server and client

### Prerequisites
* Java 17 JDK

### Recommended
* Mongodb (4.0+)

### Starting up the server
1. Compile the server with `./gradlew jar`
2. Create a folder named `resources` in your server directory, you will need to downlaod `TextMap` and `ExcelBin` folders which you can get from a repo like [https://github.com/Dimbreath/StarRailData](https://github.com/Dimbreath/StarRailData) into your resources folder.
3. Run the server with `java -jar LunarRail.jar`. Lunar Rail comes with a built in internal mongo server for its database, so no Mongodb installation is required. However, it is highly recomended to install Mongodb anyways.

### Connecting with the client
1. Login with the client to an official server at least once to download game data.
2. If you are using the provided keystore, you will need to install and have [Fiddler](https://www.telerik.com/fiddler) running. Make sure fiddler is set to decrypt https traffic.
3. Set your hosts file to redirect at least `hkrpg-sdk-os-static.hoyoverse.com` and `globaldp-prod-os01.starrails.com` to your http (dispatch) server ip.

### Server console commands

`/account create [username] {playerid}` - Creates an account with the specified username and the in-game uid for that account. The playerid parameter is optional and will be auto generated if not set.

### In-Game commands
There is a dummy user named "Server" in every player's friends list that you can message to use commands. Commands also work in other chat rooms, such as private/team chats.

`!spawn [monster id] [stage id]`

`!give [item id] [amount]`