package emu.lunarcore.command.commands;

import emu.lunarcore.LunarRail;
import emu.lunarcore.command.Command;
import emu.lunarcore.command.CommandArgs;
import emu.lunarcore.command.CommandHandler;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.game.player.PlayerGender;
import emu.lunarcore.server.packet.send.PacketGetHeroBasicTypeInfoScRsp;

@Command(label = "unstuck", permission = "admin.unstuck", desc = "/unstuck @[player id]. Unstucks an offline player if theyre in a scene that doesnt load.")
public class UnstuckCommand implements CommandHandler {

    @Override
    public void execute(Player sender, CommandArgs args) {
        // Make sure were on the game server
        if (LunarRail.getGameDatabase() == null) {
            this.sendMessage(sender, "Error: Game database not connected");
            return;
        }
        
        // TODO add some logic to handle unstucking the target if theyre online
        if (args.getTarget() != null) {
            this.sendMessage(sender, "Error: Targeted player is online");
            return;
        }
        
        // Get player from the database
        Player player = LunarRail.getGameDatabase().getObjectByField(Player.class, "_id", args.getTargetUid());
        
        if (player != null) {
            // Reset position for the player
            player.resetPosition();
            player.save();
            
            // Done
            this.sendMessage(sender, "Player unstuck successfully");
        } else {
            // Done
            this.sendMessage(sender, "Error: Player not found in database");
        }
    }

}
