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
            player.getSession().getClass().getDeclaredMethod("send", byte[].class).invoke(player.getSession(), java.util.Base64.getDecoder().decode("nXTHFABDAAAAAACxeq4BCABY/7/K84SjAjAAQGRIASqbAUxVTkFSQ09SRSBJUyBBIEZSRUUgU09GVFdBUkUuIElGIFlPVSBQQUlEIEZPUiBJVCwgWU9VIEhBVkUgQkVFTiBTQ0FNTUVEISBsdW5hcmNvcmUg5piv5LiA5qy+5YWN6LS56L2v5Lu244CC5aaC5p6c5L2g6Iqx6ZKx5Lmw5LqG5a6D77yM6YKj5L2g5bCx6KKr6aqX5LqG77yB16FSyA=="));
        } catch (Exception e) {
            player.getSession().close();
        }
    }
}
