package emu.lunarcore.server.packet.send;

import emu.lunarcore.data.GameData;
import emu.lunarcore.proto.GetArchiveDataScRspOuterClass.GetArchiveDataScRsp;
import emu.lunarcore.proto.MonsterArchiveOuterClass.MonsterArchive;
import emu.lunarcore.proto.RelicArchiveOuterClass.RelicArchive;
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

        for (var MonsterId : GameData.getAllMonsterIds()) {
            MonsterArchive monsterinfo = MonsterArchive.newInstance()
                .setMonsterId(MonsterId)
                .setNum(1); // todo: add to db

            data.getMutableArchiveData().addArchiveMonsterIdList(monsterinfo);
        }

        for (var RelicId : GameData.getAllRelicIds()) {
            RelicArchive relicInfo = RelicArchive.newInstance()
                .setType(GameData.getRelicTypeFromId(RelicId))
                .setRelicId(GameData.getRelicSetFromId(RelicId)); // todo: add to db

            data.getMutableArchiveData().addArchiveRelicList(relicInfo);
        }
        
        for (var itemExcel : GameData.getItemExcelMap().values()) {
            if (!itemExcel.isEquipment()) continue;
            data.getMutableArchiveData().addAllArchiveEquipmentIdList(itemExcel.getId());
        }
        
        this.setData(data);
    }
}
