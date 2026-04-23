package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.proto.SetAvatarEnhancedIdScRspOuterClass.SetAvatarEnhancedIdScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSetAvatarEnhancedIdScRsp extends BasePacket {

    public PacketSetAvatarEnhancedIdScRsp() {
        super(CmdId.SetAvatarEnhancedIdScRsp);

        var data = SetAvatarEnhancedIdScRsp.newInstance()
                .setRetcode(1);
        
        this.setData(data);
    }
    
    public PacketSetAvatarEnhancedIdScRsp(GameAvatar avatar) {
        super(CmdId.SetAvatarEnhancedIdScRsp);

        var data = SetAvatarEnhancedIdScRsp.newInstance()
                .setEnhanceId(avatar.getEnhanceId());
        
        this.setData(data);
    }
}
