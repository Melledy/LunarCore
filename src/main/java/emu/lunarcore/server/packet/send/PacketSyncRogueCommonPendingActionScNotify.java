package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.RogueActionOuterClass.RogueAction;
import emu.lunarcore.proto.RogueBonusSelectInfoOuterClass.RogueBonusSelectInfo;
import emu.lunarcore.proto.RogueCommonBuffSelectInfoOuterClass.RogueCommonBuffSelectInfo;
import emu.lunarcore.proto.RogueMiracleSelectInfoOuterClass.RogueMiracleSelectInfo;
import emu.lunarcore.proto.RogueCommonPendingActionOuterClass.RogueCommonPendingAction;
import emu.lunarcore.proto.SyncRogueCommonPendingActionScNotifyOuterClass.SyncRogueCommonPendingActionScNotify;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSyncRogueCommonPendingActionScNotify extends BasePacket {
    public PacketSyncRogueCommonPendingActionScNotify(RogueCommonPendingAction action) {
        super(CmdId.SyncRogueCommonPendingActionScNotify);
        
        var proto = SyncRogueCommonPendingActionScNotify.newInstance();
        
        proto.setRogueCommonPendingAction(action);
            //.setRogueVersionId(101);  // common rogue
        
        this.setData(proto);
    }
    
    public PacketSyncRogueCommonPendingActionScNotify(RogueAction action, int id) {
        this(RogueCommonPendingAction.newInstance()
            //.setActionUniqueId(id)
            .setRogueAction(action));
    }
    
    public PacketSyncRogueCommonPendingActionScNotify(int id) {
        this(RogueAction.newInstance(), id);
    }
    
    public PacketSyncRogueCommonPendingActionScNotify(RogueCommonBuffSelectInfo info, int id) {
        this(RogueAction.newInstance()
                .setBuffSelectInfo(info), id);
    }
    
    public PacketSyncRogueCommonPendingActionScNotify(RogueMiracleSelectInfo info, int id) {
        this(RogueAction.newInstance()
                .setMiracleSelectInfo(info), id);
    }
    
    public PacketSyncRogueCommonPendingActionScNotify(RogueBonusSelectInfo info, int id) {
        this(RogueAction.newInstance()
                .setBonusSelectInfo(info), id);
    }
    
    public RogueCommonPendingAction toProto() {
        return ((SyncRogueCommonPendingActionScNotify) this.getData()).getRogueCommonPendingAction();
    }
}
