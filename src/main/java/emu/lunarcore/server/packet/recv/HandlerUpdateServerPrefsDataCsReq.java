package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.UpdateServerPrefsDataCsReqOuterClass.UpdateServerPrefsDataCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketUpdateServerPrefsDataScRsp;

@Opcodes(CmdId.UpdateServerPrefsDataCsReq)
public class HandlerUpdateServerPrefsDataCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = UpdateServerPrefsDataCsReq.parseFrom(data);
        
        if (req.hasServerPrefs()) {
            session.send(new PacketUpdateServerPrefsDataScRsp(req.getServerPrefs().getServerPrefsId()));
        } else {
            session.send(CmdId.UpdateServerPrefsDataScRsp);
        }
    }

}
