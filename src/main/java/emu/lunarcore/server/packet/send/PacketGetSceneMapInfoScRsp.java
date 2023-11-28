package emu.lunarcore.server.packet.send;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.config.FloorInfo;
import emu.lunarcore.data.config.GroupInfo;
import emu.lunarcore.data.excel.MapEntranceExcel;
import emu.lunarcore.game.enums.PropState;
import emu.lunarcore.proto.GetSceneMapInfoScRspOuterClass.GetSceneMapInfoScRsp;
import emu.lunarcore.proto.MapInfoChestTypeOuterClass.MapInfoChestType;
import emu.lunarcore.proto.MazeChestOuterClass.MazeChest;
import emu.lunarcore.proto.MazeGroupOuterClass.MazeGroup;
import emu.lunarcore.proto.MazeMapDataOuterClass.MazeMapData;
import emu.lunarcore.proto.MazePropOuterClass.MazeProp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;
import us.hebi.quickbuf.RepeatedInt;

public class PacketGetSceneMapInfoScRsp extends BasePacket {

    public PacketGetSceneMapInfoScRsp(RepeatedInt list) {
        super(CmdId.GetSceneMapInfoScRsp);

        var data = GetSceneMapInfoScRsp.newInstance();

        for (int entryId : list) {
            var mazeMap = MazeMapData.newInstance()
                    .addUnlockedChestList(MazeChest.newInstance().setMapInfoChestType(MapInfoChestType.MAP_INFO_CHEST_TYPE_NORMAL))
                    .addUnlockedChestList(MazeChest.newInstance().setMapInfoChestType(MapInfoChestType.MAP_INFO_CHEST_TYPE_PUZZLE))
                    .addUnlockedChestList(MazeChest.newInstance().setMapInfoChestType(MapInfoChestType.MAP_INFO_CHEST_TYPE_CHALLENGE))
                    .setEntryId(entryId);
            
            // Map sections. TODO un hardcode
            for (int i = 0; i < 100; i++) {
                mazeMap.addAllLightenSectionList(i);
            }

            // Maze groups (Npc icons on the map, etc)
            MapEntranceExcel excel = GameData.getMapEntranceExcelMap().get(entryId);
            if (excel != null) {
                FloorInfo floorInfo = GameData.getFloorInfo(excel.getPlaneID(), excel.getFloorID());
                if (floorInfo != null) {
                    // Add groups
                    for (GroupInfo groupInfo : floorInfo.getGroups().values()) {
                        var mazeGroup = MazeGroup.newInstance().setGroupId(groupInfo.getId());
                        mazeMap.addMazeGroupList(mazeGroup);
                    }
                    // Map unlocked teleports
                    for (var teleport : floorInfo.getCachedTeleports().values()) {
                        mazeMap.addAllUnlockedTeleportList(teleport.getMappingInfoID());
                    }
                    // Map unlocked checkpoints that are not unlocked normally
                    for (var prop : floorInfo.getUnlockedCheckpoints()) {
                        var mazeProp = MazeProp.newInstance()
                                .setGroupId(prop.getAnchorGroupID())
                                .setConfigId(prop.getID())
                                .setState(PropState.CheckPointEnable.getVal());
                        
                        mazeMap.addMazePropList(mazeProp);
                    }
                }
            }
            
            data.addMapList(mazeMap);
        }

        this.setData(data);
    }
}
