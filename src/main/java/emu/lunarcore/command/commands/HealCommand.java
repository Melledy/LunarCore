package emu.lunarcore.command.commands;

import emu.lunarcore.LunarCore;
import emu.lunarcore.command.Command;
import emu.lunarcore.command.CommandArgs;
import emu.lunarcore.command.CommandHandler;
import emu.lunarcore.server.packet.send.PacketSyncLineupNotify;
import emu.lunarcore.game.player.lineup.LineupManager;
import emu.lunarcore.game.player.lineup.PlayerLineup;
import emu.lunarcore.game.player.Player;

@Command(label = "heal", permission = "player.heal", desc = "/heal. Heals your avatars.")
public class HealCommand implements CommandHandler {

    @Override
    public void execute(Player sender, CommandArgs args) {

        LineupManager lineupManager = sender.getLineupManager();
        PlayerLineup lineup = lineupManager.getLineupByIndex(lineupManager.getCurrentIndex());  
        
        lineup.forEachAvatar(avatar -> {
            avatar.setCurrentHp(lineup, 10000);
            avatar.save();
        });

        lineup.save();

        sender.getScene().syncLineup();
        sender.sendPacket(new PacketSyncLineupNotify(lineup));

        this.sendMessage(sender, "Healed all avatars.");
    }

}
