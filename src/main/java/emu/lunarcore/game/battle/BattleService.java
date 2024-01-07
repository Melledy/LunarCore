package emu.lunarcore.game.battle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import emu.lunarcore.GameConstants;
import emu.lunarcore.data.GameData;
import emu.lunarcore.data.excel.CocoonExcel;
import emu.lunarcore.data.excel.StageExcel;
import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.battle.skills.MazeSkill;
import emu.lunarcore.game.enums.StageType;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.game.scene.entity.EntityMonster;
import emu.lunarcore.game.scene.entity.EntityProp;
import emu.lunarcore.game.scene.entity.GameEntity;
import emu.lunarcore.proto.AvatarPropertyOuterClass.AvatarProperty;
import emu.lunarcore.proto.BattleEndStatusOuterClass.BattleEndStatus;
import emu.lunarcore.proto.BattleStatisticsOuterClass.BattleStatistics;
import emu.lunarcore.server.game.BaseGameService;
import emu.lunarcore.server.game.GameServer;
import emu.lunarcore.server.packet.send.PacketReEnterLastElementStageScRsp;
import emu.lunarcore.server.packet.send.PacketSceneCastSkillScRsp;
import emu.lunarcore.server.packet.send.PacketStartCocoonStageScRsp;
import emu.lunarcore.server.packet.send.PacketSyncLineupNotify;
import it.unimi.dsi.fastutil.ints.IntSet;

public class BattleService extends BaseGameService {

    public BattleService(GameServer server) {
        super(server);
    }

    public void startBattle(Player player, int casterId, int attackedGroupId, MazeSkill castedSkill, IntSet hitTargets, IntSet assistMonsters) {
        // Setup variables
        List<GameEntity> targets = new ArrayList<>();
        GameAvatar castingAvatar = null;
        
        // Check if attacker is the player or not
        if (player.getScene().getAvatarEntityIds().contains(casterId)) {
            // Get casting avatar
            castingAvatar = player.getCurrentLeaderAvatar();
            
            // Player is the attacker, add hit targets to the battle
            for (int entityId : hitTargets) {
                GameEntity entity = player.getScene().getEntities().get(entityId);
                
                if (entity != null) {
                    targets.add(entity);
                }
            }
        } else {
            // A monster attacked the player -> Check if player is ambushed first
            boolean isAmbushed = false;
            
            for (int entityId : hitTargets) {
                if (player.getScene().getAvatarEntityIds().contains(entityId)) {
                    isAmbushed = true;
                }
            }
            
            // Start battle if player has been ambushed
            if (isAmbushed) {
                GameEntity entity = player.getScene().getEntities().get(casterId);
                
                if (entity != null) {
                    targets.add(entity);
                }
            } else {
                // Skip battle since the monster didnt attack anyone
                player.sendPacket(new PacketSceneCastSkillScRsp(attackedGroupId));
                return;
            }
        }
        
        // Skip battle if no attacked entities detected
        if (targets.size() == 0) {
            player.sendPacket(new PacketSceneCastSkillScRsp(attackedGroupId));
            return;
        }
        
        // Separate entities into monster list
        Set<EntityMonster> monsters = new HashSet<>();
        
        // Destroy props
        for (var it = targets.iterator(); it.hasNext();) {
            GameEntity entity = it.next();
            
            if (entity instanceof EntityMonster monster) {
                monsters.add(monster);
            } else if (entity instanceof EntityProp prop) {
                it.remove();
                player.getScene().destroyProp(prop);
            } else {
                it.remove();
            }
        }
        
        // Check if we are using a skill that doesnt trigger a battle
        if (castedSkill != null && !castedSkill.isTriggerBattle()) {
            // Apply buffs to monsters
            castedSkill.onCastHit(player.getCurrentLeaderAvatar(), targets);
            // Skip battle if our technique does not trigger a battle
            player.sendPacket(new PacketSceneCastSkillScRsp(attackedGroupId));
            return;
        }
        
        // Add any assisting monsters from monster assist list
        for (int entityId : assistMonsters) {
            GameEntity entity = player.getScene().getEntities().get(entityId);
            
            if (entity != null && entity instanceof EntityMonster monster) {
                monsters.add(monster);
            }
        }
        
        // Start battle
        if (monsters.size() > 0) {
            // Maze skill attack event
            if (castedSkill != null && castingAvatar != null) {
                castedSkill.onAttack(castingAvatar, targets);
            }
            
            // Create battle and add npc monsters to it
            Battle battle = new Battle(player, player.getLineupManager().getCurrentLineup(), monsters);
            
            // Make sure we have at least one stage for the battle
            if (battle.getStage() == null) {
                player.sendPacket(new PacketSceneCastSkillScRsp());
                return;
            }
            
            // Add buffs to battle
            if (castingAvatar != null) {
                // Add elemental weakness debuff to enemies
                MazeBuff buff = battle.addBuff(castingAvatar.getExcel().getDamageType().getEnterBattleBuff(), battle.getLineup().getLeader());
                if (buff != null) {
                    buff.addTargetIndex(battle.getLineup().getLeader());
                    buff.addDynamicValue("SkillIndex", castedSkill.getIndex());
                }
            } else {
                // Ambush debuff (from monsters)
                battle.addBuff(GameConstants.BATTLE_AMBUSH_BUFF_ID, -1, 1);
            }
            
            // Challenge
            if (player.getChallengeInstance() != null) {
                player.getChallengeInstance().onBattleStart(battle);
            }
            
            // Rogue
            if (player.getRogueInstance() != null) {
                player.getRogueInstance().onBattleStart(battle);
            }
            
            // Set battle and send rsp packet
            player.setBattle(battle);
            player.sendPacket(new PacketSceneCastSkillScRsp(battle, attackedGroupId));
            return;
        }
        
        // Send packet
        player.sendPacket(new PacketSceneCastSkillScRsp(attackedGroupId));
    }
    
    public void startCocoon(Player player, int cocoonId, int worldLevel, int wave) {
        // Sanity check to make sure player isnt in a battle
        if (player.isInBattle()) {
            return;
        }
        
        // Get cocoon data
        CocoonExcel cocoonExcel = GameData.getCocoonExcel(cocoonId, worldLevel);
        if (cocoonExcel == null) {
            player.sendPacket(new PacketStartCocoonStageScRsp());
            return;
        }
        
        // Get waves
        wave = Math.min(Math.max(1, wave), cocoonExcel.getMaxWave());
        
        // Sanity check stamina
        int cost = cocoonExcel.getStaminaCost() * wave;
        if (player.getStamina() < cost) {
            return;
        }
        
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
        battle.setMappingInfoId(cocoonExcel.getMappingInfoID());
        battle.setCocoonWave(wave);
        battle.setWorldLevel(worldLevel);
        battle.setStaminaCost(cost);
        
        player.setBattle(battle);
        
        // Send packet
        player.sendPacket(new PacketStartCocoonStageScRsp(battle, cocoonId, wave));
    }

    public Battle finishBattle(Player player, BattleEndStatus result, BattleStatistics stats) {
        // Sanity check to make sure player is in a battle
        if (!player.isInBattle()) {
            return null;
        }
        
        // Get battle object and setup variables
        Battle battle = player.getBattle();
        battle.setResult(result);
        int minimumHp = 0;
        
        boolean updateStatus = true;
        boolean teleportToAnchor = false;
        
        // Handle result
        switch (result) {
            case BATTLE_END_WIN -> {
                // Remove monsters from the map - Could optimize it a little better
                for (var monster : battle.getNpcMonsters()) {
                    // Dont remove farmable monsters from the scene when they are defeated
                    if (monster.isFarmElement()) continue;
                    // Remove monster
                    player.getScene().removeEntity(monster);
                }
                // Drops
                getServer().getDropService().calculateDrops(battle);
                // Spend stamina
                if (battle.getStaminaCost() > 0) {
                    player.spendStamina(battle.getStaminaCost());
                }
            }
            case BATTLE_END_LOSE -> {
                // Set avatar hp to 20% if the player's party is downed
                minimumHp = 2000;
                teleportToAnchor = true;
            }
            case BATTLE_END_QUIT -> {
                updateStatus = false;
                // Only teleport back to anchor if stage is a random fight
                if (battle.getStage().getStageType() == StageType.Mainline) {
                    teleportToAnchor = true;
                }
            }
            default -> {
                updateStatus = false;
            }
        }
        
        // Check if avatar hp/sp should be updated after a battle
        if (updateStatus) {
            // Set health/energy for player avatars
            for (var battleAvatar : stats.getBattleAvatarList()) {
                GameAvatar avatar = player.getAvatarById(battleAvatar.getId());
                if (avatar == null) continue;

                AvatarProperty prop = battleAvatar.getAvatarStatus();
                int currentHp = (int) Math.round((prop.getLeftHp() / prop.getMaxHp()) * 10000);
                int currentSp = (int) prop.getLeftSp() * 100;

                avatar.setCurrentHp(battle.getLineup(), Math.max(currentHp, minimumHp));
                avatar.setCurrentSp(battle.getLineup(), Math.max(currentSp, 0));
                avatar.save();
            }

            // Sync with player
            player.sendPacket(new PacketSyncLineupNotify(battle.getLineup()));

            // Clear food buffs for player
            player.removeFoodBuffs(1);
        }
        
        // Teleport to anchor if player has lost/retreated. On official servers, the player party is teleported to the nearest anchor.
        if (teleportToAnchor) {
            var anchorProp = player.getScene().getNearestSpring();
            if (anchorProp != null && anchorProp.getPropInfo() != null) {
                var anchor = player.getScene().getFloorInfo().getAnchorInfo(
                        anchorProp.getPropInfo().getAnchorGroupID(), 
                        anchorProp.getPropInfo().getAnchorID()
                );
                if (anchor != null) {
                    player.moveTo(anchor.getPos());
                }
            }
        }
        
        // Challenge
        if (player.getChallengeInstance() != null) {
            player.getChallengeInstance().onBattleFinish(battle, result, stats);
        }
        
        // Rogue
        if (player.getRogueInstance() != null) {
            player.getRogueInstance().onBattleFinish(battle, result, stats);
        }
        
        // Battle callback
        if (battle.getOnFinish() != null) {
            battle.getOnFinish().accept(stats);
        }
        
        // Done - Clear battle object from player
        player.setBattle(null);
        return battle;
    }

    public void reEnterBattle(Player player, int stageId) {
        // Sanity check to make sure player isnt in a battle
        if (player.isInBattle()) {
            player.sendPacket(new PacketReEnterLastElementStageScRsp());
            return;
        }
        
        // Get stage
        StageExcel stage = GameData.getStageExcelMap().get(stageId);
        if (stage == null || stage.getStageType() != StageType.FarmElement) {
            player.sendPacket(new PacketReEnterLastElementStageScRsp());
            return;
        }
        
        // Create new battle for player
        Battle battle = new Battle(player, player.getCurrentLineup(), stage);
        battle.setStaminaCost(GameConstants.FARM_ELEMENT_STAMINA_COST);
        player.setBattle(battle);
        
        // Get mapping info id
        int mappingInfoId = ((stageId / 10) % 100) + 1100;
        int mappingInfoLevel = stageId % 10;
        var mappingInfoExcel = GameData.getMappingInfoExcel(mappingInfoId, mappingInfoLevel);
        if (mappingInfoExcel != null && mappingInfoExcel.getFarmType() != null && mappingInfoExcel.getFarmType().equals("ELEMENT")) {
            battle.setMappingInfoId(mappingInfoId);
            battle.setWorldLevel(mappingInfoLevel);
        }
        
        // Send packet
        player.sendPacket(new PacketReEnterLastElementStageScRsp(battle));
    }
}
