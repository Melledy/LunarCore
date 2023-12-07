package emu.lunarcore.server.packet.send;

import java.util.Collection;

import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.ExchangeGachaCeilingScRspOuterClass.ExchangeGachaCeilingScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketExchangeGachaCeilingScRsp extends BasePacket {

    public PacketExchangeGachaCeilingScRsp(Player player, int gachaType, int avatarId, Collection<GameItem> items) {
        super(CmdId.ExchangeGachaCeilingScRsp);
        
        var data = ExchangeGachaCeilingScRsp.newInstance();
        
        if (items == null) {
            data.setRetcode(1);
        } else {
            data.setGachaCeiling(player.getGachaInfo().toGachaCeiling(player));
            data.setGachaType(gachaType);
            data.setAvatarId(avatarId);
            
            for (var item : items) {
                data.getMutableTransferItemList().addItemList(item.toProto());
            }
        }
        
        this.setData(data);
    }
}
