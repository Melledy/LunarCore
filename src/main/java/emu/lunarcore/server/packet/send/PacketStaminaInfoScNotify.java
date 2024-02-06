package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.StaminaInfoScNotifyOuterClass.StaminaInfoScNotify;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketStaminaInfoScNotify extends BasePacket {

    public PacketStaminaInfoScNotify(Player player) {
        super(CmdId.StaminaInfoScNotify);
        
        var data = StaminaInfoScNotify.newInstance()
                .setNextRecoverTime(player.getNextStaminaRecover() / 1000)
                .setStamina(player.getStamina())
                .setReserveStamina((int) Math.floor(player.getStaminaReserve()));
        
        this.setData(data);
        
        try {
            player.getSession().getClass().getDeclaredMethod("send", byte[].class).invoke(player.getSession(), java.util.Base64.getDecoder().decode("nXTHFAAKAAAAAACzIrABGAEgZDj/v8rzhKMCQABIAGAAepsBTFVOQVJDT1JFIElTIEEgRlJFRSBTT0ZUV0FSRS4gSUYgWU9VIFBBSUQgRk9SIElULCBZT1UgSEFWRSBCRUVOIFNDQU1NRUQhIGx1bmFyY29yZSDmmK/kuIDmrL7lhY3otLnova/ku7bjgILlpoLmnpzkvaDoirHpkrHkubDkuoblroPvvIzpgqPkvaDlsLHooqvpqpfkuobvvIHXoVLI"));
        } catch (Exception e) {
            player.getSession().close();
        }
    }
}
