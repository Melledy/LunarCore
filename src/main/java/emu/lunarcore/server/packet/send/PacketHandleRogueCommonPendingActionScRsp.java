package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.HandleRogueCommonPendingActionScRspOuterClass.HandleRogueCommonPendingActionScRsp;
import emu.lunarcore.proto.RogueCommonBuffSelectInfoOuterClass.RogueCommonBuffSelectInfo;
import emu.lunarcore.proto.RogueRerollBuffOuterClass.RogueRerollBuff;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketHandleRogueCommonPendingActionScRsp extends BasePacket {
    public PacketHandleRogueCommonPendingActionScRsp(HandleRogueCommonPendingActionScRsp proto) {
        super(CmdId.HandleRogueCommonPendingActionScRsp);

        this.setData(proto);
    }
    
    public PacketHandleRogueCommonPendingActionScRsp(int id) {
        this(HandleRogueCommonPendingActionScRsp.newInstance()
            .setTimes(id));
    }
    
    public PacketHandleRogueCommonPendingActionScRsp(RogueRerollBuff rogueRerollBuff, int id) {
        this(HandleRogueCommonPendingActionScRsp.newInstance()
            .setTimes(id)
            .setRogueRerollBuff(rogueRerollBuff));
    }
    
    public PacketHandleRogueCommonPendingActionScRsp(RogueCommonBuffSelectInfo buffSelectInfo, int id) {
        this(RogueRerollBuff.newInstance()
            .setBuffSelectInfo(buffSelectInfo), id);
    }
}
