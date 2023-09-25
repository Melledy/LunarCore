package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.SetGameplayBirthdayScRspOuterClass.SetGameplayBirthdayScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSetGameplayBirthdayScRsp extends BasePacket {

    public PacketSetGameplayBirthdayScRsp() {
        super(CmdId.SetGameplayBirthdayScRsp);

        var data = SetGameplayBirthdayScRsp.newInstance()
                .setRetcode(1);
        
        this.setData(data);
    }
    
    public PacketSetGameplayBirthdayScRsp(int birthday) {
        super(CmdId.SetGameplayBirthdayScRsp);

        var data = SetGameplayBirthdayScRsp.newInstance()
                .setBirthday(birthday);
        
        this.setData(data);
    }
}
