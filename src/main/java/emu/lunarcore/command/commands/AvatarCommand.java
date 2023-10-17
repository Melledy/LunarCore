package emu.lunarcore.command.commands;

import emu.lunarcore.command.Command;
import emu.lunarcore.command.CommandArgs;
import emu.lunarcore.command.CommandHandler;
import emu.lunarcore.data.GameData;
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
        
        boolean hasChanged = false;
        
        // Try to set level
        if (args.getLevel() > 0) {
            avatar.setLevel(args.getLevel());
            hasChanged = true;
        }
        
        // Try to set level
        if (args.getLevel() > 0) {
            avatar.setLevel(Math.min(args.getLevel(), 80));
            hasChanged = true;
        }
        
        // Try to set promotion (ascension level)
        if (args.getPromotion() >= 0) {
            avatar.setPromotion(Math.min(args.getPromotion(), avatar.getExcel().getMaxPromotion()));
            hasChanged = true;
        }
        
        // Try to set rank (eidolons)
        if (args.getRank() >= 0) {
            avatar.setRank(Math.min(args.getRank(), avatar.getExcel().getMaxRank()));
            hasChanged = true;
        }
        
        // Try to set skill trees
        if (args.getStage() > 0) {
            for (int pointId : avatar.getExcel().getSkillTreeIds()) {
                var skillTree = GameData.getAvatarSkillTreeExcel(pointId, 1);
                if (skillTree == null) continue;
                
                int minLevel = skillTree.isDefaultUnlock() ? 1 : 0;
                int pointLevel = Math.max(Math.min(args.getStage(), skillTree.getMaxLevel()), minLevel);
                
                avatar.getSkills().put(pointId, pointLevel);
            }
            hasChanged = true;
        }

        // Done
        if (hasChanged) {
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
