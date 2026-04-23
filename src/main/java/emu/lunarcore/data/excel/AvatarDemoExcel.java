package emu.lunarcore.data.excel;

import emu.lunarcore.data.common.ExcelMonsterParam;
import emu.lunarcore.data.resource.GameResource;
import emu.lunarcore.data.resource.ResourceType;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.Getter;

@Getter
@ResourceType(name = {"AvatarDemoConfig.json"})
public class AvatarDemoExcel extends GameResource {
    private int StageID;
    private int AvatarID;
    private IntArrayList TrialAvatarList;

    private int RewardID;
    private int RaidID;
    private int MapEntranceID;

    private int MazeGroupID1;
    private int[] ConfigList1;
    private int[] NpcMonsterIDList1;
    private int[] EventIDList1;

    private transient Int2ObjectMap<ExcelMonsterParam> monsters;

    @Override
    public int getId() {
        return StageID;
    }

    @Override
    public void onLoad() {
        // Cache challenge monsters
        this.monsters = new Int2ObjectOpenHashMap<>();
        for (int i = 0; i < ConfigList1.length; i++) {
            if (ConfigList1[i] == 0) break;

            var monster = new ExcelMonsterParam(ConfigList1[i], NpcMonsterIDList1[i], EventIDList1[i]);
            this.monsters.put(monster.getConfigId(), monster);
        }

        // Clear arrays to save memory
        this.ConfigList1 = null;
        this.NpcMonsterIDList1 = null;
        this.EventIDList1 = null;
    }
}
