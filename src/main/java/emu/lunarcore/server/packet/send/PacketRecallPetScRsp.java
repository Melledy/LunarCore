package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.RecallPetScRspOuterClass.RecallPetScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketRecallPetScRsp extends BasePacket {

    public PacketRecallPetScRsp(int petId) {
        super(CmdId.RecallPetScRsp);

        var data = RecallPetScRsp.newInstance()
                .setCurPetId(petId)
                .setNewPetId(0);

        this.setData(data);
    }
}