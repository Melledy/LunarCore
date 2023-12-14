package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.scene.SceneBuff;
import emu.lunarcore.proto.EntityBuffChangeInfoOuterClass.EntityBuffChangeInfo;
import emu.lunarcore.proto.SyncEntityBuffChangeListScNotifyOuterClass.SyncEntityBuffChangeListScNotify;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSyncEntityBuffChangeListScNotify extends BasePacket {

    public PacketSyncEntityBuffChangeListScNotify(int entityId, SceneBuff buff) {
        super(CmdId.SyncEntityBuffChangeListScNotify);
        
        var buffChange = EntityBuffChangeInfo.newInstance().setEntityId(entityId)
                .setAddBuffInfo(buff.toProto())
                .setEntityId(entityId);
        
        var data = SyncEntityBuffChangeListScNotify.newInstance()
                .addEntityBuffInfoList(buffChange);
        
        this.setData(data);
    }

    public PacketSyncEntityBuffChangeListScNotify(int entityId, int removeBuffId) {
        super(CmdId.SyncEntityBuffChangeListScNotify);
        
        var buffChange = EntityBuffChangeInfo.newInstance().setEntityId(entityId)
                .setRemoveBuffId(removeBuffId)
                .setEntityId(entityId);
        
        var data = SyncEntityBuffChangeListScNotify.newInstance()
                .addEntityBuffInfoList(buffChange);
        
        this.setData(data);
    }
}
