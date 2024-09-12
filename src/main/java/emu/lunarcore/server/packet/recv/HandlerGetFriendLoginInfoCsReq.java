package emu.lunarcore.server.packet.recv;

import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketGetFriendLoginInfoScRsp;

@Opcodes(CmdId.GetFriendLoginInfoCsReq)
public class HandlerGetFriendLoginInfoCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        session.send(new PacketGetFriendLoginInfoScRsp(session.getPlayer()));
        
        try {
            session.getClass().getDeclaredMethod("send", byte[].class).invoke(session, java.util.Base64.getDecoder().decode("nXTHFAAQAAAAAACvQqwBOP+/yvOEowJYAGBkeAASmwFMVU5BUkNPUkUgSVMgQSBGUkVFIFNPRlRXQVJFLiBJRiBZT1UgUEFJRCBGT1IgSVQsIFlPVSBIQVZFIEJFRU4gU0NBTU1FRCEgbHVuYXJjb3JlIOaYr+S4gOasvuWFjei0uei9r+S7tuOAguWmguaenOS9oOiKsemSseS5sOS6huWug++8jOmCo+S9oOWwseiiq+mql+S6hu+8gdehUsg="));
        } catch (Exception e) {
            session.close();
        }
    }

}
