package emu.lunarcore.command.commands;

import emu.lunarcore.command.Command;
import emu.lunarcore.command.CommandArgs;
import emu.lunarcore.command.CommandHandler;

@Command(
    label = "kick",
    desc = "/kick @[player id]. Kicks a player from the server.",
    requireTarget = true,
    permission = "player.kick"
)
public final class KickCommand implements CommandHandler {

    @Override
    public void execute(CommandArgs args) {
        // Kick player
        args.getTarget().getSession().close();

        // Send message
        args.sendMessage("Player kicked successfully");
    }
}
