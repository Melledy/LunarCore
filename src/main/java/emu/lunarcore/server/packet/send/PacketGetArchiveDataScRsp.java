package emu.lunarcore.server.packet.send;

import emu.lunarcore.data.GameData;
import emu.lunarcore.proto.GetArchiveDataScRspOuterClass.GetArchiveDataScRsp;
import emu.lunarcore.proto.MonsterArchiveOuterClass.MonsterArchive;
import emu.lunarcore.proto.RelicArchiveOuterClass.RelicArchive;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CacheablePacket;
import emu.lunarcore.server.packet.CmdId;

@CacheablePacket
public class PacketGetArchiveDataScRsp extends BasePacket {

    public PacketGetArchiveDataScRsp() {
        super(CmdId.GetArchiveDataScRsp);
        
        var data = GetArchiveDataScRsp.newInstance();
        var archiveData = data.getMutableArchiveData();
        
        for (var avatarExcel : GameData.getAvatarExcelMap().values()) {
            archiveData.addArchiveAvatarIdList(avatarExcel.getAvatarID());
        }

        for (var monsterExcel : GameData.getMonsterExcelMap().values()) {
            MonsterArchive monsterinfo = MonsterArchive.newInstance()
                .setMonsterId(monsterExcel.getId())
                .setNum(1); // todo: add to db

            archiveData.addArchiveMonsterIdList(monsterinfo);
        }

        for (var relicExcel : GameData.getRelicExcelMap().values()) {
            RelicArchive relicInfo = RelicArchive.newInstance()
                .setSlot(relicExcel.getType().getVal())
                .setRelicId(relicExcel.getId()); // todo: add to db

            archiveData.addRelicList(relicInfo);
        }
        
        for (var equipmentExcel : GameData.getEquipExcelMap().values()) {
            archiveData.addAllArchiveEquipmentIdList(equipmentExcel.getId());
        }
        
        this.setData(data);
    }
}
