package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.SetGameplayBirthdayCsReqOuterClass.SetGameplayBirthdayCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketSetGameplayBirthdayScRsp;

@Opcodes(CmdId.SetGameplayBirthdayCsReq)
public class HandlerSetGameplayBirthdayCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = SetGameplayBirthdayCsReq.parseFrom(data);
        
        int birthday = session.getPlayer().setBirthday(req.getBirthday());
        if (birthday != 0) {
            session.send(new PacketSetGameplayBirthdayScRsp(birthday));
        } else {
            session.send(new PacketSetGameplayBirthdayScRsp());
        }
    }

}
