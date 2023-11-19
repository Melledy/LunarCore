package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.EnableRogueTalentCsReqOuterClass.EnableRogueTalentCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketEnableRogueTalentScRsp;

@Opcodes(CmdId.EnableRogueTalentCsReq)
public class HandlerEnableRogueTalentCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = EnableRogueTalentCsReq.parseFrom(data);
        
        if (session.getPlayer().getRogueManager().enableTalent(req.getTalentId())) {
            session.send(new PacketEnableRogueTalentScRsp(session.getPlayer().getRogueManager()));
        } else {
            session.send(new PacketEnableRogueTalentScRsp());
        }
    }

}
