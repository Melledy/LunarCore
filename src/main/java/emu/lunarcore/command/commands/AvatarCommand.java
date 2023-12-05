package emu.lunarcore.command.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import emu.lunarcore.command.Command;
import emu.lunarcore.command.CommandArgs;
import emu.lunarcore.command.CommandHandler;
import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.server.packet.send.PacketPlayerSyncScNotify;

@Command(label = "avatar", aliases = {"a"}, requireTarget = true, permission = "player.avatar", desc = "/avatar {cur | all | lineup} lv(level) p(ascension) r(eidolon) s(skill levels). Sets the current avatar's properties")
public class AvatarCommand implements CommandHandler {

    @Override
    public void execute(CommandArgs args) {
        // Temp avatar list
        List<GameAvatar> changeList = new ArrayList<>();
        
        // Handle optional arguments
        switch (args.get(0).toLowerCase()) {
        case "all":
            args.getTarget().getAvatars().forEach(changeList::add);
            break;
        case "lineup":
            args.getTarget().getCurrentLineup().forEachAvatar(changeList::add);
            break;
        case "cur":
        default:
            changeList.add(args.getTarget().getCurrentLeaderAvatar());
            break;
        }
        
        // Try to set properties of avatars
        Iterator<GameAvatar> it = changeList.iterator();
        while (it.hasNext()) {
            GameAvatar avatar = it.next();
            
            if (args.setProperties(avatar)) {
                // Save avatar
                avatar.save();
            } else {
                // Remove from list if nothing was changed
                it.remove();
            }
        }
        
        
        if (changeList.size() > 0) {
            // Send packet
            args.getTarget().sendPacket(new PacketPlayerSyncScNotify(changeList.toArray(GameAvatar[]::new)));
            // Send message
            args.sendMessage("Set avatar(s) properties successfully");
        } else {
            args.sendMessage("No avatar properties to change");
        }
    }

}
