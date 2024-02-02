package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.EnhanceRogueBuffCsReqOuterClass.EnhanceRogueBuffCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketEnhanceRogueBuffScRsp;

@Opcodes(CmdId.EnhanceRogueBuffCsReq)
public class HandlerEnhanceRogueBuffCsReq extends PacketHandler {
    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var proto = EnhanceRogueBuffCsReq.parseFrom(data);
        var buffId = proto.getBuffId();
        
        var buff = session.getPlayer().getRogueInstance().enhanceBuff(buffId);
        session.send(new PacketEnhanceRogueBuffScRsp(buff));
    }
}
