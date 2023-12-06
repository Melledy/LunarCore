package emu.lunarcore.server.packet.recv;

import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.proto.SelectPhoneThemeCsReqOuterClass.SelectPhoneThemeCsReq;
import emu.lunarcore.server.packet.send.PacketSelectPhoneThemeScRsp;

@Opcodes(CmdId.SelectPhoneThemeCsReq)
public class HandlerSelectPhoneThemeCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {

        var req = SelectPhoneThemeCsReq.parseFrom(data);
        Player player = session.getPlayer();
        
        session.send(new PacketSelectPhoneThemeScRsp(player, req.getThemeId()));
    }
    
}
