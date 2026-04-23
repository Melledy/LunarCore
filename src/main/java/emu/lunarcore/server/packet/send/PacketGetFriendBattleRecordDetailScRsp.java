package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.AvatarTypeOuterClass.AvatarType;
import emu.lunarcore.proto.BattleRecord1InfoOuterClass.BattleRecord1Info;
import emu.lunarcore.proto.BattleRecord1OuterClass.BattleRecord1;
import emu.lunarcore.proto.BattleRecordAvatarOuterClass.BattleRecordAvatar;
import emu.lunarcore.proto.BattleRecordAvatarsOuterClass.BattleRecordAvatars;
import emu.lunarcore.proto.BattleRecordType2OuterClass.BattleRecordType2;
import emu.lunarcore.proto.BattleRecordType2OuterClass.BattleRecordType2.RecordDisplayAvatar;
import emu.lunarcore.proto.BattleRecordType2OuterClass.BattleRecordType2.RecordDisplayInfo;
import emu.lunarcore.proto.BattleRecordType2OuterClass.BattleRecordType2.RecordType2;
import emu.lunarcore.proto.DisplayRecordTypeOuterClass.DisplayRecordType;
import emu.lunarcore.proto.FriendBattleRecordType1OuterClass.FriendBattleRecordType1;
import emu.lunarcore.proto.FriendBattleRecordType2OuterClass.FriendBattleRecordType2;
import emu.lunarcore.proto.GetFriendBattleRecordDetailScRspOuterClass.GetFriendBattleRecordDetailScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetFriendBattleRecordDetailScRsp extends BasePacket {

    public static BattleRecordAvatar createBattleAvatar(int id, int index) {
        return BattleRecordAvatar.newInstance()
                .setLevel(80)
                .setId(id)
                .setIndex(index)
                .setAvatarType(AvatarType.AVATAR_FORMAL_TYPE);
    }

    public static RecordDisplayAvatar createRecordDisplayAvatar(int id, int slot) {
        return RecordDisplayAvatar.newInstance()
                .setLevel(80)
                .setId(id)
                .setSlot(slot)
                .setAvatarType(AvatarType.AVATAR_FORMAL_TYPE);
    }

    public PacketGetFriendBattleRecordDetailScRsp(int uid, DisplayRecordType type, int groupId) {
        super(CmdId.GetFriendBattleRecordDetailScRsp);

        var avatarlist1 = BattleRecordAvatars.newInstance()
                .addAllAvatarList(
                        createBattleAvatar(1305, 0),
                        createBattleAvatar(1309, 1),
                        createBattleAvatar(1112, 2),
                        createBattleAvatar(1304, 3)
                        );

        /*
        var avatarlist2 = BattleRecordAvatars.newInstance()
                .addAllAvatarList(
                        createBattleAvatar(1308, 0),
                        createBattleAvatar(1303, 1),
                        createBattleAvatar(1217, 2),
                        createBattleAvatar(1101, 3)
                        );
        */

        var recordInfo = BattleRecord1Info.newInstance()
                .setLevel(12)
                .setRoundCount(17)
                .setBattleRecordStars(2)
                .addAllBattleRecordAvatars(avatarlist1);

        var record = BattleRecord1.newInstance()
                .setRecordId(13)
                .setRecordInfo(recordInfo);

        var withGroup = FriendBattleRecordType1.newInstance()
                .setStarCnt(36)
                .setGroupId(groupId)
                .setRecord1(record);

        var data = GetFriendBattleRecordDetailScRsp.newInstance()
                .setWithGroup(withGroup)
                .setUid(uid);

        this.setData(data);
    }


    public PacketGetFriendBattleRecordDetailScRsp(int uid, DisplayRecordType type) {
        super(CmdId.GetFriendBattleRecordDetailScRsp);

        var displayInfo = RecordDisplayInfo.newInstance()
                .addAllUnkRepeated(
                        63,
                        102,
                        57,
                        62,
                        13,
                        26,
                        12,
                        123,
                        104
                        )
                .addAllAvatarList(
                        createRecordDisplayAvatar(1315, 0),
                        createRecordDisplayAvatar(1309, 1),
                        createRecordDisplayAvatar(1208, 2),
                        createRecordDisplayAvatar(1306, 3)
                        );

        var record = RecordType2.newInstance()
                .setScore(7500)
                .setIsWin(true)
                .setInt1(13)
                .setInt2(183)
                .setDisplayInfo(displayInfo);

        var recordtype2 = BattleRecordType2.newInstance()
                .setRecord(record)
                .setRecordId(804);

        var noGroup = FriendBattleRecordType2.newInstance()
                .setRecordType2(recordtype2);

        var data = GetFriendBattleRecordDetailScRsp.newInstance()
                .setNoGroup(noGroup)
                .setUid(uid);

        this.setData(data);
    }
}
