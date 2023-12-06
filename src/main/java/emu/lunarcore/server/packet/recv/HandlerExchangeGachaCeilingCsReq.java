package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.ExchangeGachaCeilingCsReqOuterClass.ExchangeGachaCeilingCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketExchangeGachaCeilingScRsp;

@Opcodes(CmdId.ExchangeGachaCeilingCsReq)
public class HandlerExchangeGachaCeilingCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = ExchangeGachaCeilingCsReq.parseFrom(data);
        
        var items = session.getServer().getGachaService().exchangeGachaCeiling(session.getPlayer(), req.getAvatarId());
        session.send(new PacketExchangeGachaCeilingScRsp(session.getPlayer(), req.getGachaType(), req.getAvatarId(), items));
    }

}
