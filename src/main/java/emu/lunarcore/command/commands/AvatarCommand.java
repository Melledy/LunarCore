package emu.lunarcore.command.commands;

import emu.lunarcore.command.Command;
import emu.lunarcore.command.CommandArgs;
import emu.lunarcore.command.CommandHandler;
import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.server.packet.send.PacketPlayerSyncScNotify;

@Command(label = "avatar", aliases = {"a"}, permission = "player.avatar", desc = "/avatar lv(level) p(ascension) r(eidolon) s(skill levels). Sets the current avatar's properties")
public class AvatarCommand implements CommandHandler {

    @Override
    public void execute(Player sender, CommandArgs args) {
        // Check target
        if (args.getTarget() == null) {
            this.sendMessage(sender, "Error: Targeted player not found or offline");
            return;
        }
        
        // Get current leader avatar
        GameAvatar avatar = args.getTarget().getCurrentLeaderAvatar();
        if (avatar == null) {
            // No leader!
            return;
        }
        
        // Change properties
        if (args.setProperties(avatar)) {
            // Save avatar
            avatar.save();
            
            // Send packet
            args.getTarget().sendPacket(new PacketPlayerSyncScNotify(avatar));
            
            // Send message
            sender.sendMessage("Set avatar properties successfully");
        } else {
            sender.sendMessage("No avatar properties to change");
        }
    }

}
