package emu.lunarcore.command.commands;

import emu.lunarcore.command.Command;
import emu.lunarcore.command.CommandArgs;
import emu.lunarcore.command.CommandHandler;
import emu.lunarcore.game.player.Player;

@Command(
    label = "kick",
    desc = "/kick @[player id]. Kicks a player from the server.",
    permission = "player.kick"
)
public final class KickCommand implements CommandHandler {

    @Override
    public void execute(Player sender, CommandArgs args) {
        // Check target
        if (args.getTarget() == null) {
            this.sendMessage(sender, "Error: Targeted player not found or offline");
            return;
        }

        // Kick player
        args.getTarget().getSession().close();

        // Send message
        this.sendMessage(sender, "Player kicked successfully");
    }
}
