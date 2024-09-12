package emu.lunarcore.game.enums;

import lombok.Getter;

@Getter
public enum DialogueEventType {
    NONE(0),
    GetItem(1),
    TriggerRogueBuffSelect(2),
    TriggerRogueMiracleSelect(3),
    EnhanceRogueBuff(4),
    TriggerRandomEventList(5),
    ChangeLineupData(6),
    TriggerRogueBuffReforge(8),
    GetRogueMiracle(9),
    TriggerDialogueEventList(10),
    GetAllRogueBuffInGroup(11),
    GetAllRogueBuffInGroupAndGetItem(12),
    TriggerBattle(13),
    TriggerRogueMiracleTrade(14),
    RepeatableGamble(15),
    ReplaceRogueBuffKeepLevel(16),
    FinishChessRogue(17),
    GetRogueBuff(21),
    GetChessRogueCheatDice(18),
    SetChessRogueNextStartCellAdventureRoomType(19),
    ChangeChessRogueActionPoint(20),
    ReplaceRogueBuff(23),
    DestroyRogueMiracle(22),
    TriggerRogueBuffDrop(26),
    ChangeRogueMiracleToRogueCoin(24),
    RemoveRogueMiracle(27),
    GetItemByPercent(25),
    RemoveRogueBuff(28),
    TriggerRogueMiracleRepair(29),
    RepairRogueMiracle(30),
    ChangeRogueMiracleToRogueMiracle(31),
    ChangeRogueMiracleToRogueBuff(32),
    GetChessRogueRerollDice(33),
    GetRogueBuffByMiracleCount(34),
    ChangeNousValue(35),
    ReviveAvatar(36),
    TriggerDialogueEventListByCondition(37),
    TriggerRogueMiracleDropWithEvent(38),
    TriggerRogueBuffEnhance(39),
    GetCoinByLoseCoin(40),
    ChangeRogueNpcWeight(41),
    GetDestroyedRogueMiracle(42),
    ChangeDestroyedRogueMiracleToRogueMiracle(43),
    DestroyRogueMiracleThenGetRogueMiracle(44),
    TriggerDestroyedRogueMiracleSelect(45);

    private final int val;
    
    DialogueEventType(int i) {
        this.val = i;
    }
}
