package emu.lunarcore.command.commands;

import emu.lunarcore.GameConstants;
import emu.lunarcore.command.Command;
import emu.lunarcore.command.CommandArgs;
import emu.lunarcore.command.CommandHandler;
import emu.lunarcore.game.player.Player;

@Command(label = "refill", aliases = {"rf"}, permission = "player.refill", desc = "/refill - refill your skill points in open world.")
public class RefillMPCommand implements CommandHandler {

    @Override
    public void execute(Player sender, CommandArgs args) {
        // Check target
        if (args.getTarget() == null) {
            this.sendMessage(sender, "Error: Targeted player not found or offline");
            return;
        }
        
        sender.getCurrentLineup().addMp(GameConstants.MAX_MP);
        this.sendMessage(sender, "Successfully refilled skill points for " + args.getTarget().getName());
    }

}
