package emu.lunarcore.command.commands;

import emu.lunarcore.command.Command;
import emu.lunarcore.command.CommandArgs;
import emu.lunarcore.command.CommandHandler;
import emu.lunarcore.util.Utils;

@Command(label = "worldlevel", aliases = {"wl"}, permission = "player.worldlevel", requireTarget = true, desc = "/worldlevel [world level]. Sets the targeted player's equilibrium level.")
public class WorldLevelCommand implements CommandHandler {

    @Override
    public void execute(CommandArgs args) {
        // Set world level
        int level = Utils.parseSafeInt(args.get(0));
        level = Math.min(Math.max(level, 0), 6);
        
        args.getTarget().setWorldLevel(level);
        
        // Done
        args.sendMessage("Set world level to " + level);
    }

}
