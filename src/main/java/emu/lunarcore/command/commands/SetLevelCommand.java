package emu.lunarcore.command.commands;

import emu.lunarcore.util.Utils;
import emu.lunarcore.LunarCore;
import emu.lunarcore.GameConstants;
import emu.lunarcore.command.Command;
import emu.lunarcore.command.CommandArgs;
import emu.lunarcore.command.CommandHandler;
import emu.lunarcore.game.player.Player;

@Command(label = "setlevel", aliases = {"level"}, permission = "player.setlevel", desc = "/setlevel - Set your Equilibrium level.")
public class SetLevelCommand implements CommandHandler {

    @Override
    public void execute(Player sender, CommandArgs args) {
        int targetLevel = Utils.parseSafeInt(args.get(0));
        sender.setLevel(targetLevel);

        this.sendMessage(sender, "Set level to "+args.get(0));
    }

}
