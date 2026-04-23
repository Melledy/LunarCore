package emu.lunarcore.server.packet.recv;

import java.util.List;

import emu.lunarcore.proto.MapPropDefInfoOuterClass.MapPropDefInfo;
import emu.lunarcore.proto.MarkChestInfoOuterClass.MarkChestInfo;
import emu.lunarcore.proto.UpdateMarkChestCsReqOuterClass.UpdateMarkChestCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketMarkChestChangedScNotify;
import emu.lunarcore.server.packet.send.PacketUpdateMarkChestScRsp;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@Opcodes(CmdId.UpdateMarkChestCsReq)
public class HandlerUpdateMarkChestCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        UpdateMarkChestCsReq req = UpdateMarkChestCsReq.parseFrom(data);

        int funcId = req.getFuncId();
        int markAvatarId = req.getMarkAvatarId();
        List<MapPropDefInfo> mapPropDefInfo = new ObjectArrayList<>();

        for(MapPropDefInfo propDefInfo : req.getChestPropDefList()) {
            mapPropDefInfo.add(propDefInfo);
        }

        List<MarkChestInfo> toRsp = new ObjectArrayList<>();

        if (session.getPlayer().getScene() != null) {
            session.getPlayer().getScene().setMapPropDefInfoForFuncId(funcId, mapPropDefInfo);
        }

        MarkChestInfo rsp = MarkChestInfo.newInstance()
                .setFuncId(funcId);

        for (MapPropDefInfo propDefInfo : mapPropDefInfo) {
            rsp.addChestPropDefList(propDefInfo);
        }

        toRsp.add(rsp);

        session.send(new PacketUpdateMarkChestScRsp(funcId, markAvatarId, toRsp));
        session.send(new PacketMarkChestChangedScNotify(toRsp));
    }
}