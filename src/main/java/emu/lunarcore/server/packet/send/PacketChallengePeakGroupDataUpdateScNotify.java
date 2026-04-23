package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.ChallengePeakGroupDataUpdateScNotifyOuterClass.ChallengePeakGroupDataUpdateScNotify;
import emu.lunarcore.proto.ChallengePeakLevelInfoOuterClass.ChallengePeakLevelInfo;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntList;

public class PacketChallengePeakGroupDataUpdateScNotify extends BasePacket {

    public PacketChallengePeakGroupDataUpdateScNotify(int groupId, Int2ObjectMap<IntList> lineupAvatars) {
        super(CmdId.ChallengePeakGroupDataUpdateScNotify);

        var data = ChallengePeakGroupDataUpdateScNotify.newInstance();
        var group = data.getMutableChallengePeakGroup()
                .setChallengePeakGroupId(groupId);
        
        for (var info : lineupAvatars.int2ObjectEntrySet()) {
            var level = ChallengePeakLevelInfo.newInstance()
                    .setChallengePeakId(info.getIntKey());
            
            for (int id : info.getValue()) {
                level.addChallengeAvatarIdList(id);
            }
            
            group.addChallengePeakLevelList(level);
        }
        
        this.setData(data);
    }
}
