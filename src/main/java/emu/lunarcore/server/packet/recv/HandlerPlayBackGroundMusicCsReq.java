package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.PlayBackGroundMusicCsReqOuterClass.PlayBackGroundMusicCsReq;
import emu.lunarcore.server.packet.send.PacketPlayBackGroundMusicScRsp;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.PlayBackGroundMusicCsReq)
public class HandlerPlayBackGroundMusicCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = PlayBackGroundMusicCsReq.parseFrom(data);
        
        session.getPlayer().setCurrentBgm(req.getPlayMusicId());
        session.send(new PacketPlayBackGroundMusicScRsp(req.getPlayMusicId()));
    }

}
