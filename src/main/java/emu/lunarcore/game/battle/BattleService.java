package emu.lunarcore.game.battle;

import java.util.ArrayList;
import java.util.List;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.excel.CocoonExcel;
import emu.lunarcore.data.excel.StageExcel;
import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.game.scene.entity.EntityMonster;
import emu.lunarcore.game.scene.entity.EntityProp;
import emu.lunarcore.game.scene.entity.GameEntity;
import emu.lunarcore.proto.AvatarBattleInfoOuterClass.AvatarBattleInfo;
import emu.lunarcore.proto.AvatarPropertyOuterClass.AvatarProperty;
import emu.lunarcore.proto.BattleEndStatusOuterClass.BattleEndStatus;
import emu.lunarcore.server.game.BaseGameService;
import emu.lunarcore.server.game.GameServer;
import emu.lunarcore.server.packet.send.PacketSceneCastSkillScRsp;
import emu.lunarcore.server.packet.send.PacketStartCocoonStageScRsp;
import emu.lunarcore.server.packet.send.PacketSyncLineupNotify;

import us.hebi.quickbuf.RepeatedInt;
import us.hebi.quickbuf.RepeatedMessage;

public class BattleService extends BaseGameService {

    public BattleService(GameServer server) {
        super(server);
    }

    public void startBattle(Player player, int attackerId, RepeatedInt attackedList) {
        // Setup variables
        List<GameEntity> entities = new ArrayList<>();
        List<EntityMonster> monsters = new ArrayList<>();
        
        boolean isPlayerCaster = false; // Set true if the player is the one casting
        
        // Check if attacker is the player or not
        if (player.getScene().getAvatarEntityIds().contains(attackerId)) {
            // Attacker is the player
            for (int entityId : attackedList) {
                GameEntity entity = player.getScene().getEntities().get(entityId);
                
                if (entity != null) {
                    entities.add(entity);
                }
            }

            isPlayerCaster = true;
        } else {
            // Player is ambushed
            GameEntity entity = player.getScene().getEntities().get(attackerId);
            
            if (entity != null) {
                entities.add(entity);
            }
        }
        
        // Give the client an error if not attacked entities detected
        if (entities.size() == 0) {
            player.sendPacket(new PacketSceneCastSkillScRsp(1));
            return;
        }
        
        // Destroy props
        var it = entities.iterator();
        while (it.hasNext()) {
            GameEntity entity = it.next();
            
            if (entity instanceof EntityMonster monster) {
                monsters.add(monster);
            } else if (entity instanceof EntityProp) {
                it.remove();
                player.getScene().removeEntity(entity);
            }
        }

        // Start battle
        if (monsters.size() > 0) {
            // Get stages from monsters
            List<StageExcel> stages = new ArrayList<>();
            
            for (var monster : monsters) {
                StageExcel stage = GameData.getStageExcelMap().get(monster.getStageId(player.getWorldLevel()));
                
                if (stage != null) {
                    stages.add(stage);
                }
            }
            
            if (stages.size() == 0) {
                // An error has occurred while trying to get stage data
                player.sendPacket(new PacketSceneCastSkillScRsp(1));
                return;
            }
            
            // Create battle and add npc monsters to it
            Battle battle = new Battle(player, player.getLineupManager().getCurrentLineup(), stages);
            battle.getNpcMonsters().addAll(monsters);
            // Add weakness buff to battle
            if (isPlayerCaster) {
                GameAvatar avatar = player.getLineupManager().getCurrentLeaderAvatar();
                if (avatar != null) {
                    MazeBuff buff = battle.addBuff(avatar.getExcel().getDamageType().getEnterBattleBuff(), 0);
                    if (buff != null) {
                        buff.addDynamicValue("SkillIndex", 1);
                    }
                }
            }
            // Set battle and send rsp packet
            player.setBattle(battle);
            player.sendPacket(new PacketSceneCastSkillScRsp(battle));
            return;
        }
        
        // Send packet
        player.sendPacket(new PacketSceneCastSkillScRsp(0));
    }
    
    public void startCocoon(Player player, int cocoonId, int worldLevel, int wave) {
        // Get cocoon data
        CocoonExcel cocoonExcel = GameData.getCocoonExcel(cocoonId, worldLevel);
        if (cocoonExcel == null) {
            player.sendPacket(new PacketStartCocoonStageScRsp());
            return;
        }
        
        // Get waves
        wave = Math.min(Math.max(1, wave), cocoonExcel.getMaxWave());
        
        // Get stages from cocoon
        List<StageExcel> stages = new ArrayList<>();
        
        for (int i = 0; i < wave; i++) {
            StageExcel stage = GameData.getStageExcelMap().get(cocoonExcel.getRandomStage());
            
            if (stage != null) {
                stages.add(stage);
            }
        }
        
        // Sanity
        if (stages.size() <= 0) {
            player.sendPacket(new PacketStartCocoonStageScRsp());
            return;
        }
        
        // Build battle from cocoon data
        Battle battle = new Battle(player, player.getLineupManager().getCurrentLineup(), stages);
        
        // Send packet
        player.sendPacket(new PacketStartCocoonStageScRsp(battle, cocoonId, wave));
    }

    public void finishBattle(Player player, BattleEndStatus result, RepeatedMessage<AvatarBattleInfo> battleAvatars) {
        // Sanity
        if (!player.isInBattle()) {
            return;
        }
        
        // Get battle object
        Battle battle = player.getBattle();
        
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
        player.sendPacket(new PacketSyncLineupNotify(battle.getLineup()));
        
        // Delete enemies if the player won
        if (result == BattleEndStatus.BATTLE_END_WIN) {
            // Could optimize it a little better
            for (var monster : battle.getNpcMonsters()) {
                player.getScene().removeEntity(monster);
            }
        }
        
        // Done - Clear battle object from player
        player.setBattle(null);
    }
}
