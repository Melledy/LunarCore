package emu.lunarcore.data.config;

import java.util.List;

import lombok.Getter;

/**
 *  Original name: LevelGroupInfo
 */
@Getter
public class GroupInfo {
    private transient int id;
    private GroupLoadSide LoadSide;
    private boolean LoadOnInitial;
    private int OwnerMainMissionID;
    
    private List<AnchorInfo> AnchorList;
    private List<MonsterInfo> MonsterList;
    private List<PropInfo> PropList;
    private List<NpcInfo> NPCList;
    
    public void setId(int id) {
        if (this.id == 0) this.id = id;
    }
    
    public MonsterInfo getMonsterById(int configId) {
        return MonsterList.stream().filter(m -> m.getID() == configId).findFirst().orElse(null);
    }
    
    public static enum GroupLoadSide {
        Client, Server;
    }
}
