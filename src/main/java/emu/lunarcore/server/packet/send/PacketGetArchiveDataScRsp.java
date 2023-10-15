package emu.lunarcore.server.packet.send;

import emu.lunarcore.data.GameData;
import emu.lunarcore.proto.ArchiveDataOuterClass.ArchiveData;
import emu.lunarcore.proto.GetArchiveDataScRspOuterClass.GetArchiveDataScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetArchiveDataScRsp extends BasePacket {

    public PacketGetArchiveDataScRsp() {
        super(CmdId.GetArchiveDataScRsp);

        var archive = ArchiveData.newInstance();
        
        for (var avatarExcel : GameData.getAvatarExcelMap().values()) {
            archive.addArchiveAvatarIdList(avatarExcel.getAvatarID());
        }
        
        var data = GetArchiveDataScRsp.newInstance().setArchiveData(archive);
        
        this.setData(data);
    }
}
