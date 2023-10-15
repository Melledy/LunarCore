package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.EnteredSceneInfoOuterClass.EnteredSceneInfo;
import emu.lunarcore.proto.GetEnteredSceneScRspOuterClass.GetEnteredSceneScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetEnteredSceneScRsp extends BasePacket {

    public PacketGetEnteredSceneScRsp(Player player) {
        super(CmdId.GetEnteredSceneScRsp);
        
        var sceneInfo = EnteredSceneInfo.newInstance();
        
        if (player.getScene() != null) {
            sceneInfo.setFloorId(player.getScene().getFloorId());
            sceneInfo.setPlaneId(player.getScene().getPlaneId());
        }
        
        var data = GetEnteredSceneScRsp.newInstance()
                .addEnteredSceneInfo(sceneInfo);
        
        this.setData(data);
    }
}
