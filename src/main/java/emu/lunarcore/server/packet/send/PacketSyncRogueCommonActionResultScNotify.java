package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.RogueBuffDataOuterClass.RogueBuffData;
import emu.lunarcore.proto.RogueMiracleDataOuterClass.RogueMiracleData;
import emu.lunarcore.proto.RogueActionResultDataOuterClass.RogueActionResultData;
import emu.lunarcore.proto.RogueActionResultOuterClass.RogueActionResult;
import emu.lunarcore.proto.RogueBuffSourceOuterClass.RogueBuffSource;
import emu.lunarcore.proto.RogueVirtualItemDataOuterClass.RogueVirtualItemData;
import emu.lunarcore.proto.SyncRogueCommonActionResultScNotifyOuterClass.SyncRogueCommonActionResultScNotify;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSyncRogueCommonActionResultScNotify extends BasePacket {
    public PacketSyncRogueCommonActionResultScNotify(RogueActionResult action) {
        super(CmdId.SyncRogueCommonActionResultScNotify);
        
        var proto = SyncRogueCommonActionResultScNotify.newInstance();
            //.setRogueVersionId(101);
        
        proto.setAction(action);
        
        this.setData(proto);
    }
    
    public PacketSyncRogueCommonActionResultScNotify(RogueBuffSource source, RogueActionResultData data) {
        this(RogueActionResult.newInstance()
            .setSource(source)
            .setActionData(data));
    }

    public PacketSyncRogueCommonActionResultScNotify(RogueBuffSource source, RogueMiracleData miracle) {
        this(source, RogueActionResultData.newInstance()
            .setAddMiracleList(miracle));
    }
    
    public PacketSyncRogueCommonActionResultScNotify(RogueBuffSource source, RogueBuffData buff) {
        this(source, RogueActionResultData.newInstance()
            .setAddBuffList(buff));
    }
    
    public PacketSyncRogueCommonActionResultScNotify(RogueBuffSource source, int addMoneyNum) {
        this(source, RogueActionResultData.newInstance()
            .setAddItemList(RogueVirtualItemData.newInstance()
                .setNum(addMoneyNum)));
    }
}
