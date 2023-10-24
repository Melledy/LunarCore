package emu.lunarcore.server.packet.send;

import java.util.Collection;

import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.proto.ScenePlaneEventScNotifyOuterClass.ScenePlaneEventScNotify;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketScenePlaneEventScNotify extends BasePacket {
    
    public PacketScenePlaneEventScNotify(GameItem item) {
        super(CmdId.ScenePlaneEventScNotify);
        
        var data = ScenePlaneEventScNotify.newInstance();
        
        if (item != null) {
            data.getMutableGetItemList().addItemList(item.toProto());
        } else {
            data.getMutableGetItemList();
        }
        
        this.setData(data);
    }

    public PacketScenePlaneEventScNotify(Collection<GameItem> items) {
        super(CmdId.ScenePlaneEventScNotify);
        
        var data = ScenePlaneEventScNotify.newInstance();
        
        if (items != null && items.size() > 0) {
            for (var item : items) {
                data.getMutableGetItemList().addItemList(item.toProto());
            }
        } else {
            data.getMutableGetItemList();
        }
        
        this.setData(data);
    }
}
