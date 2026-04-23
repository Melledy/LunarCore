package emu.lunarcore.command.commands;

import emu.lunarcore.command.Command;
import emu.lunarcore.command.CommandArgs;
import emu.lunarcore.command.CommandHandler;
import emu.lunarcore.util.Position;
import emu.lunarcore.util.Utils;

@Command(label = "teleport", aliases = {"tp"}, permission = "player.teleport", requireTarget = true, desc = "/tp [x] [y] [z]. Teleports the player to the specified coordinates.")
public class TeleportCommand implements CommandHandler {

    @Override
    public void execute(CommandArgs args) {
        // Get coords
        int x = Utils.parseSafeInt(args.get(0));
        int y = Utils.parseSafeInt(args.get(1));
        int z = Utils.parseSafeInt(args.get(2));
        
        // Move player
        args.getTarget().moveTo(new Position(x, y, z));
    }

}
