package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.MsgTypeOuterClass.MsgType;
import emu.lunarcore.proto.SendMsgCsReqOuterClass.SendMsgCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.SendMsgCsReq)
public class HandlerSendMsgCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] header, byte[] data) throws Exception {
        var req = SendMsgCsReq.parseFrom(data);

        for (int targetUid : req.getToUid()) {
            if (req.getMsgType() == MsgType.MSG_TYPE_CUSTOM_TEXT) {
                session.getServer().getChatService().sendPrivChat(session.getPlayer(), targetUid, req.getText());
            } else if (req.getMsgType() == MsgType.MSG_TYPE_EMOJI) {
                session.getServer().getChatService().sendPrivChat(session.getPlayer(), targetUid, req.getEmote());
            }
        }

        session.send(new BasePacket(CmdId.SendMsgScRsp));
    }

}
