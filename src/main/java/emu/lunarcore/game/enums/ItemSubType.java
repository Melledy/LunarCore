package emu.lunarcore.game.enums;

import lombok.Getter;

@Getter
public enum ItemSubType {
    Unknown (0),
    Virtual (101),
    GameplayCounter (102),
    AvatarCard (201),
    Equipment (301),
    Relic (401),
    Gift (501),
    Food (502),
    ForceOpitonalGift (503),
    Book (504),
    HeadIcon (505),
    MusicAlbum (506),
    Formula (507),
    ChatBubble (508),
    AvatarSkin (509),
    PhoneTheme (510),
    TravelBrochurePaster (511),
    ChessRogueDiceSurface (512),
    RogueMedal (513),
    PamSkin (514),
    FindChest (515),
    PersonalCard (516),
    PhoneCase (517),
    PlayerOutfit (518),
    HeadIconFrame (519),
    PlatformBoundGift (520),
    Material (601),
    Eidolon (602),
    MuseumExhibit (603),
    MuseumStuff (604),
    AetherSkill (605),
    AetherSpirit (606),
    FightFestSkill (607),
    TrainPartyDiyMaterial (608),
    PlanetFesItem (609),
    ElfRestaurantItem (610),
    MatchThreeV2 (611),
    HipplenOutfit (612),
    DiceCombatAvatar (613),
    DiceCombatDice (614),
    IdleLiveItem (616),
    AvatarExp (651),
    AvatarRank (652),
    TracePath (653),
    WeeklyMonsterDrop (654),
    EquipmentExp (655),
    RelicExp (656),
    CommonMonsterDrop (657),
    ComposeMaterial (658),
    Mission (701),
    RelicSetShowOnly (801),
    RelicRarityShowOnly (802),
    NormalPet (901);

    private int val;

    private ItemSubType(int value) {
        this.val = value;
    }
}
