package emu.lunarcore.game.drops;

import emu.lunarcore.GameConstants;
import emu.lunarcore.data.GameData;
import emu.lunarcore.game.battle.Battle;
import emu.lunarcore.game.enums.ItemRarity;
import emu.lunarcore.game.inventory.ItemParamMap;
import emu.lunarcore.game.scene.entity.EntityMonster;
import emu.lunarcore.server.game.BaseGameService;
import emu.lunarcore.server.game.GameServer;
import emu.lunarcore.util.Utils;

public class DropService extends BaseGameService {
    private int[] baseRelicSets = new int[] { 
        1020, 1110, // World 1-3
        1150, 1180, // World 4
        1220, 1240  // World 5
    };

    public DropService(GameServer server) {
        super(server);
    }
    
    private int getRandomRelicId(ItemRarity rarity) {
        // TODO get relic set by world id
        int baseRelicSet = Utils.randomElement(baseRelicSets);
        int relicId = ((rarity.getVal() + 1) * 10000) + baseRelicSet + Utils.randomRange(1, 4);
        return relicId;
    }

    // TODO this isnt the right way drops are calculated on the official server... but its good enough for now
    public void calculateDrops(Battle battle) {
        // Setup drop map
        var dropMap = new ItemParamMap();
        
        // Calculate drops from monsters
        for (EntityMonster monster : battle.getNpcMonsters()) {
            monster.calculateDrops(dropMap);
        }
        
        // Mapping info
        if (battle.getMappingInfoId() > 0) {
            var mapInfoExcel = GameData.getMappingInfoExcelMap().get(battle.getMappingInfoId(), battle.getWorldLevel());
            if (mapInfoExcel != null) {
                int rolls = Math.max(battle.getCocoonWave(), 1);
                for (var dropParam : mapInfoExcel.getDropList()) {
                    for (int i = 0; i < rolls; i++) {
                        dropParam.roll(dropMap);
                    }
                }
            }
        }
        
        // Sanity check
        if (dropMap.size() == 0) {
            return;
        }
        
        // Create drops
        dropMap.forEachItem(item -> {
            battle.getDrops().add(item);
        });
        
        // Add to inventory
        battle.getPlayer().getInventory().addItems(battle.getDrops());
    }
    
    // TODO filler
    public ItemParamMap calculateChestDrops(int worldId, int propId) {
        // Get world number
        int worldIndex = (int) Math.floor(worldId / 100) - 1;
        
        // Setup drop map
        var drops = new ItemParamMap();
        
        // Setup reward amounts
        int expReward = 5;
        int coinReward = 500;
        
        // Change reward amounts based on chest type
        int propIndex = propId % 10;
        if (propIndex == 2) {
            expReward = 20;
            coinReward = 2500;
        } else if (propIndex == 3) {
            expReward = 30;
            coinReward = 0;
        }
        
        // Exp
        drops.add(GameConstants.MATERIAL_HCOIN_ID, expReward);
        drops.add(GameConstants.TRAILBLAZER_EXP_ID, expReward);
        drops.add(212, 3); // Avatar exp material
        drops.add(232, 1); // Relic exp material
        
        // Relics
        for (int i = 0; i < 2; i++) {
            int relicId = this.getRandomRelicId(ItemRarity.Rare);
            drops.add(relicId, 1);
        }
        
        // Offering materials
        int offeringItemId = 120000 + worldIndex;
        drops.add(offeringItemId, 15);
        
        // Coins
        drops.add(GameConstants.MATERIAL_COIN_ID, coinReward);
        
        return drops;
    }
}
