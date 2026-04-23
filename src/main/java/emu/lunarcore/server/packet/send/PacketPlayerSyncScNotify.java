package emu.lunarcore.server.packet.send;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import emu.lunarcore.LunarCore;
import emu.lunarcore.game.enums.MissionStatusType;
import emu.lunarcore.proto.MissionOuterClass.Mission;
import emu.lunarcore.proto.MissionStatusOuterClass.MissionStatus;
import emu.lunarcore.proto.MissionSyncOuterClass.MissionSync;
import emu.lunarcore.proto.PlayerSyncScNotifyOuterClass.PlayerSyncScNotify;
import emu.lunarcore.server.game.Syncable;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketPlayerSyncScNotify extends BasePacket {

    @Deprecated // This constructor does not create a proto
    private PacketPlayerSyncScNotify() {
        super(CmdId.PlayerSyncScNotify);
    }

    public PacketPlayerSyncScNotify(Syncable object) {
        this();

        var data = PlayerSyncScNotify.newInstance();

        object.onSync(data);

        this.setData(data);
    }

    public PacketPlayerSyncScNotify(Syncable... objects) {
        this();

        var data = PlayerSyncScNotify.newInstance();

        for (var object : objects) {
            object.onSync(data);
        }

        this.setData(data);
    }

    public PacketPlayerSyncScNotify(Collection<? extends Syncable> objects) {
        this();

        var data = PlayerSyncScNotify.newInstance();

        for (var object : objects) {
            object.onSync(data);
        }

        this.setData(data);
    }

    public PacketPlayerSyncScNotify(Map<Integer, MissionStatusType> ongoing, int mainMissionId) {
        this();
        var missionSync = MissionSync.newInstance();

        ongoing.forEach((missionId, status) -> {

            var m = Mission.newInstance()
                    .setId(missionId)
                    .setStatus(MissionStatus.valueOf(status.name()));

            if(status == MissionStatusType.MISSION_FINISH){
                m.setProgress(1);
            }

            missionSync.addMissionList(m);
        });

        missionSync.addMainMissionId(mainMissionId);

        var data = PlayerSyncScNotify.newInstance().setMissionSync(missionSync);
        LunarCore.getLogger().warn("PacketPlayerSyncScNotify sync {}", data.toString());
        this.setData(data);
    }

    public PacketPlayerSyncScNotify(Map<Integer, MissionStatusType> ongoing) {
        this();
        var missionSync = MissionSync.newInstance();
        ongoing.forEach((missionId, status) -> {
            var i = Mission.newInstance()
                    .setId(missionId)
                    .setStatus(MissionStatus.valueOf(status.name()));
            if(status == MissionStatusType.MISSION_FINISH){
                i.setProgress(1);//  TODO: make better
            }
            missionSync.addMissionList(i);
        });
        var data = PlayerSyncScNotify.newInstance().setMissionSync(missionSync);
        LunarCore.getLogger().warn("PacketPlayerSyncScNotify mapin {}", data.toString());
        this.setData(data);
    }

    public PacketPlayerSyncScNotify(List<Integer> next, int done, int progess) {
        this();

        var IdDone = Mission.newInstance()
                .setId(done)
                .setStatus(MissionStatus.MISSION_FINISH);

        if (progess != 0) {
            IdDone.setProgress(progess);
        }

        var missionSync = MissionSync.newInstance()
                .addMissionList(IdDone);

        for (int subMissionId : next) {

            var idNext = Mission.newInstance()
                    .setId(subMissionId)
                    .setStatus(MissionStatus.MISSION_DOING);

            missionSync.addMissionList(idNext);

        }

        var data = PlayerSyncScNotify.newInstance().setMissionSync(missionSync);

        LunarCore.getLogger().warn("PacketPlayerSyncScNotify next {}",data.toString());

        this.setData(data);
    }

    public PacketPlayerSyncScNotify(int next, int done, int progess) {
        this();

        var idNext = Mission.newInstance()
                .setId(next)
                .setStatus(MissionStatus.MISSION_DOING);

        var IdDone = Mission.newInstance()
                .setId(done)
                .setStatus(MissionStatus.MISSION_FINISH);

        if(progess != 0){
            IdDone.setProgress(progess);
        }

        var missionSync = MissionSync.newInstance()
                .addMissionList(idNext)
                .addMissionList(IdDone);

        var data = PlayerSyncScNotify.newInstance().setMissionSync(missionSync);

        LunarCore.getLogger().warn("PacketPlayerSyncScNotify next2 {}",data.toString());

        this.setData(data);
    }

    public PacketPlayerSyncScNotify(int mainMissionId, int subMissionId, MissionStatus missionStatus) {
        this();

        var missionList = Mission.newInstance()
                .setId(subMissionId)
                .setStatus(missionStatus);

        var missionSync = MissionSync.newInstance()
                .addMissionList(missionList)
                .addMainMissionId(mainMissionId);

        var data = PlayerSyncScNotify.newInstance()
                .setMissionSync(missionSync);

        LunarCore.getLogger().warn("PacketPlayerSyncScNotify main and sub {}",data.toString());

        this.setData(data);
    }


    public PacketPlayerSyncScNotify(List<Integer> next, MissionStatus missionStatus, int progess) {
        this();

        var missionSync = MissionSync.newInstance();

        for (int subMissionId : next) {

            var i = Mission.newInstance()
                    .setId(subMissionId)
                    .setStatus(missionStatus);

            if(progess != 0){
                i.setProgress(progess);
            }

            missionSync.addMissionList(i);
        }

        var data = PlayerSyncScNotify.newInstance().setMissionSync(missionSync);

        LunarCore.getLogger().warn("PacketPlayerSyncScNotify next and status {}",data.toString());

        this.setData(data);
    }

    public PacketPlayerSyncScNotify(int mainMissionId, int[] subMissionIds, MissionStatus missionStatus) {
        this();

        var missionSync = MissionSync.newInstance();

        for (int subMissionId : subMissionIds) {
            missionSync.addMissionList(
                    Mission.newInstance()
                    .setId(subMissionId)
                    .setStatus(missionStatus)
                    );
        }

        var data = PlayerSyncScNotify.newInstance()
                .setMissionSync(missionSync);

        this.setData(data);
    }
}
