package emu.lunarcore.game.drops;

import java.util.ArrayList;
import java.util.List;

import emu.lunarcore.GameConstants;
import emu.lunarcore.data.GameData;
import emu.lunarcore.data.common.ItemParam;
import emu.lunarcore.game.battle.Battle;
import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.game.scene.entity.EntityMonster;
import emu.lunarcore.server.game.BaseGameService;
import emu.lunarcore.server.game.GameServer;
import emu.lunarcore.util.Utils;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

public class DropService extends BaseGameService {

    public DropService(GameServer server) {
        super(server);
    }

    public void calculateDrops(Battle battle) {
        // TODO this isnt the right way drops are calculated on the official server... but its good enough for now
        if (battle.getNpcMonsters().size() == 0) {
            return;
        }
        
        var dropMap = new Int2IntOpenHashMap();
        
        // Get drops from monsters
        for (EntityMonster monster : battle.getNpcMonsters()) {
            var dropExcel = GameData.getMonsterDropExcel(monster.getExcel().getId(), monster.getWorldLevel());
            if (dropExcel == null || dropExcel.getDisplayItemList() == null) {
                continue;
            }
            
            for (ItemParam param : dropExcel.getDisplayItemList()) {
                int id = param.getId();
                int count = Utils.randomRange(0, 3);
                
                if (id == 2) {
                    count = dropExcel.getAvatarExpReward();
                }
                
                dropMap.put(id, count + dropMap.get(id));
            }
        }
        
        for (var entry : dropMap.int2IntEntrySet()) {
            if (entry.getIntValue() <= 0) {
                continue;
            }
            
            // Create item and add it to player
            GameItem item = new GameItem(entry.getIntKey(), entry.getIntValue());

            if (battle.getPlayer().getInventory().addItem(item)) {
                battle.getDrops().add(item);
            }
        }
    }
    
    // TODO filler
    public List<GameItem> calculateDropsFromProp(int propId) {
        List<GameItem> drops = new ArrayList<>();
        
        drops.add(new GameItem(GameConstants.MATERIAL_HCOIN_ID, 5));
        drops.add(new GameItem(GameConstants.TRAILBLAZER_EXP_ID, 5));
        drops.add(new GameItem(GameConstants.MATERIAL_COIN_ID, Utils.randomRange(20, 100)));
        
        return drops;
    }
}
