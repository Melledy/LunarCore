package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.battle.Battle;
import emu.lunarcore.game.player.PlayerLineup;
import emu.lunarcore.game.scene.entity.EntityMonster;
import emu.lunarcore.game.player.Player;
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
    public PacketSceneCastSkillScRsp(Player player, Battle battle) {
        super(CmdId.SceneCastSkillScRsp);

        // Build data
        var data = SceneCastSkillScRsp.newInstance()
                //.setAttackedGroupId(monster.getGroupId())
                .setBattleInfo(battle.toProto());

        this.setData(data);
    }
}
