package emu.lunarcore.server.packet.recv;

import emu.lunarcore.game.rogue.RogueBuffSelectMenu;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketRollRogueBuffScRsp;

@Opcodes(CmdId.NONE) // TODO update
public class HandlerRollRogueBuffCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        RogueBuffSelectMenu selectMenu = null;
        
        if (session.getPlayer().getRogueInstance() != null) {
            selectMenu = session.getPlayer().getRogueInstance().rollBuffSelect();
        }
        
        session.send(new PacketRollRogueBuffScRsp(selectMenu));
    }

}
