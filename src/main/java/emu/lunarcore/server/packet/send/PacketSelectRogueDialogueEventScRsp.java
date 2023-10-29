package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.SelectRogueDialogueEventScRspOuterClass.SelectRogueDialogueEventScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSelectRogueDialogueEventScRsp extends BasePacket {

    public PacketSelectRogueDialogueEventScRsp(int dialogueEventId) {
        super(CmdId.SelectRogueDialogueEventScRsp);
        
        var data = SelectRogueDialogueEventScRsp.newInstance()
                .setDialogueEventId(dialogueEventId);
        
        this.setData(data);
    }
}
