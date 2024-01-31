package emu.lunarcore.game.rogue;

import emu.lunarcore.data.GameData;
import emu.lunarcore.game.player.Player;
import lombok.Getter;

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
        var param = event.getRogueEffectParamList();
        switch (event.getRogueEffectType()) {
            case GetItem -> rogueInstance.setMoney(rogueInstance.getMoney() + param.get(1));
            case TriggerBattle -> rogueInstance.createBuffSelect(3);  //this.getPlayer().getServer().getBattleService().startBattle(player, param.get(0));  // NOT WORKING
            case TriggerRogueMiracleSelect -> this.getRogueInstance().createMiracleSelect(1);
            case TriggerRogueBuffSelect -> this.getRogueInstance().createBuffSelect(1);
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
