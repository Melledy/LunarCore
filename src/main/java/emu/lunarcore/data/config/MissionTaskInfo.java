package emu.lunarcore.data.config;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;

@Getter
public class MissionTaskInfo {
    // @SerializedName("OnInitSequece")
    // private OnInitSequece[] onInitSequece;

    @SerializedName("OnStartSequece")
    private OnStartSequece[] onStartSequece;

    @SerializedName("Type")
    private String type;

    @Getter
    public static class OnStartSequece {
        @SerializedName("Order")
        private int order;

        @SerializedName("TaskList")
        private TaskList[] taskList;

        @Getter
        public static class TaskList {
            @SerializedName("$type")
            private String type;

            //@SerializedName("ValueSource")
            //private Object valueSource;

            @SerializedName("PerformanceType")
            private String PerformanceType;

            @SerializedName("PerformanceID")
            private int performanceId;

            @SerializedName("Key")
            private String key;

            @SerializedName("EntranceID")
            private int entranceId;

            // need dynamic class
            //@SerializedName("GroupID")
            //private int groupId;

            @SerializedName("AnchorID")
            private int anchorId;
        }
    }
}