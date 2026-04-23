package emu.lunarcore.game.trial;

import emu.lunarcore.data.GameData;
import emu.lunarcore.game.player.BasePlayerManager;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.game.player.PlayerGender;
import emu.lunarcore.game.player.lineup.PlayerLineup;
import emu.lunarcore.proto.ExtraLineupTypeOuterClass.ExtraLineupType;
import it.unimi.dsi.fastutil.ints.IntArrayList;

public class TrialManager extends BasePlayerManager {

    public TrialManager(Player player) {
        super(player);
    }
    
    public TrialAvatar createTrialAvatar(int specialAvatarId) {
        // Get excels
        var specialAvatarExcel = GameData.getSpecialAvatarExcelMap().get(specialAvatarId);
        if (specialAvatarExcel == null) return null;
        
        var avatarExcel = GameData.getAvatarExcelMap().get(specialAvatarExcel.getAvatarID());
        if (avatarExcel == null) return null;
        
        // Hacky way to remove duplicate TBs
        if (avatarExcel.getId() > 8000) {
            boolean isMale = avatarExcel.getId() % 2 == 1;
            if (getPlayer().getGender() == PlayerGender.GENDER_WOMAN && isMale) {
                return null;
            } else if (getPlayer().getGender() == PlayerGender.GENDER_MAN && !isMale) {
                return null;
            }
        }
        
        // Create trial avatar
        TrialAvatar avatar = new TrialAvatar(getPlayer(), specialAvatarExcel, avatarExcel);
        
        // Add to temp avatar storage
        getPlayer().getAvatars().addTempAvatar(avatar);
        
        //
        return avatar;
    }

    public TrialInstance startTrial(int stageId) {
        // Get excel
        var excel = GameData.getAvatarDemoExcelMap().get(stageId);
        if (excel == null) return null;
        
        // Create avatars
        var avatars = new IntArrayList();
        
        for (int id : excel.getTrialAvatarList()) {
            // Create trial avatar
            TrialAvatar avatar = this.createTrialAvatar(id);
            
            // Skip
            if (avatar == null) continue;
            
            // Add to temp lineup
            avatars.add(avatar.getSpecialAvatarExcel().getId());
        }

        // Get trial lineup
        PlayerLineup lineup = getPlayer().getLineupManager().getExtraLineupByType(ExtraLineupType.LINEUP_STAGE_TRIAL_VALUE);
        
        // Add avatars to lineup
        if (avatars.size() > 0) {
            lineup.replace(avatars);
            lineup.setMp(lineup.getMaxMp());
        } else {
            // Error
            return null;
        }
        
        // Set first lineup before we enter scenes
        getPlayer().getLineupManager().setCurrentExtraLineup(ExtraLineupType.LINEUP_STAGE_TRIAL_VALUE, false);
        
        // Create trial instance
        TrialInstance trial = new TrialInstance(getPlayer(), excel);
        getPlayer().setInstance(trial);
        
        // Enter scene
        boolean success = getPlayer().enterScene(excel.getMapEntranceID(), 0, true);
        if (!success) {
            // Reset lineup/instance if entering scene failed
            getPlayer().getLineupManager().setCurrentExtraLineup(0, false);
            getPlayer().setInstance(null);
            return null;
        }
        
        // Done
        return trial;
    }

}
