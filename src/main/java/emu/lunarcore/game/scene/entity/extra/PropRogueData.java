package emu.lunarcore.game.scene.entity.extra;

import emu.lunarcore.proto.PropExtraInfoOuterClass.PropExtraInfo;
import emu.lunarcore.proto.PropRogueInfoOuterClass.PropRogueInfo;
import lombok.Getter;

@Getter
public class PropRogueData {
    private int roomId;
    private int siteId;
    
    public PropRogueData(int roomId, int siteId) {
        this.roomId = roomId;
        this.siteId = siteId;
    }

    public PropExtraInfo toProto() {
        var data = PropRogueInfo.newInstance()
                .setRoomId(this.getRoomId())
                .setSiteId(this.getSiteId());

        return PropExtraInfo.newInstance().setRogueInfo(data);
    }
    
}
