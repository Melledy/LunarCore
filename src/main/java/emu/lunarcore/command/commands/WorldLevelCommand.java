package emu.lunarcore.command.commands;

import emu.lunarcore.command.Command;
import emu.lunarcore.command.CommandArgs;
import emu.lunarcore.command.CommandHandler;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.util.Utils;

@Command(label = "worldlevel", aliases = {"wl"})
public class WorldLevelCommand implements CommandHandler {

    @Override
    public void execute(Player sender, CommandArgs args) {
        // Check target
        if (args.getTarget() == null) {
            this.sendMessage(sender, "Error: Targeted player not found or offline");
            return;
        }
        
        // Set world level
        int level = Utils.parseSafeInt(args.get(0));
        level = Math.min(Math.max(level, 0), 6);
        
        args.getTarget().setWorldLevel(level);
        
        // Done
        this.sendMessage(sender, "Set world level to " + level);
    }

}
