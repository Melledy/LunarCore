package emu.lunarcore.server.packet.recv;

import emu.lunarcore.game.rogue.RogueBuffData;
import emu.lunarcore.game.rogue.RogueBuffSelectMenu;
import emu.lunarcore.proto.SelectRogueBuffCsReqOuterClass.SelectRogueBuffCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketSelectRogueBuffScRsp;

@Opcodes(CmdId.NONE) // TODO update
public class HandlerSelectRogueBuffCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = SelectRogueBuffCsReq.parseFrom(data);
        
        if (session.getPlayer().getRogueInstance() != null) {
            RogueBuffData buff = session.getPlayer().getRogueInstance().selectBuff(req.getMazeBuffId());
            if (buff != null) {
                RogueBuffSelectMenu buffSelect = session.getPlayer().getRogueInstance().updateBuffSelect();
                session.send(new PacketSelectRogueBuffScRsp(buff, buffSelect));
            }
        }
        
        session.send(CmdId.NONE);
    }

}
