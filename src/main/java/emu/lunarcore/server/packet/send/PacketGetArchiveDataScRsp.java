package emu.lunarcore.server.packet.send;

import emu.lunarcore.data.GameData;
import emu.lunarcore.proto.GetArchiveDataScRspOuterClass.GetArchiveDataScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetArchiveDataScRsp extends BasePacket {

    // TODO cache packet
    public PacketGetArchiveDataScRsp() {
        super(CmdId.GetArchiveDataScRsp);
        
        var data = GetArchiveDataScRsp.newInstance();
        
        for (var avatarExcel : GameData.getAvatarExcelMap().values()) {
            data.getMutableArchiveData().addArchiveAvatarIdList(avatarExcel.getAvatarID());
        }
        
        for (var itemExcel : GameData.getItemExcelMap().values()) {
            if (!itemExcel.isEquipment()) continue;
            data.getMutableArchiveData().addAllArchiveEquipmentIdList(itemExcel.getId());
        }
        
        this.setData(data);
    }
}
