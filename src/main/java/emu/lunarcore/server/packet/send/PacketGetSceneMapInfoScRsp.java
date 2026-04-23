package emu.lunarcore.server.packet.send;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.config.FloorInfo;
import emu.lunarcore.data.config.GroupInfo;
import emu.lunarcore.data.excel.MapEntranceExcel;
import emu.lunarcore.game.enums.PropState;
import emu.lunarcore.proto.GetSceneMapInfoScRspOuterClass.GetSceneMapInfoScRsp;
import emu.lunarcore.proto.MapInfoChestTypeOuterClass.MapInfoChestType;
import emu.lunarcore.proto.MapInfoGroupOuterClass.MapInfoGroup;
import emu.lunarcore.proto.MapPropInfoOuterClass.MapPropInfo;
import emu.lunarcore.proto.MazeChestOuterClass.MazeChest;
import emu.lunarcore.proto.SceneIdentifierInfoOuterClass.SceneIdentifierInfo;
import emu.lunarcore.proto.SceneMapInfoOuterClass.SceneMapInfo;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import us.hebi.quickbuf.RepeatedMessage;

public class PacketGetSceneMapInfoScRsp extends BasePacket {

    public PacketGetSceneMapInfoScRsp(RepeatedMessage<SceneIdentifierInfo> list) {
        super(CmdId.GetSceneMapInfoScRsp);

        var data = GetSceneMapInfoScRsp.newInstance();

        // Map entries
        var entrances = new ObjectOpenHashSet<MapEntranceExcel>();
        var floorSet = new IntOpenHashSet();
        
        // Add entry ids from floor ids TODO optimize
        for (var info : list) {
            floorSet.add(info.getFloorId());
        }
        
        for (var excel : GameData.getMapEntranceExcelMap()) {
            if (floorSet.contains(excel.getFloorID())) {
                entrances.add(excel);
            }
        }

        // Get maze map infos from entries
        for (var excel : entrances) {
            // Create map info
            var mapInfo = SceneMapInfo.newInstance()
                    .setEntryId(excel.getId())
                    .setDimensionId(0);

            // Set floor id
            mapInfo.setFloorId(excel.getFloorID());
            
            // Set scene identifier info
            mapInfo.getMutableSceneIdentifier()
                .setFloorId(excel.getFloorID());

            // Map sections. TODO un hardcode
            for (int i = 0; i < 1000; i++) {
                mapInfo.addLightenSectionList(i);
            }
            
            int chestAmount = 0;
            int puzzleAmount = 0;

            var mazeChest = MazeChest.newInstance()
                    .setMapInfoChestType(MapInfoChestType.MAP_INFO_CHEST_TYPE_NORMAL)
                    .setTotalAmountList(chestAmount)
                    .setUnlockedAmountList(chestAmount);
            mapInfo.addChestList(mazeChest);

            var puzzleChest = MazeChest.newInstance()
                    .setMapInfoChestType(MapInfoChestType.MAP_INFO_CHEST_TYPE_PUZZLE)
                    .setTotalAmountList(puzzleAmount)
                    .setUnlockedAmountList(puzzleAmount);
            mapInfo.addChestList(puzzleChest);

            // Maze groups (Npc icons on the map, etc)
            FloorInfo floorInfo = GameData.getFloorInfo(excel.getPlaneID(), excel.getFloorID());
            if (floorInfo != null) {
                // Add groups
                for (GroupInfo groupInfo : floorInfo.getGroups().values()) {
                    var mazeGroup = MapInfoGroup.newInstance().setGroupId(groupInfo.getId());
                    mapInfo.addGroupList(mazeGroup);
                }
                // Map unlocked teleports
                for (var teleport : floorInfo.getCachedTeleports().values()) {
                    mapInfo.addUnlockTeleportList(teleport.getMappingInfoID());
                }
                // Map unlocked checkpoints that are not unlocked normally
                for (var prop : floorInfo.getUnlockedCheckpoints()) {
                    var mazeProp = MapPropInfo.newInstance()
                            .setGroupId(prop.getAnchorGroupID())
                            .setConfigId(prop.getID())
                            .setState(PropState.CheckPointEnable.getVal());

                    mapInfo.addMapInfoPropList(mazeProp);
                }
            }

            data.addSceneMapInfoList(mapInfo);
        }

        this.setData(data);
    }
}