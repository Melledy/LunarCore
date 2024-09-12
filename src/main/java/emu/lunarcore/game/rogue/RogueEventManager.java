package emu.lunarcore.game.rogue;

import emu.lunarcore.LunarCore;
import emu.lunarcore.data.GameData;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.game.rogue.event.RogueEventResultInfo;
import emu.lunarcore.server.packet.send.PacketSyncRogueCommonDialogueOptionFinishScNotify;
import emu.lunarcore.util.Utils;
import emu.lunarcore.util.WeightedList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.Getter;

@Getter
public class RogueEventManager {
    private RogueInstance rogueInstance;
    private Player player;
    
    public RogueEventManager(RogueInstance rogueInstance) {
        this.rogueInstance = rogueInstance;
        this.player = rogueInstance.getPlayer();
    }
    
    public int handleEvent(int optionId, int eventUniqueId) {
        var event = GameData.getRogueDialogueEventList().get(optionId);
        if (event == null || event.getRogueEffectType() == null) return 0;
        IntArrayList param = event.getRogueEffectParamList();
        var instance = this.getRogueInstance().getRunningEvents().get(eventUniqueId);
        boolean isEvent = false;
        if (instance != null && instance.SelectedOptionId == 0) {
            instance.SelectedOptionId = optionId;
            isEvent = true;
        }
        
        switch (event.getRogueEffectType()) {
            case GetItem -> rogueInstance.addDialogueCoin(param.getInt(1));
            case TriggerBattle -> {
                var battleId = param.getInt(0);
                var option = this.getRogueInstance().getRunningEvents().get(eventUniqueId).Options.stream().filter(x -> x.OptionId == optionId).findFirst();
                if (option.isEmpty()) return 0;
                var result = new RogueEventResultInfo();
                result.BattleEventId = battleId;
                option.get().Results.add(result);
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
                    this.handleEvent(id, eventUniqueId);
                    this.getRogueInstance().getRunningEvents().get(eventUniqueId).EffectEventId.add(id);
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
                handleCost(optionId);
                this.handleEvent(randomEventId, eventUniqueId);
                return randomEventId;
            }
            case GetAllRogueBuffInGroupAndGetItem -> {
                var rogueBuff = GameData.getRogueBuffGroupExcelMap().get(param.getInt(0));
                this.getRogueInstance().addBuff(rogueBuff.getRogueBuffList());
                this.getRogueInstance().addDialogueCoin(param.getInt(2));
            }
            case RepeatableGamble -> {
                var failEventId = param.getInt(0);
                var initialPercent = param.getInt(1);
                var increasePercent = param.getInt(2);
                var option = this.getRogueInstance().getRunningEvents().get(eventUniqueId).Options.stream().filter(x -> x.OptionId == optionId).findFirst();
                if (option.isEmpty()) return 0;
                var nowPercentage = option.get().Ratio;
                if (nowPercentage != 0)
                    nowPercentage = initialPercent;
                var weightList = new WeightedList<Integer>();
                for (int i = 3; i < param.size(); i += 2) {
                    weightList.add(param.getInt(i + 1), param.getInt(i));
                }
                int randomNum = Utils.randomRange(0, 10000);
                handleCost(optionId);
                if (randomNum <= nowPercentage * 10000) {
                    this.handleEvent(failEventId, eventUniqueId);
                    this.getRogueInstance().getRunningEvents().get(eventUniqueId).EffectEventId.add(failEventId);
                    return 0;
                } else {
                    int nextEventId = weightList.next();
                    // add percentage
                    option.get().Ratio += increasePercent / 100f;
                    this.handleEvent(nextEventId, eventUniqueId);
                    this.getRogueInstance().getRunningEvents().get(eventUniqueId).EffectEventId.add(nextEventId);
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
        handleCost(optionId);
        
        if (isEvent) {
            // Sync
            this.getRogueInstance().getPlayer().sendPacket(new PacketSyncRogueCommonDialogueOptionFinishScNotify(instance));
        }
        return 0;
    }
    
    public void handleCost(int eventId) {
        var event = GameData.getRogueDialogueEventList().get(eventId);
        if (event == null || event.getCostType() == null) return;
        var param = event.getCostParamList();
        switch (event.getCostType()) {
            case CostItemValue -> rogueInstance.removeCoin(param.getInt(1));
            case CostItemPercent -> rogueInstance.removeCoin(rogueInstance.getCoin() * param.getInt(1) / 100);
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
