package emu.lunarcore.server.packet.recv;

import emu.lunarcore.game.rogue.RogueMiracleData;
import emu.lunarcore.game.rogue.RogueMiracleSelectMenu;
import emu.lunarcore.proto.SelectRogueMiracleCsReqOuterClass.SelectRogueMiracleCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketSelectRogueMiracleScRsp;

@Opcodes(CmdId.NONE) // TODO update
public class HandlerSelectRogueMiracleCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = SelectRogueMiracleCsReq.parseFrom(data);
        
        if (session.getPlayer().getRogueInstance() != null) {
            RogueMiracleData miracle = session.getPlayer().getRogueInstance().selectMiracle(req.getMiracleId());
            if (miracle != null) {
                RogueMiracleSelectMenu miracleSelect = session.getPlayer().getRogueInstance().updateMiracleSelect();
                session.send(new PacketSelectRogueMiracleScRsp(miracle, miracleSelect));
            }
        }
        
        session.send(CmdId.NONE);
    }

}
