package emu.lunarcore.command.commands;

import emu.lunarcore.LunarCore;
import emu.lunarcore.GameConstants;
import emu.lunarcore.command.Command;
import emu.lunarcore.command.CommandArgs;
import emu.lunarcore.command.CommandHandler;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.game.player.lineup.PlayerLineup;

@Command(label = "refill", aliases = {"rf"}, permission = "player.refill", desc = "/refill - refill your skill points in open world.")
public class RefillMPCommand implements CommandHandler {

    @Override
    public void execute(Player sender, CommandArgs args) {
        PlayerLineup lineup = sender.getCurrentLineup();
        lineup.addMp(GameConstants.MAX_MP);
        this.sendMessage(sender, "Successfully refilled skill points.");
    }

}
