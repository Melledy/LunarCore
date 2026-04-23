package emu.lunarcore.server.packet.send;

import emu.lunarcore.data.GameData;
import emu.lunarcore.proto.GetOfferingInfoScRspOuterClass.GetOfferingInfoScRsp;
import emu.lunarcore.proto.OfferingInfoOuterClass.OfferingInfo;
import emu.lunarcore.proto.OfferingStateOuterClass.OfferingState;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;
import us.hebi.quickbuf.RepeatedInt;

public class PacketGetOfferingInfoScRsp extends BasePacket {

    public PacketGetOfferingInfoScRsp(RepeatedInt list) {
        super(CmdId.GetOfferingInfoScRsp);

        var data = GetOfferingInfoScRsp.newInstance();

        for (int id : list) {
            var excel = GameData.getOfferingTypeExcelMap().get(id);
            
            if (excel == null) {
                // End packet if client sends an invalid offering so that we dont get spammed by malicious packets
                data.setRetcode(1);
                this.setData(data);
                return;
            }

            var info = OfferingInfo.newInstance()
                    .setOfferingId(excel.getId())
                    .setOfferingLevel(excel.getMaxLevel())
                    .setOfferingState(OfferingState.OFFERING_STATE_OPEN);

            data.addOfferingInfoList(info);
        }

        this.setData(data);
    }
}
