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
        var archive = data.getMutableArchiveData();

        for (var monsterExcel : GameData.getMonsterExcelMap()) {
            MonsterArchive monsterinfo = MonsterArchive.newInstance()
                    .setMonsterId(monsterExcel.getId())
                    .setNum(1); // todo: add to db

            archive.addMonsterList(monsterinfo);
        }

        for (var relicExcel : GameData.getRelicExcelMap()) {
            RelicArchive relicInfo = RelicArchive.newInstance()
                    .setSlot(relicExcel.getType().getVal())
                    .setRelicId(relicExcel.getSetId()); // todo: add to db

            archive.addRelicList(relicInfo);
        }

        for (var item : GameData.getItemExcelMap()) {
            if (item.isEquipment()) {
                var equipmentExcel = item.getEquipmentExcel();
                archive.addArchiveEquipmentIdList(equipmentExcel.getId());
            }
        }

        this.setData(data);
    }
}
