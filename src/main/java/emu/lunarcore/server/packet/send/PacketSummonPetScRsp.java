package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.SummonPetScRspOuterClass.SummonPetScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSummonPetScRsp extends BasePacket {

    public PacketSummonPetScRsp(int curPetId, int newPetId) {
        super(CmdId.SummonPetScRsp);

        var data = SummonPetScRsp.newInstance()
                .setCurPetId(curPetId)
                .setNewPetId(newPetId);

        this.setData(data);
    }
}