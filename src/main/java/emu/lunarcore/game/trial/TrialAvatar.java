package emu.lunarcore.game.trial;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.excel.AvatarExcel;
import emu.lunarcore.data.excel.SpecialAvatarExcel;
import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.AvatarTypeOuterClass.AvatarType;
import lombok.Getter;

@Getter
public class TrialAvatar extends GameAvatar {
    private SpecialAvatarExcel specialAvatarExcel;

    public TrialAvatar(Player owner, SpecialAvatarExcel specialAvatarExcel, AvatarExcel excel) {
        super(excel);
        this.setOwner(owner);
        this.setExcel(excel);
        this.specialAvatarExcel = specialAvatarExcel;
        this.avatarId = specialAvatarExcel.getId();
        
        // Set stats
        this.setLevel(specialAvatarExcel.getLevel());
        this.setPromotion(specialAvatarExcel.getPromotion());
        this.setRank(specialAvatarExcel.getRank());
        this.extraLineupHp = 10000;
        this.extraLineupSp = 7000;
        
        // Hacky way to set skill points
        for (int pointId : this.getExcel().getSkillTreeIds()) {
            var skillTree = GameData.getAvatarSkillTreeExcelMap().get(pointId, 1);
            if (skillTree == null) continue;
            
            // Point level should be 1 by default
            int pointLevel = Math.min(1, skillTree.getMaxLevel());
            if (skillTree.getMaxLevel() == 6) {
                pointLevel = 5; // Basic attack
            } else if (skillTree.getMaxLevel() == 10) {
                pointLevel = 8; // Skill/Ult/Talent
            }
            
            // Get anchor id
            this.getSkillTree().put(skillTree.getAnchorId(), pointLevel);
        }
        
        // Set equipment
        var equipmentExcel = GameData.getItemExcelMap().get(specialAvatarExcel.getEquipmentID());
        if (equipmentExcel != null) {
            var equipment = new GameItem(equipmentExcel);
            equipment.setLevel(specialAvatarExcel.getEquipmentLevel());
            equipment.setPromotion(specialAvatarExcel.getPromotion());
            equipment.setRank(specialAvatarExcel.getEquipmentRank());
            
            this.equipItem(equipment);
        }
    }
    
    @Override
    public int getActorId() {
        return specialAvatarExcel.getAvatarID();
    }
    
    @Override
    public int getBattleAvatarId() {
        return this.avatarId;
    }
    
    @Override
    public AvatarType getAvatarType() {
        return AvatarType.AVATAR_TRIAL_TYPE;
    }
    
    @Override
    public void save() {
        // Prevent the server from saving trial avatars
    }
    
}
