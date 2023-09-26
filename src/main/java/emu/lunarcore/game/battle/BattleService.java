package emu.lunarcore.game.battle;

import java.util.Collection;
import java.util.List;

import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.game.scene.EntityMonster;
import emu.lunarcore.game.scene.EntityProp;
import emu.lunarcore.game.scene.GameEntity;
import emu.lunarcore.proto.AvatarBattleInfoOuterClass.AvatarBattleInfo;
import emu.lunarcore.proto.AvatarPropertyOuterClass.AvatarProperty;
import emu.lunarcore.proto.BattleEndStatusOuterClass.BattleEndStatus;
import emu.lunarcore.server.game.BaseGameService;
import emu.lunarcore.server.game.GameServer;
import emu.lunarcore.server.packet.send.PacketSceneCastSkillScRsp;
import emu.lunarcore.server.packet.send.PacketSyncLineupNotify;
import us.hebi.quickbuf.RepeatedInt;
import us.hebi.quickbuf.RepeatedMessage;

public class BattleService extends BaseGameService {

    public BattleService(GameServer server) {
        super(server);
    }

    public void onBattleStart(Player player, int attackerId, RepeatedInt attackedList) {
        // Setup variables
        int entityId = attackedList.get(0);
        GameEntity entity = null;

        // Check if attacker is the player or not
        if (player.getScene().getAvatarEntityIds().contains(attackerId)) {
            entity = player.getScene().getEntities().get(entityId);
        } else if (player.getScene().getAvatarEntityIds().contains(entityId)) {
            entity = player.getScene().getEntities().get(attackerId);
        }

        if (entity != null) {
            if (entity instanceof EntityMonster) {
                player.sendPacket(new PacketSceneCastSkillScRsp(player, (EntityMonster) entity));
                return;
            } else if (entity instanceof EntityProp) {
                player.sendPacket(new PacketSceneCastSkillScRsp(0));
                return;
            }
        }
        
        player.sendPacket(new PacketSceneCastSkillScRsp(1));
    }

    public void onBattleResult(Player player, BattleEndStatus result, RepeatedMessage<AvatarBattleInfo> battleAvatars) {
        // Lose
        if (result == BattleEndStatus.BATTLE_END_LOSE) {

        }

        // Set health/energy
        for (var battleAvatar : battleAvatars) {
            GameAvatar avatar = player.getAvatarById(battleAvatar.getId());
            if (avatar == null) continue;

            AvatarProperty prop = battleAvatar.getAvatarStatus();
            int currentHp = (int) Math.round((prop.getLeftHp() / prop.getMaxHp()) * 100);
            int currentSp = (int) prop.getLeftSp() * 100;

            //avatar.setCurrentHp(currentHp);
            avatar.setCurrentSp(currentSp);
            avatar.save();
        }

        // Sync with player
        player.sendPacket(new PacketSyncLineupNotify(player.getLineupManager().getCurrentLineup()));
    }

}
