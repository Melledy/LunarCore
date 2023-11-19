package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.ExchangeHcoinCsReqOuterClass.ExchangeHcoinCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;

@Opcodes(CmdId.ExchangeHcoinCsReq)
public class HandlerExchangeHcoinCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = ExchangeHcoinCsReq.parseFrom(data);
        
        if (session.getPlayer().getMcoin() >= req.getNum()) {
            session.getPlayer().addMCoin(-req.getNum());
            session.getPlayer().addHCoin(req.getNum());
            session.getPlayer().save();
        }
        
        session.send(CmdId.ExchangeHcoinScRsp);
    }

}
