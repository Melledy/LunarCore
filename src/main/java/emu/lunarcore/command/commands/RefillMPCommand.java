package emu.lunarcore.command.commands;

import emu.lunarcore.command.Command;
import emu.lunarcore.command.CommandArgs;
import emu.lunarcore.command.CommandHandler;

@Command(label = "refill", aliases = {"rf"}, permission = "player.refill", requireTarget = true, desc = "/refill - refill your skill points in open world.")
public class RefillMPCommand implements CommandHandler {

    @Override
    public void execute(CommandArgs args) {
        var lineup = args.getTarget().getCurrentLineup();
        lineup.addMp(lineup.getMaxMp());
        
        args.sendMessage("Successfully refilled skill points for " + args.getTarget().getName());
    }

}
