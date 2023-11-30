package emu.lunarcore.command.commands;

import java.util.ArrayList;
import java.util.List;

import emu.lunarcore.command.Command;
import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.player.lineup.PlayerLineup;
import emu.lunarcore.command.CommandArgs;
import emu.lunarcore.command.CommandHandler;
import emu.lunarcore.game.player.Player;

@Command(label = "energy", permission = "player.energy", desc = "/energy. Refills all characters energy in current lineup.")
public class RefillSPCommand implements CommandHandler {

    @Override
    public void execute(Player sender, CommandArgs args) {

        PlayerLineup lineup = sender.getCurrentLineup();
        for (int i = 0; i < lineup.getAvatars().size(); i++) {
            GameAvatar avatar = sender.getAvatarById(lineup.getAvatars().get(i));
            if (avatar == null) continue;
            avatar.setCurrentSp(lineup, 10000);
            avatar.save();
        }
        lineup.save();
        
        lineup.refreshLineup();
        this.sendMessage(sender, "Refilled SP");
    }

}
