package emu.lunarcore.command.commands;

import emu.lunarcore.command.Command;
import emu.lunarcore.command.CommandArgs;
import emu.lunarcore.command.CommandHandler;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.game.player.PlayerGender;
import emu.lunarcore.server.packet.send.PacketGetHeroBasicTypeInfoScRsp;

@Command(label = "gender")
public class GenderCommand implements CommandHandler {

    @Override
    public void execute(Player sender, CommandArgs args) {
        // Check target
        if (args.getTarget() == null) {
            this.sendMessage(sender, "Error: Targeted player not found or offline");
            return;
        }
        
        // Set world level
        String gender = args.get(0).toLowerCase();
        
        switch (gender) {
            case "m", "male", "boy", "man" -> {
                args.getTarget().setGender(PlayerGender.GENDER_MAN);
            }
            case "f", "female", "girl", "woman" -> {
                args.getTarget().setGender(PlayerGender.GENDER_WOMAN);
            }
        }
        
        // Send packet
        args.getTarget().sendPacket(new PacketGetHeroBasicTypeInfoScRsp(args.getTarget()));
        
        // Done
        this.sendMessage(sender, "Gender set successfully");
    }

}
