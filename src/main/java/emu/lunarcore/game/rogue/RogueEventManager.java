package emu.lunarcore.game.rogue;

import emu.lunarcore.data.GameData;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.util.WeightedList;
import lombok.Getter;

import java.util.List;

@Getter
public class RogueEventManager {
    private RogueInstance rogueInstance;
    private Player player;
    
    public RogueEventManager(RogueInstance rogueInstance) {
        this.rogueInstance = rogueInstance;
        this.player = rogueInstance.getPlayer();
    }
    
    public void handleEvent(int eventId) {
        var event = GameData.getRogueDialogueEventList().get(eventId);
        if (event == null || event.getRogueEffectType() == null) return;
        List<Integer> param = event.getRogueEffectParamList();
        switch (event.getRogueEffectType()) {
            case GetItem -> rogueInstance.setMoney(rogueInstance.getMoney() + param.get(1));
            case TriggerBattle -> this.getPlayer().getServer().getBattleService().startBattle(player, param.get(0));
            case TriggerRogueMiracleSelect -> this.getRogueInstance().createMiracleSelect(1);
            case TriggerRogueBuffSelect -> this.getRogueInstance().createBuffSelect(1);
            case GetRogueBuff -> {
                var rogueBuff = GameData.getRogueBuffGroupExcelMap().get(param.get(0));
                if (rogueBuff != null) {
                    var weightList = new WeightedList<RogueBuffData>();
                    for (var buff : rogueBuff.getRogueBuffList()) {
                        weightList.add(1.0f, buff);
                    }
                    // random param.get(1) times
                    while (true) {
                        var buff = weightList.next();
                        if (buff == null || buff.getExcel() == null) break;
                        if (this.getRogueInstance().getBuffs().containsValue(buff)) continue;
                        this.getRogueInstance().addBuff(buff);
                        param.set(1, param.get(1) - 1);
                        if (param.get(1) <= 0) break;
                    }
                }
            }
            case GetAllRogueBuffInGroup -> {
                var rogueBuff = GameData.getRogueBuffGroupExcelMap().get(param.get(0));
                this.getRogueInstance().addBuff(rogueBuff.getRogueBuffList());
            }
        }
        handleCost(eventId);
    }
    
    public void handleCost(int eventId) {
        var event = GameData.getRogueDialogueEventList().get(eventId);
        if (event == null || event.getCostType() == null) return;
        var param = event.getCostParamList();
        switch (event.getCostType()) {
            case CostItemValue -> rogueInstance.setMoney(rogueInstance.getMoney() - param.get(1));
            case CostItemPercent -> rogueInstance.setMoney(rogueInstance.getMoney() - (rogueInstance.getMoney() * param.get(1) / 100));
            case CostHpCurrentPercent -> {
                var lineup = this.getPlayer().getCurrentLineup();
                lineup.forEachAvatar(avatar -> {
                    avatar.setCurrentHp(lineup, avatar.getCurrentHp(lineup) - (avatar.getCurrentHp(lineup) * param.get(0) / 100));
                });
            }
            case CostHpSpToPercent -> {
                var lineup = this.getPlayer().getCurrentLineup();
                lineup.forEachAvatar(avatar -> {
                    avatar.setCurrentHp(lineup, avatar.getCurrentHp(lineup) - (avatar.getCurrentHp(lineup) * param.get(0) / 100));
                    avatar.setCurrentSp(lineup, avatar.getMaxSp() - (avatar.getMaxSp() * param.get(1) / 100));
                });
            }
        }    
    }
}
