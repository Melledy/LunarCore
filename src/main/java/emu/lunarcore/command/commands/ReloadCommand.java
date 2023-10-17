package emu.lunarcore.command.commands;

import emu.lunarcore.LunarRail;
import emu.lunarcore.command.Command;
import emu.lunarcore.command.CommandArgs;
import emu.lunarcore.command.CommandHandler;
import emu.lunarcore.game.player.Player;

@Command(label = "reload", permission = "admin.reload", desc = "/reload. Reloads the server config.")
public class ReloadCommand implements CommandHandler {

    @Override
    public void execute(Player sender, CommandArgs args) {
        LunarRail.loadConfig();
        this.sendMessage(sender, "Reloaded the server config");
    }

}
