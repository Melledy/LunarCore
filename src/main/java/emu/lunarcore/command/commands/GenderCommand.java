package emu.lunarcore.command.commands;

import emu.lunarcore.command.Command;
import emu.lunarcore.command.CommandArgs;
import emu.lunarcore.command.CommandHandler;
import emu.lunarcore.data.GameData;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.game.player.PlayerGender;
import emu.lunarcore.server.packet.send.PacketGetHeroBasicTypeInfoScRsp;

@Command(label = "gender", permission = "player.gender", requireTarget = true, desc = "/gender {male | female}. Sets the player gender.")
public class GenderCommand implements CommandHandler {

    @Override
    public void execute(CommandArgs args) {
        // Set world level
        Player target = args.getTarget();
        PlayerGender playerGender = null;
        
        String gender = args.get(0).toLowerCase();
        switch (gender) {
            case "m", "male", "boy", "man" -> {
                playerGender = PlayerGender.GENDER_MAN;
            }
            case "f", "female", "girl", "woman" -> {
                playerGender = PlayerGender.GENDER_WOMAN;
            }
        }
        
        // Change gender
        if (playerGender != null && playerGender != target.getGender()) {
            // Set gender first
            target.setGender(playerGender);
            target.save();

            // Get first hero excel that matches our new player gender
            var heroExcel = GameData.getHeroExcelMap().values().stream().filter(path -> path.getGender() == target.getGender()).findFirst().orElse(null);
            if (heroExcel != null) {
                // Set hero basic type
                target.setHeroBasicType(heroExcel.getId());
            }
            
            // Send packet and response message
            target.sendPacket(new PacketGetHeroBasicTypeInfoScRsp(target));
            args.sendMessage("Gender for " + target.getName() + " set successfully");
        } else {
            args.sendMessage("Error: Invalid input");
        }
    }

}
