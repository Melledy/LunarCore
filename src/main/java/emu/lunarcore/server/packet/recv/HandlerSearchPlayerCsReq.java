package emu.lunarcore.server.packet.recv;

import java.util.ArrayList;
import java.util.List;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.SearchPlayerCsReqOuterClass.SearchPlayerCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketSearchPlayerScRsp;

@Opcodes(CmdId.SearchPlayerCsReq)
public class HandlerSearchPlayerCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = SearchPlayerCsReq.parseFrom(data);
        
        // Setup result list
        List<Player> results = new ArrayList<>();
        
        // Get searched player
        for (int uid : req.getSearchUidList()) {
            Player target = session.getServer().getPlayerByUid(uid, true);
            
            if (target != null) {
                results.add(target);
            }
        }
        
        session.send(new PacketSearchPlayerScRsp(results));
    }

}
