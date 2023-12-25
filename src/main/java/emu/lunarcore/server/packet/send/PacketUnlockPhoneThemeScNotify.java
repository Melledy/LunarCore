package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.UnlockPhoneThemeScNotifyOuterClass.UnlockPhoneThemeScNotify;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketUnlockPhoneThemeScNotify extends BasePacket {

    public PacketUnlockPhoneThemeScNotify(int id) {
        super(CmdId.UnlockPhoneThemeScNotify);
        
        var data = UnlockPhoneThemeScNotify.newInstance()
                .setThemeId(id);
        
        this.setData(data);
    }
}
