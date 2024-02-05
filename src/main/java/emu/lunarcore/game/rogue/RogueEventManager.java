package emu.lunarcore.game.rogue;

import emu.lunarcore.LunarCore;
import emu.lunarcore.data.GameData;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.RogueDialogueEventParamOuterClass.RogueDialogueEventParam;
import emu.lunarcore.util.Utils;
import emu.lunarcore.util.WeightedList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.Getter;
import lombok.Setter;

@Getter
public class RogueEventManager {
    private RogueInstance rogueInstance;
    private Player player;
    @Setter private int nowPercentage = 0;
    
    public RogueEventManager(RogueInstance rogueInstance) {
        this.rogueInstance = rogueInstance;
        this.player = rogueInstance.getPlayer();
    }
    
    public int handleEvent(int eventId, int npcId) {
        var event = GameData.getRogueDialogueEventList().get(eventId);
        if (event == null || event.getRogueEffectType() == null) return 0;
        IntArrayList param = event.getRogueEffectParamList();
        
        switch (event.getRogueEffectType()) {
            case GetItem -> rogueInstance.addDialogueMoney(param.getInt(1));
            case TriggerBattle -> {
                this.getPlayer().getServer().getBattleService().startBattle(player, param.getInt(0)); // handle in SceneEnterStageCsReq
            }
            case TriggerRogueMiracleSelect -> this.getRogueInstance().createMiracleSelect(1);
            case TriggerRogueBuffSelect -> {
                this.getRogueInstance().createBuffSelect(param.getInt(2), param.getInt(0));
            }
            case GetRogueBuff -> {
                var rogueBuff = GameData.getRogueBuffGroupExcelMap().get(param.getInt(0));
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
                        param.set(1, param.getInt(1) - 1);
                        if (param.getInt(1) <= 0) break;
                    }
                }
            }
            case GetAllRogueBuffInGroup -> {
                var rogueBuff = GameData.getRogueBuffGroupExcelMap().get(param.getInt(0));
                this.getRogueInstance().addBuff(rogueBuff.getRogueBuffList());
            }
            case TriggerDialogueEventList -> {
                for (var id : param) {
                    this.handleEvent(id, npcId);
                    this.getRogueInstance().getCurDialogueParams().get(npcId).add(RogueDialogueEventParam.newInstance()
                        .setDialogueEventId(id)
                        .setIsValid(true));
                }
            }
            case TriggerRandomEventList -> {
                var weightList = new WeightedList<Integer>();
                var nextEventId = 0;
                for (var id : param) {
                    if (nextEventId == 0) {
                        nextEventId = id;
                        continue;
                    }
                    weightList.add(id, nextEventId);
                    nextEventId = 0;
                }
                int randomEventId = weightList.next();
                handleCost(eventId);
                this.handleEvent(randomEventId, npcId);
                return randomEventId;
            }
            case GetAllRogueBuffInGroupAndGetItem -> {
                var rogueBuff = GameData.getRogueBuffGroupExcelMap().get(param.getInt(0));
                this.getRogueInstance().addBuff(rogueBuff.getRogueBuffList());
                this.getRogueInstance().addDialogueMoney(param.getInt(2));
            }
            case RepeatableGamble -> {
                var failEventId = param.getInt(0);
                var initialPercent = param.getInt(1);
                var increasePercent = param.getInt(2);
                if (this.nowPercentage != 0)
                    this.nowPercentage = initialPercent;
                var weightList = new WeightedList<Integer>();
                for (int i = 3; i < param.size(); i += 2) {
                    weightList.add(param.getInt(i + 1), param.getInt(i));
                }
                int randomNum = Utils.randomRange(0, 100);
                if (randomNum <= this.nowPercentage) {
                    handleCost(eventId);
                    //this.handleEvent(failEventId, npcId);
                    this.getRogueInstance().getCurDialogueParams().get(npcId).add(RogueDialogueEventParam.newInstance()
                        .setDialogueEventId(failEventId)
                        .setIsValid(true));
                    return 0;
                } else {
                    this.nowPercentage += increasePercent;
                    handleCost(eventId);
                    int nextEventId = weightList.next();
                    this.handleEvent(nextEventId, npcId);
                    this.getRogueInstance().getCurDialogueParams().get(npcId).add(RogueDialogueEventParam.newInstance()
                        .setDialogueEventId(nextEventId)
                        .setIsValid(true)
                        .setRatio(this.nowPercentage));  // not working
                    return 0;
                }
            }
            case EnhanceRogueBuff -> {
                var rogueBuff = GameData.getRogueBuffGroupExcelMap().get(param.getInt(0));
                if (rogueBuff != null) {
                    var weightList = new WeightedList<RogueBuffData>();
                    for (var buff : rogueBuff.getRogueBuffList()) {
                        weightList.add(1.0f, buff);
                    }
                    // random param.get(1) times
                    while (true) {
                        var buff = weightList.next();
                        if (buff == null || buff.getExcel() == null) break;
                        if (!this.getRogueInstance().getBuffs().containsValue(buff)) continue;
                        if (this.getRogueInstance().getBuffs().get(buff.getId()).getLevel() >= 2) continue;
                        this.getRogueInstance().addBuff(new RogueBuffData(buff.getId(), buff.getLevel() + 1));
                        param.set(1, param.getInt(1) - 1);
                        if (param.getInt(1) <= 0) break;
                    }
                }
            }
            case NONE -> {}  // do nothing
            default -> {
                LunarCore.getLogger().info("RogueEventManager: unhandled event type: " + event.getRogueEffectType());  // DEBUG
            }
        }
        handleCost(eventId);
        return 0;
    }
    
    public void handleCost(int eventId) {
        var event = GameData.getRogueDialogueEventList().get(eventId);
        if (event == null || event.getCostType() == null) return;
        var param = event.getCostParamList();
        switch (event.getCostType()) {
            case CostItemValue -> rogueInstance.setMoney(rogueInstance.getMoney() - param.getInt(1));
            case CostItemPercent -> rogueInstance.setMoney(rogueInstance.getMoney() - (rogueInstance.getMoney() * param.getInt(1) / 100));
            case CostHpCurrentPercent -> {
                var lineup = this.getPlayer().getCurrentLineup();
                lineup.forEachAvatar(avatar -> {
                    avatar.setCurrentHp(lineup, avatar.getCurrentHp(lineup) - (avatar.getCurrentHp(lineup) * param.getInt(0) / 100));
                });
            }
            case CostHpSpToPercent -> {
                var lineup = this.getPlayer().getCurrentLineup();
                lineup.forEachAvatar(avatar -> {
                    avatar.setCurrentHp(lineup, avatar.getCurrentHp(lineup) - (avatar.getCurrentHp(lineup) * param.getInt(0) / 100));
                    avatar.setCurrentSp(lineup, avatar.getMaxSp() - (avatar.getMaxSp() * param.getInt(1) / 100));
                });
            }
            default -> {}
        }    
    }
}
