package emu.lunarcore.command.commands;

import emu.lunarcore.LunarCore;
import emu.lunarcore.command.Command;
import emu.lunarcore.command.CommandArgs;
import emu.lunarcore.command.CommandHandler;
import emu.lunarcore.game.player.Player;

@Command(label = "unstuck", permission = "admin.unstuck", desc = "/unstuck @[player id]. Unstucks an offline player if theyre in a scene that doesnt load.")
public class UnstuckCommand implements CommandHandler {

    @Override
    public void execute(CommandArgs args) {
        // Make sure were on the game server
        if (LunarCore.getGameDatabase() == null) {
            args.sendMessage("Error: Game database not connected");
            return;
        }
        
        // TODO add some logic to handle unstucking the target if theyre online
        if (args.getTarget() != null) {
            args.sendMessage("Error: Targeted player is online");
            return;
        }
        
        // Get player from the database
        Player player = LunarCore.getGameDatabase().getObjectByField(Player.class, "_id", args.getTargetUid());
        
        if (player != null) {
            // Reset position for the player
            player.resetPosition();
            player.save();
            
            // Done
            args.sendMessage("Player unstuck successfully");
        } else {
            // Done
            args.sendMessage("Error: Player not found in database");
        }
    }

}
