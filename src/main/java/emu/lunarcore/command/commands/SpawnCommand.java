package emu.lunarcore.command.commands;

import emu.lunarcore.command.Command;
import emu.lunarcore.command.CommandArgs;
import emu.lunarcore.command.CommandHandler;
import emu.lunarcore.data.GameData;
import emu.lunarcore.data.config.GroupInfo;
import emu.lunarcore.data.config.MonsterInfo;
import emu.lunarcore.data.excel.NpcMonsterExcel;
import emu.lunarcore.data.excel.PropExcel;
import emu.lunarcore.game.enums.PropState;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.game.scene.entity.EntityMonster;
import emu.lunarcore.game.scene.entity.EntityProp;
import emu.lunarcore.util.Position;
import emu.lunarcore.util.Utils;

@Command(label = "spawn", permission = "player.spawn", desc = "/spawn [monster/prop id] x[amount] s[stage id]. Spawns a monster or prop near the targeted player.")
public class SpawnCommand implements CommandHandler {

    @Override
    public void execute(Player sender, CommandArgs args) {
        // Check target
        if (args.getTarget() == null) {
            this.sendMessage(sender, "Error: Targeted player not found or offline");
            return;
        }
        
        Player target = args.getTarget();
        
        if (target.getScene() == null) {
            this.sendMessage(sender, "Error: Target is not in scene");
            return;
        }
        
        // Get id
        int id = Utils.parseSafeInt(args.get(0));
        int stage = Math.max(Utils.parseSafeInt(args.get(1)), 1);
        int amount = Math.max(args.getAmount(), 1);
        int radius = Math.max(args.getRank(), 5) * 1000;
        
        // Spawn monster
        NpcMonsterExcel monsterExcel = GameData.getNpcMonsterExcelMap().get(id);
        if (monsterExcel != null) {
            // Try to find monster config
            GroupInfo group = null;
            MonsterInfo monsterInfo = null;
            
            for (var groupInfo : target.getScene().getFloorInfo().getGroups().values()) {
                if (groupInfo.getMonsterList().size() == 0) continue;
                
                for (var m : groupInfo.getMonsterList()) {
                    if (m.getFarmElementID() == 0) {
                        group = groupInfo;
                        monsterInfo = groupInfo.getMonsterList().get(0);
                        break;
                    }
                }
                
                if (monsterInfo != null) {
                    break; 
                }
            }
            
            if (monsterInfo == null) {
                this.sendMessage(sender, "Error: No monster config found in this scene");
                return;
            }
            
            // Spawn monster
            for (int i = 0; i < amount; i++) {
                Position pos = target.getPos().clone().add(Utils.randomRange(-radius, radius), 0, Utils.randomRange(-radius, radius));
                EntityMonster monster = new EntityMonster(target.getScene(), monsterExcel, group, monsterInfo);
                monster.getPos().set(pos);
                monster.setEventId(monsterInfo.getEventID());
                monster.setOverrideStageId(stage);
                
                target.getScene().addEntity(monster, true);
            }
            
            // Send message when done
            this.sendMessage(sender, "Spawning " + amount + " monsters");
            return;
        }
        
        PropExcel propExcel = GameData.getPropExcelMap().get(id);
        if (propExcel != null) {
            // Spawn props
            for (int i = 0; i < amount; i++) {
                Position pos = target.getPos().clone().add(Utils.randomRange(-radius, radius), 0, Utils.randomRange(-radius, radius));
                EntityProp prop = new EntityProp(target.getScene(), propExcel, null, null);
                prop.getPos().set(pos);
                prop.setState(PropState.Open);
                
                target.getScene().addEntity(prop, true);
            }
            
            // Send message when done
            this.sendMessage(sender, "Spawning " + amount + " props");
            return;
        }

        this.sendMessage(sender, "Error: Invalid id");
    }

}
