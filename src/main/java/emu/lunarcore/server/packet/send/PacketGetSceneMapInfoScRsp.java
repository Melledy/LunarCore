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
            // Create maze map
            var mazeMap = MazeMapData.newInstance()
                    .setEntryId(entryId);
            
            // Get map entrance excel
            MapEntranceExcel excel = GameData.getMapEntranceExcelMap().get(entryId);
            if (excel == null) {
                data.addMapList(mazeMap);
                continue;
            }
            
            // Get floor info
            FloorInfo floorInfo = GameData.getFloorInfo(excel.getPlaneID(), excel.getFloorID());
            if (floorInfo == null) {
                data.addMapList(mazeMap);
                continue;
            }
            
            // Chest counts
            mazeMap.addUnlockedChestList(MazeChest.newInstance().setMapInfoChestType(MapInfoChestType.MAP_INFO_CHEST_TYPE_NORMAL).setTotalAmountList(1));
            mazeMap.addUnlockedChestList(MazeChest.newInstance().setMapInfoChestType(MapInfoChestType.MAP_INFO_CHEST_TYPE_PUZZLE).setTotalAmountList(1));
            mazeMap.addUnlockedChestList(MazeChest.newInstance().setMapInfoChestType(MapInfoChestType.MAP_INFO_CHEST_TYPE_CHALLENGE).setTotalAmountList(1));
            
            // Add groups (Npc icons on the map, etc)
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
            
            // Lighten sections
            for (int i = 0; i < 100; i++) {
                mazeMap.addAllLightenSectionList(i);
            }
            
            // Add to proto
            data.addMapList(mazeMap);
        }

        this.setData(data);
    }
}
