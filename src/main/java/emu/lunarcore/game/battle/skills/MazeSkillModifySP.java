package emu.lunarcore.game.battle.skills;

import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.battle.Battle;
import emu.lunarcore.proto.MotionInfoOuterClass.MotionInfo;

public class MazeSkillModifySP extends MazeSkillAction {
    private int amount;
    
    public MazeSkillModifySP(int sp) {
        this.amount = sp * 100;
    }

    @Override
    public void onCast(GameAvatar caster, MotionInfo castPosition) {
        caster.setCurrentSp(amount + caster.getCurrentSp());
        // TODO Perhaps we should send a sync lineup packet here
    }

    @Override
    public void onAttack(GameAvatar caster, Battle battle) {
        
    }

}
