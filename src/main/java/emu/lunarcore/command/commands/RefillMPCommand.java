package emu.lunarcore.command.commands;

import emu.lunarcore.GameConstants;
import emu.lunarcore.command.Command;
import emu.lunarcore.command.CommandArgs;
import emu.lunarcore.command.CommandHandler;
import emu.lunarcore.game.player.Player;

@Command(label = "refill", aliases = {"rf"}, permission = "player.refill", requireTarget = true, desc = "/refill - refill your skill points in open world.")
public class RefillMPCommand implements CommandHandler {

    @Override
    public void execute(Player sender, CommandArgs args) {
        sender.getCurrentLineup().addMp(GameConstants.MAX_MP);
        this.sendMessage(sender, "Successfully refilled skill points for " + args.getTarget().getName());
    }

}
