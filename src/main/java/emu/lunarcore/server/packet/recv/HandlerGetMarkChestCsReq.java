package emu.lunarcore.server.packet.recv;

import java.util.ArrayList;
import java.util.List;

import emu.lunarcore.proto.MapPropDefInfoOuterClass.MapPropDefInfo;
import emu.lunarcore.proto.MarkChestInfoOuterClass.MarkChestInfo;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketGetMarkChestScRsp;

@Opcodes(CmdId.GetMarkChestCsReq)
public class HandlerGetMarkChestCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        List<MarkChestInfo> toRsp = new ArrayList<>();

        if (session.getPlayer().getScene() != null) {
            List<Integer> mapPropDefInfoList = session.getPlayer().getScene().getFuncIdsForMapPropDef();
            for (int i : mapPropDefInfoList) {
                MarkChestInfo markChestInfo = MarkChestInfo.newInstance()
                        .setFuncId(i);
                List<MapPropDefInfo> mapPropDefInfos = session.getPlayer().getScene().getMapPropDefInfo(i);
                for (MapPropDefInfo mapPropDefInfo : mapPropDefInfos) {
                    markChestInfo.addChestPropDefList(mapPropDefInfo);
                }
                toRsp.add(markChestInfo);
            }
        }

        session.send(new PacketGetMarkChestScRsp(toRsp));
    }

}