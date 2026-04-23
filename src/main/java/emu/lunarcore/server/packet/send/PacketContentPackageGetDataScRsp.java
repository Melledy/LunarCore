package emu.lunarcore.server.packet.send;

import emu.lunarcore.data.GameData;
import emu.lunarcore.proto.ContentPackageGetDataScRspOuterClass.ContentPackageGetDataScRsp;
import emu.lunarcore.proto.ContentPackageInfoOuterClass.ContentPackageInfo;
import emu.lunarcore.proto.ContentPackageStatusOuterClass.ContentPackageStatus;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketContentPackageGetDataScRsp extends BasePacket {

    public PacketContentPackageGetDataScRsp() {
        super(CmdId.ContentPackageGetDataScRsp);

        var proto = ContentPackageGetDataScRsp.newInstance();
        var data = proto.getMutableData();

        // Add content packages from excels
        for (var excel : GameData.getContentPackageExcelMap().values()) {
            var contentPackage = ContentPackageInfo.newInstance()
                    .setContentId(excel.getId())
                    .setStatus(ContentPackageStatus.ContentPackageStatus_Finished);

            data.addContentPackageList(contentPackage);

            // Hacky way to set current content
            if (data.getCurContentId() == 0) {
                data.setCurContentId(excel.getId());
            }
        }

        this.setData(proto);
    }
}
