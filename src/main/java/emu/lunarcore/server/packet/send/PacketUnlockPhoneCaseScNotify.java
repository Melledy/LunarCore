package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.UnlockPhoneCaseScNotifyOuterClass.UnlockPhoneCaseScNotify;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketUnlockPhoneCaseScNotify extends BasePacket {

    public PacketUnlockPhoneCaseScNotify(int id) {
        super(CmdId.UnlockPhoneCaseScNotify);

        var data = UnlockPhoneCaseScNotify.newInstance()
                .setPhoneCaseId(id);

        this.setData(data);
    }
}
