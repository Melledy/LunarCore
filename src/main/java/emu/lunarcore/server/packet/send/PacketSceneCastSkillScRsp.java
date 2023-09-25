package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.player.PlayerLineup;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.game.scene.EntityMonster;
import emu.lunarcore.proto.SceneBattleInfoOuterClass.SceneBattleInfo;
import emu.lunarcore.proto.SceneCastSkillScRspOuterClass.SceneCastSkillScRsp;
import emu.lunarcore.proto.SceneMonsterOuterClass.SceneMonster;
import emu.lunarcore.proto.SceneMonsterWaveOuterClass.SceneMonsterWave;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.util.Utils;

public class PacketSceneCastSkillScRsp extends BasePacket {

    public PacketSceneCastSkillScRsp() {
        this(0);
    }
    
    public PacketSceneCastSkillScRsp(int retcode) {
        super(CmdId.SceneCastSkillScRsp);

        var data = SceneCastSkillScRsp.newInstance()
                .setRetcode(retcode);

        this.setData(data);
    }

    // TODO
    public PacketSceneCastSkillScRsp(Player player, EntityMonster monster) {
        super(CmdId.SceneCastSkillScRsp);

        var wave = SceneMonsterWave.newInstance()
                .setStageId(monster.getStage().getId());

        int[] monsters = {101203002, 100202003, 100204007, 100205006};

        for (int i = 0; i < 5; i++) {
            var m = SceneMonster.newInstance()
                    .setMonsterId(Utils.randomElement(monsters));

            wave.addMonsterList(m);
        }

        var battle = SceneBattleInfo.newInstance()
                .setStageId(monster.getStage().getId())
                .setLogicRandomSeed(Utils.randomRange(1, Short.MAX_VALUE))
                .addMonsterWaveList(wave)
                .setWorldLevel(player.getWorldLevel());

        // Avatars
        PlayerLineup lineup = player.getLineupManager().getCurrentLineup();
        for (int i = 0; i < lineup.getAvatars().size(); i++) {
            GameAvatar avatar = player.getAvatarById(lineup.getAvatars().get(i));
            if (avatar == null) continue;

            battle.addBattleAvatarList(avatar.toBattleProto(i));
        }

        // Build data
        var data = SceneCastSkillScRsp.newInstance()
                .setAttackedGroupId(monster.getGroupId())
                .setBattleInfo(battle);

        this.setData(data);
    }
}
