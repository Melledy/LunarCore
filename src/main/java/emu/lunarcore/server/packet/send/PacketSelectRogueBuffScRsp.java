package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.rogue.RogueBuffData;
import emu.lunarcore.game.rogue.RogueBuffSelectMenu;
import emu.lunarcore.proto.SelectRogueBuffScRspOuterClass.SelectRogueBuffScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSelectRogueBuffScRsp extends BasePacket {

    public PacketSelectRogueBuffScRsp(RogueBuffData buff, RogueBuffSelectMenu buffSelect) {
        super(CmdId.NONE); // TODO update
        
        var data = SelectRogueBuffScRsp.newInstance()
                .setMazeBuffId(buff.getId())
                .setMazeBuffLevel(buff.getLevel());
        
        if (buffSelect != null) {
            data.setBuffSelectInfo(buffSelect.toProto());
        } else {
            data.getMutableBuffSelectInfo();
        }
        
        this.setData(data);
    }
}
