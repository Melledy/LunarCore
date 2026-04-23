package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.UnlockTutorialCsReqOuterClass.UnlockTutorialCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketUnlockTutorialScRsp;

@Opcodes(CmdId.UnlockTutorialCsReq)
public class HandlerUnlockTutorialCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = UnlockTutorialCsReq.parseFrom(data);
        session.send(new PacketUnlockTutorialScRsp(req.getTutorialId()));
    }

}