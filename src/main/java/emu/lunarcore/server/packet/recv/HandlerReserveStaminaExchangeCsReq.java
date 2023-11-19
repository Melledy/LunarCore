package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.ReserveStaminaExchangeCsReqOuterClass.ReserveStaminaExchangeCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketReserveStaminaExchangeScRsp;

@Opcodes(CmdId.ReserveStaminaExchangeCsReq)
public class HandlerReserveStaminaExchangeCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = ReserveStaminaExchangeCsReq.parseFrom(data);
        
        int exchangedAmount = session.getPlayer().exchangeReserveStamina(req.getNum());
        session.send(new PacketReserveStaminaExchangeScRsp(exchangedAmount));
    }

}
