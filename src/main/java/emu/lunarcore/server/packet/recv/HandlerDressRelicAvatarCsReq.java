package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.DressRelicAvatarCsReqOuterClass.DressRelicAvatarCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.DressRelicAvatarCsReq)
public class HandlerDressRelicAvatarCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = DressRelicAvatarCsReq.parseFrom(data);

        for (var param : req.getParamList()) {
            session.getPlayer().getInventory().equipItem(req.getBaseAvatarId(), param.getRelicUniqueId());
        }

        session.send(CmdId.DressRelicAvatarScRsp);
    }

}
