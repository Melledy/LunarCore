package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.GenderOuterClass.Gender;
import emu.lunarcore.proto.GetHeroBasicTypeInfoScRspOuterClass.GetHeroBasicTypeInfoScRsp;
import emu.lunarcore.proto.HeroBasicTypeInfoOuterClass.HeroBasicTypeInfo;
import emu.lunarcore.proto.HeroBasicTypeOuterClass.HeroBasicType;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetHeroBasicTypeInfoScRsp extends BasePacket {

    public PacketGetHeroBasicTypeInfoScRsp() {
        super(CmdId.GetHeroBasicTypeInfoScRsp);

        var heroBasicType = HeroBasicTypeInfo.newInstance()
                .setBasicType(HeroBasicType.BoyWarrior);

        var data = GetHeroBasicTypeInfoScRsp.newInstance()
                .setGender(Gender.GenderMan)
                .setCurBasicType(HeroBasicType.BoyWarrior)
                .addBasicTypeInfoList(heroBasicType);

        this.setData(data);
    }
}
