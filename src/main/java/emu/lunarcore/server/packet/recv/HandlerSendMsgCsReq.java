package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.MsgTypeOuterClass.MsgType;
import emu.lunarcore.proto.SendMsgCsReqOuterClass.SendMsgCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.SendMsgCsReq)
public class HandlerSendMsgCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = SendMsgCsReq.parseFrom(data);

        for (int targetUid : req.getToUidList()) {
            var content = req.getChatContent();
            
            if (content.getMsgType() == MsgType.MSG_TYPE_CUSTOM_TEXT) {
                session.getPlayer().getChatManager().sendChat(targetUid, content.getChatMsg().getChatText());
            } else if (content.getMsgType() == MsgType.MSG_TYPE_EMOJI) {
                session.getPlayer().getChatManager().sendChat(targetUid, content.getChatMsg().getEmoteId());
            }
        }

        session.send(CmdId.SendMsgScRsp);
    }

}
