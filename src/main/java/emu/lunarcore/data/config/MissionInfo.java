package emu.lunarcore.data.config;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
public class MissionInfo {
    @SerializedName("MainMissionID")
    private int mainMissionID;
    
    @SerializedName("StartSubMissionList")
    private int[] startSubMissionList;
    
    @SerializedName("FinishSubMissionList")
    private int[] finishSubMissionList;
    
    @SerializedName("SubMissionList")
    private SubMissionInfo[] subMissionList;

    @SerializedName("MissionCustomValueList")
    private MissionCustomValueInfo[] missionCustomValueList;

    @SerializedName("CustomValueSaveIndexList")
    private String[] missionCustomValueSaveIndexList;

    @Getter
    @Setter
    public static class SubMissionInfo {
        @SerializedName("ID")
        private int id;

        @SerializedName("MainMissionID")
        private int mainMissionID;

        @SerializedName("TakeType")
        private String takeType;

        @SerializedName("TakeParamIntList")
        private Integer[] takeParamIntList;

        @SerializedName("FinishType")
        private String finishType;

        @SerializedName("ParamInt1")
        private int paramInt1;

        @SerializedName("ParamInt2")
        private int paramInt2;        

        @SerializedName("ParamInt3")
        private int paramInt3;

        @SerializedName("LevelFloorID")
        private int levelFloorID;

        @Setter private MissionTaskInfo task;
    }

    @Getter
    public static class MissionCustomValueInfo {
        @SerializedName("Index")
        private Integer index = 0;

        @SerializedName("Name")
        private String name;

        @SerializedName("Type")
        private String type;

        @SerializedName("ValidValueParamList")
        private Integer[] validValueParamList;
    }


    @Getter
    public static class MissionSimpleInfo {
        private int mainMissionID;
        private int[] startSubMissionList;
        private int[] finishSubMissionList;
        private SubMissionInfo[] subMissionList;
        private MissionCustomValueInfo[] missionCustomValueList;
        private String[] missionCustomValueSaveIndexList;
    }

    /**
     * Finds a sub-mission by its ID.
     *
     * @param id The ID of the sub-mission to find.
     * @return The SubMissionInfo object with the matching ID, or null if not found.
     */
    public SubMissionInfo getSubMissionById(int id) {
        for (SubMissionInfo subMission : subMissionList) {
            if (subMission.getId() == id) {
                return subMission;
            }
        }
        return null; // Return null if no matching sub-mission is found
    }
}