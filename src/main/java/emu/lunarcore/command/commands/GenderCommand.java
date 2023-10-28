package emu.lunarcore.command.commands;

import emu.lunarcore.command.Command;
import emu.lunarcore.command.CommandArgs;
import emu.lunarcore.command.CommandHandler;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.game.player.PlayerGender;
import emu.lunarcore.server.packet.send.PacketGetHeroBasicTypeInfoScRsp;

@Command(label = "gender", permission = "player.gender", desc = "/gender {male | female}. Sets the player gender.")
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
        PlayerGender playerGender = null;
        
        switch (gender) {
            case "m", "male", "boy", "man" -> {
                playerGender = PlayerGender.GENDER_MAN;
            }
            case "f", "female", "girl", "woman" -> {
                playerGender = PlayerGender.GENDER_WOMAN;
            }
        }
        
        // Change gender
        if (playerGender != null && playerGender != args.getTarget().getGender()) {
            args.getTarget().setGender(playerGender);
            args.getTarget().sendPacket(new PacketGetHeroBasicTypeInfoScRsp(args.getTarget()));
            
            this.sendMessage(sender, "Gender for " + args.getTarget().getName() + " set successfully");
        } else {
            this.sendMessage(sender, "Error: Invalid input");
        }
    }

}
