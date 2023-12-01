package emu.lunarcore.command.commands;

import emu.lunarcore.util.Utils;
import emu.lunarcore.command.Command;
import emu.lunarcore.command.CommandArgs;
import emu.lunarcore.command.CommandHandler;
import emu.lunarcore.game.player.Player;

@Command(label = "setlevel", aliases = {"level"}, permission = "player.setlevel", requireTarget = true, desc = "/setlevel [level] - Sets the targeted player's trailblazer level.")
public class SetLevelCommand implements CommandHandler {

    @Override
    public void execute(Player sender, CommandArgs args) {
        int targetLevel = Utils.parseSafeInt(args.get(0));
        args.getTarget().setLevel(targetLevel);

        this.sendMessage(sender, "Set level to " + targetLevel);
    }

}
