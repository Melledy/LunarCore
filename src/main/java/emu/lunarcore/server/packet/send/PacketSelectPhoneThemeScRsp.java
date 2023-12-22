package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.SelectPhoneThemeScRspOuterClass.SelectPhoneThemeScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSelectPhoneThemeScRsp extends BasePacket {
    
    public PacketSelectPhoneThemeScRsp() {
        super(CmdId.SelectPhoneThemeScRsp);

        var data = SelectPhoneThemeScRsp.newInstance()
            .setRetcode(1);
        
        this.setData(data);
    }

    public PacketSelectPhoneThemeScRsp(int themeId) {
        super(CmdId.SelectPhoneThemeScRsp);

        var data = SelectPhoneThemeScRsp.newInstance()
            .setCurPhoneTheme(themeId); 
        
        this.setData(data);
    }
}
