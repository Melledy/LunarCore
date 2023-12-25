package emu.lunarcore.server.packet.recv;

import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.proto.SelectPhoneThemeCsReqOuterClass.SelectPhoneThemeCsReq;
import emu.lunarcore.server.packet.send.PacketSelectPhoneThemeScRsp;
import emu.lunarcore.server.packet.send.PacketSetHeadIconScRsp;

@Opcodes(CmdId.SelectPhoneThemeCsReq)
public class HandlerSelectPhoneThemeCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = SelectPhoneThemeCsReq.parseFrom(data);
        
        if (session.getPlayer().setPhoneTheme(req.getThemeId())) {
            // Success
            session.send(new PacketSelectPhoneThemeScRsp(req.getThemeId()));
        } else {
            // Failure (player didnt have the phone theme)
            session.send(new PacketSetHeadIconScRsp());
        }
    }
    
}
