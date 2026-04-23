package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.DisplayRecordTypeOuterClass.DisplayRecordType;
import emu.lunarcore.proto.GetFriendBattleRecordDetailCsReqOuterClass.GetFriendBattleRecordDetailCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketGetFriendBattleRecordDetailScRsp;


@Opcodes(CmdId.GetFriendBattleRecordDetailCsReq)
public class HandlerGetFriendBattleRecordDetailCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = GetFriendBattleRecordDetailCsReq.parseFrom(data);

        int uid = req.getUid();
        DisplayRecordType type = req.getType();

        if (req.hasGroupId()) {
            int groupId = req.getGroupId();
            session.send(new PacketGetFriendBattleRecordDetailScRsp(uid, type, groupId));
        } else {
            session.send(new PacketGetFriendBattleRecordDetailScRsp(uid, type));
        }

    }

}
