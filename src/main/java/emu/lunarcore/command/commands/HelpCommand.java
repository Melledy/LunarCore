package emu.lunarcore.command.commands;

import emu.lunarcore.LunarCore;
import emu.lunarcore.command.Command;
import emu.lunarcore.command.CommandArgs;
import emu.lunarcore.command.CommandHandler;
import emu.lunarcore.game.player.Player;

@Command(label = "help", permission = "player.help", desc = "/help. Displays a list of available commands.")
public class HelpCommand implements CommandHandler {

    @Override
    public void execute(Player sender, CommandArgs args) {
        StringBuilder help = new StringBuilder();
        this.sendMessage(sender, "Displaying list of commands:");
        
        var labels = LunarCore.getCommandManager().getLabels().keySet().stream().sorted().toList();
        for (var label : labels) {
            Command command = LunarCore.getCommandManager().getLabels().get(label).getClass().getAnnotation(Command.class);
            if (command == null) continue;
            
            this.sendMessage(sender, command.desc());
        }
    }

}
