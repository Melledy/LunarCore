package emu.lunarcore.game.enums;

import lombok.Getter;

public enum AvatarPropertyType {
    Unknown (0),
    MaxHP (1),
    Attack (2),
    Defence (3),
    Speed (4),
    CriticalChance (5),
    CriticalDamage (6),
    HealRatio (7),
    StanceBreakAddedRatio (8),
    SPRatio (9),
    StatusProbability (10),
    StatusResistance (11),
    PhysicalAddedRatio (12),
    PhysicalResistance (13),
    FireAddedRatio (14),
    FireResistance (15),
    IceAddedRatio (16),
    IceResistance (17),
    ThunderAddedRatio (18),
    ThunderResistance (19),
    WindAddedRatio (20),
    WindResistance (21),
    QuantumAddedRatio (22),
    QuantumResistance (23),
    ImaginaryAddedRatio (24),
    ImaginaryResistance (25),
    BaseHP (26),
    HPDelta (27),
    BaseAttack (28),
    AttackDelta (29),
    BaseDefence (30),
    DefenceDelta (31),
    HPAddedRatio (32),
    AttackAddedRatio (33),
    DefenceAddedRatio (34),
    BaseSpeed (35),
    HealTakenRatio (36),
    PhysicalResistanceDelta (37),
    FireResistanceDelta (38),
    IceResistanceDelta (39),
    ThunderResistanceDelta (40),
    WindResistanceDelta (41),
    QuantumResistanceDelta (42),
    ImaginaryResistanceDelta (43),
    AllDamageReduce (44),
    RelicValueExtraAdditionRatio (45),
    EquipValueExtraAdditionRatio (46),
    EquipExtraRank (47),
    AvatarExtraRank (48),
    AllDamageTypeAddedRatio (49),
    SpeedAddedRatio (50),
    SpeedDelta (51),
    CriticalChanceBase (52),
    CriticalDamageBase (53),
    SPRatioBase (54),
    HealRatioBase (55),
    StatusProbabilityBase (56),
    StatusResistanceBase (57),
    BreakDamageAddedRatio (58),
    BreakDamageAddedRatioBase (59),
    MaxSP (60);

    @Getter
    private int val;

    private AvatarPropertyType(int value) {
        this.val = value;
    }
}
