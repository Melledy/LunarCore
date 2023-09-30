package emu.lunarcore.commands;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.excel.ItemExcel;
import emu.lunarcore.data.excel.NpcMonsterExcel;
import emu.lunarcore.data.excel.StageExcel;
import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.game.inventory.ItemMainType;
import emu.lunarcore.game.inventory.ItemSubType;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.game.scene.entity.EntityMonster;
import emu.lunarcore.util.Position;

@SuppressWarnings("unused")
public class PlayerCommands {
    private static HashMap<String, PlayerCommand> list = new HashMap<>();

    static {
        try {
            // Look for classes
            for (Class<?> cls : PlayerCommands.class.getDeclaredClasses()) {
                // Get non abstract classes
                if (!Modifier.isAbstract(cls.getModifiers())) {
                    Command commandAnnotation = cls.getAnnotation(Command.class);
                    PlayerCommand command = (PlayerCommand) cls.getConstructor().newInstance();

                    if (commandAnnotation != null) {
                        command.setLevel(commandAnnotation.gmLevel());
                        for (String alias : commandAnnotation.aliases()) {
                            if (alias.length() == 0) {
                                continue;
                            }

                            String commandName = "!" + alias;
                            list.put(commandName, command);
                            commandName = "/" + alias;
                            list.put(commandName, command);
                        }
                    }

                    String commandName = "!" + cls.getSimpleName().toLowerCase();
                    list.put(commandName, command);
                    commandName = "/" + cls.getSimpleName().toLowerCase();
                    list.put(commandName, command);
                }

            }
        } catch (Exception e) {

        }
    }

    public static void handle(Player player, String msg) {
        String[] split = msg.split(" ");

        // End if invalid
        if (split.length == 0) {
            return;
        }

        //
        String first = split[0].toLowerCase();
        PlayerCommand c = PlayerCommands.list.get(first);

        if (c != null) {
            // Execute
            int len = Math.min(first.length() + 1, msg.length());
            c.execute(player, msg.substring(len));
        } else {
            player.dropMessage("Error: Invalid command!");
        }
    }

    public static abstract class PlayerCommand {
        // GM level required to use this command
        private int level;
        protected int getLevel() { return this.level; }
        protected void setLevel(int minLevel) { this.level = minLevel; }

        // Main
        public abstract void execute(Player player, String raw);
    }

    // ================ Commands ================

    @Command(aliases = {"g", "item"}, desc = "/give [item id] [count] - Gives {count} amount of {item id}")
    public static class Give extends PlayerCommand {
        @Override
        public void execute(Player player, String raw) {
            String[] split = raw.split(" ");
            int itemId = 0, count = 1;

            try {
                itemId = Integer.parseInt(split[0]);
            } catch (Exception e) {
                itemId = 0;
            }

            try {
                count = Math.max(Math.min(Integer.parseInt(split[1]), Integer.MAX_VALUE), 1);
            } catch (Exception e) {
                count = 1;
            }

            // Give
            ItemExcel itemData = GameData.getItemExcelMap().get(itemId);
            GameItem item;

            if (itemData == null) {
                player.dropMessage("Error: Item data not found");
                return;
            }

            if (itemData.isEquippable()) {
                List<GameItem> items = new LinkedList<>();
                for (int i = 0; i < count; i++) {
                    item = new GameItem(itemData);
                    //items.add(item);
                    player.getInventory().addItem(item);
                }
                // TODO add item hint packet
            } else {
                item = new GameItem(itemData, count);
                player.getInventory().addItem(item);
                // TODO add item hint packet
            }

            player.dropMessage("Giving you " + count + " of " + itemId);
        }
    }
    
    @Command(aliases = {"wl", "el"}, desc = "/worldlevel [world level]")
    public static class WorldLevel extends PlayerCommand {
        @Override
        public void execute(Player player, String raw) {
            int level = 0;

            try {
                level = Integer.parseInt(raw);
            } catch (Exception e) {
                level = 0;
            }

            level = Math.min(Math.max(level, 0), 6);

            // Set world level
            player.setWorldLevel(level);
            player.dropMessage("Set world level to " + level);
        }
    }
    
    @Command(aliases = {"ga"}, desc = "/giveall {materials|avatars}")
    public static class GiveAll extends PlayerCommand {
        @Override
        public void execute(Player player, String raw) {
            switch (raw) {
                case "materials":
                    // Character/Relic/Lightcone upgrade materials
                    for (ItemExcel excel : GameData.getItemExcelMap().values()) {
                        int purpose = excel.getPurposeType();
                        if (purpose >= 1 && purpose <= 7) {
                            player.getInventory().addItem(excel, 1000);
                        }
                    }
                    // Credits
                    player.getInventory().addItem(2, 10_000_000);
                    break;
                case "avatars":
                    // All avatars and their eidolons
                    for (ItemExcel excel : GameData.getItemExcelMap().values()) {
                        if (excel.getItemMainType() == ItemMainType.AvatarCard) {
                            player.getInventory().addItem(excel, 1);
                        } else if (excel.getItemSubType() == ItemSubType.Eidolon) {
                            player.getInventory().addItem(excel, 6);
                        }
                    }
                    break;
            }
        }
    }

    /* Temporarily disabled as spawned monsters need 
    @Command(desc = "/spawn [monster id] [count] - Creates {count} amount of {item id}")
    public static class Spawn extends PlayerCommand {
        @Override
        public void execute(Player player, String raw) {
            String[] split = raw.split(" ");
            int monsterId = 0, stageId = 2;

            try {
                monsterId = Integer.parseInt(split[0]);
            } catch (Exception e) {
                monsterId = 0;
            }

            try {
                stageId = Integer.parseInt(split[1]);
            } catch (Exception e) {
                stageId = 2;
            }

            // TODO
            NpcMonsterExcel excel = GameData.getNpcMonsterExcelMap().get(monsterId);
            if (excel == null) {
                player.dropMessage("Npc monster id not found!");
                return;
            }

            StageExcel stage = GameData.getStageExcelMap().get(stageId);
            if (stage == null) {
                player.dropMessage("Stage id not found!");
                return;
            }

            Position pos = player.getPos().clone();
            pos.setX(pos.getX() + 50);

            // Add to scene
            EntityMonster monster = new EntityMonster(excel, stage, pos);
            player.getScene().addMonster(monster);
        }
    }
    */
}
