package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.PickRogueAvatarScRspOuterClass.PickRogueAvatarScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

import java.util.HashSet;

public class PacketPickRogueAvatarScRsp extends BasePacket {
    public PacketPickRogueAvatarScRsp(HashSet<Integer> avatarIds) {
        super(CmdId.PickRogueAvatarScRsp);
        
        var proto = PickRogueAvatarScRsp.newInstance();
        
        for (var avatarId : avatarIds) {
            proto.addBaseAvatarList(avatarId);
        }
        
        this.setData(proto);
    }
}
