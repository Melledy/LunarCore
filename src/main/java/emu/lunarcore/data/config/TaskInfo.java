package emu.lunarcore.data.config;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Original name: TaskConfig
 */
@Getter
public class TaskInfo {
    @Getter(AccessLevel.NONE)
    private String $type;
    
    @SerializedName(value = "ID", alternate = {"SummonUnitID"})
    private int ID;
    
    private boolean TriggerBattle = true;
    private DynamicFloat LifeTime;
    
    @SerializedName(value = "OnAttack", alternate = {"OnBattle"})
    private List<TaskInfo> OnAttack;
    private List<TaskInfo> SuccessTaskList;
    private List<TaskInfo> OnProjectileHit;
    private List<TaskInfo> OnProjectileLifetimeFinish;
    
    public String getType() {
        return this.$type;
    }
    
    public int getLifeTime() {
        if (this.LifeTime == null) {
            return 20; // TODO change
        }
        
        return (int) this.LifeTime.getValue();
    }
}
