package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.MotionInfoOuterClass.MotionInfo;
import emu.lunarcore.proto.RotateMapCsReqOuterClass.RotateMapCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketRotateMapScRsp;

@Opcodes(CmdId.RotateMapCsReq)
public class HandlerRotateMapCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = RotateMapCsReq.parseFrom(data);

        MotionInfo motion = req.getMotion();

        session.send(new PacketRotateMapScRsp(motion));
    }
}
