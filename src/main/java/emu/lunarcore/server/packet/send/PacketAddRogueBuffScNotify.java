package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.rogue.RogueBuffData;
import emu.lunarcore.proto.AddRogueBuffScNotifyOuterClass.AddRogueBuffScNotify;
import emu.lunarcore.proto.RogueBuffSourceOuterClass.RogueBuffSource;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketAddRogueBuffScNotify extends BasePacket {

    public PacketAddRogueBuffScNotify(RogueBuffData buff, RogueBuffSource source) {
        super(CmdId.NONE); // TODO update
        
        var data = AddRogueBuffScNotify.newInstance()
                .setMazeBuffInfo(buff.toProto())
                .setSource(source);
        
        this.setData(data);
    }
}
