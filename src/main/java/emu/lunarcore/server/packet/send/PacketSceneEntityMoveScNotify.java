package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.MotionInfoOuterClass.MotionInfo;
import emu.lunarcore.proto.SceneEntityMoveScNotifyOuterClass.SceneEntityMoveScNotify;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSceneEntityMoveScNotify extends BasePacket {

    public PacketSceneEntityMoveScNotify(Player player) {
        super(CmdId.SceneEntityMoveScNotify);
        
        var data = SceneEntityMoveScNotify.newInstance()
                .setEntryId(player.getEntryId())
                .setMotion(MotionInfo.newInstance().setPos(player.getPos().toProto()).setRot(player.getRot().toProto()));
        
        this.setData(data);
    }
    
}
