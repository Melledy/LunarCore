package emu.lunarcore.game.battle;

import java.util.ArrayList;
import java.util.List;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.excel.MazeBuffExcel;
import emu.lunarcore.data.excel.StageExcel;
import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.game.player.PlayerLineup;
import emu.lunarcore.game.scene.EntityMonster;
import emu.lunarcore.proto.SceneBattleInfoOuterClass.SceneBattleInfo;
import emu.lunarcore.proto.SceneMonsterOuterClass.SceneMonster;
import emu.lunarcore.proto.SceneMonsterWaveOuterClass.SceneMonsterWave;
import emu.lunarcore.util.Utils;
import lombok.Getter;

@Getter
public class Battle {
    private final Player player;
    private final PlayerLineup lineup;
    private final List<EntityMonster> npcMonsters;
    private final List<MazeBuff> buffs;
    private StageExcel stage;

    public Battle(Player player, PlayerLineup lineup, StageExcel stage) {
        this.player = player;
        this.lineup = lineup;
        this.npcMonsters = new ArrayList<>();
        this.buffs = new ArrayList<>();
        this.stage = stage;
    }
    
    public MazeBuff addBuff(int buffId, int ownerId) {
        return addBuff(buffId, ownerId, 0xffffffff);
    }
    
    public MazeBuff addBuff(int buffId, int ownerId, int waveFlag) {
        MazeBuffExcel excel = GameData.getMazeBuffExcel(buffId, 1);
        if (excel == null) return null;
        
        MazeBuff buff = new MazeBuff(excel, ownerId, waveFlag);
        this.buffs.add(buff);
        
        return buff;
    }
    
    public SceneBattleInfo toProto() {
        var wave = SceneMonsterWave.newInstance()
                .setStageId(stage.getId());

        int[] monsters = {101203002, 100202003, 100204007, 100205006};

        for (int i = 0; i < 5; i++) {
            var m = SceneMonster.newInstance()
                    .setMonsterId(Utils.randomElement(monsters));

            wave.addMonsterList(m);
        }
        
        var proto = SceneBattleInfo.newInstance()
                .setStageId(stage.getId())
                .setLogicRandomSeed(Utils.randomRange(1, Short.MAX_VALUE))
                .addMonsterWaveList(wave)
                .setWorldLevel(player.getWorldLevel());
        
        // Buffs
        for (MazeBuff buff : this.getBuffs()) {
            proto.addBuffList(buff.toProto());
        }
        
        // Avatars
        for (int i = 0; i < lineup.getAvatars().size(); i++) {
            GameAvatar avatar = getPlayer().getAvatarById(lineup.getAvatars().get(i));
            if (avatar == null) continue;

            proto.addBattleAvatarList(avatar.toBattleProto(i));
        }
        
        return proto;
    }
}
