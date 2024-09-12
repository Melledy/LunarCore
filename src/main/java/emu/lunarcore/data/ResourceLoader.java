package emu.lunarcore.data;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import emu.lunarcore.data.config.*;

import emu.lunarcore.data.config.rogue.RogueDialogueEventConfigInfo;
import emu.lunarcore.data.config.rogue.RogueNPCConfigInfo;
import org.reflections.Reflections;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import emu.lunarcore.LunarCore;
import emu.lunarcore.data.ResourceDeserializers.LunarCoreDoubleDeserializer;
import emu.lunarcore.data.ResourceDeserializers.LunarCoreHashDeserializer;
import emu.lunarcore.data.config.FloorInfo.FloorGroupSimpleInfo;
import emu.lunarcore.data.custom.ActivityScheduleData;
import emu.lunarcore.util.Utils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

public class ResourceLoader {
    private static boolean loaded = false;

    // Special gson factory we create for loading resources
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(double.class, new LunarCoreDoubleDeserializer())
            .registerTypeAdapter(long.class, new LunarCoreHashDeserializer())
            .create();

    // Load all resources
    public static void loadAll() {
        // Make sure we don't load more than once
        if (loaded) return;

        // Create data folder if it doesnt exist when starting the server
        checkDataFolder();
        // Start loading resources
        loadResources();
        // Load floor infos after resources
        loadFloorInfos();
        // Load maze abilities
        loadMazeAbilities();
        // Load rogue maps
        loadRogueMapGen();
        // Load activity schedule config
        loadActivityScheduleConfig();
        // Load rogue dialogue events
        loadRogueDialogueEvent();
        
        // Done
        loaded = true;
        LunarCore.getLogger().info("Resource loading complete");
    }
    
    private static void checkDataFolder() {
        File dir = new File(LunarCore.getConfig().getDataDir());
        if (!dir.exists()) {
            dir.mkdir();
        }
    }
    
    private static Int2ObjectMap<?> getMapForExcel(Class<?> dataClass, Class<?> resourceDefinition) {
        Int2ObjectMap<?> map = null;

        try {
            Field field = dataClass.getDeclaredField(Utils.lowerCaseFirstChar(resourceDefinition.getSimpleName()) + "Map");
            field.setAccessible(true);

            map = (Int2ObjectMap<?>) field.get(null);

            field.setAccessible(false);
        } catch (Exception e) {

        }

        return map;
    }

    private static List<Class<?>> getResourceDefClasses() {
        Reflections reflections = new Reflections(ResourceLoader.class.getPackage().getName());
        Set<?> classes = reflections.getSubTypesOf(GameResource.class);

        List<Class<?>> classList = new ArrayList<>(classes.size());
        classes.forEach(o -> {
            Class<?> c = (Class<?>) o;
            if (c.getAnnotation(ResourceType.class) != null) {
                classList.add(c);
            }
        });

        classList.sort((a, b) -> b.getAnnotation(ResourceType.class).loadPriority().value() - a.getAnnotation(ResourceType.class).loadPriority().value());

        return classList;
    }

    private static void loadResources() {
        for (Class<?> resourceDefinition : getResourceDefClasses()) {
            ResourceType type = resourceDefinition.getAnnotation(ResourceType.class);

            if (type == null) {
                continue;
            }

            @SuppressWarnings("rawtypes")
            Int2ObjectMap map = ResourceLoader.getMapForExcel(type.gameDataClass(), resourceDefinition);

            try {
                loadFromResource(resourceDefinition, type, map);
            } catch (FileNotFoundException e) {
                LunarCore.getLogger().error("Resource file not found: {}.", Arrays.toString(type.name()));
            } catch (Exception e) {
                LunarCore.getLogger().error("Error loading resource file: " + Arrays.toString(type.name()), e);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private static void loadFromResource(Class<?> c, ResourceType type, Int2ObjectMap map) throws Exception {
        int count = 0;

        for (String name : type.name()) {
            count += loadFromResource(c, type, name, map);
        }

        LunarCore.getLogger().info("Loaded " + count + " " + c.getSimpleName() + "s.");
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static <T> int loadFromResource(Class<T> c, ResourceType type, String fileName, Int2ObjectMap map) throws Exception {
        String file = LunarCore.getConfig().getResourceDir() + "/ExcelOutput/" + fileName;

        // Load reader from file
        try (InputStreamReader fileReader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            // Setup variables
            Stream<T> stream = null;

            // Determine format of json
            JsonElement json = JsonParser.parseReader(fileReader);

            if (json.isJsonArray()) {
                // Parse list
                List<T> excels = gson.fromJson(json, TypeToken.getParameterized(List.class, c).getType());
                stream = excels.stream();
            } else if (json.isJsonObject()) {
                // Check if object is map or a nested map
                boolean isMap = true;

                var it = json.getAsJsonObject().asMap().entrySet().iterator();
                if (it.hasNext()) {
                    var it2 = it.next().getValue().getAsJsonObject().asMap().entrySet().iterator();
                    String key = it2.next().getKey();
                    try {
                        Integer.parseInt(key);
                        isMap = false;
                    } catch (Exception ex) {

                    }
                }

                // Parse json
                if (isMap) {
                    // Map
                    Map<Integer, T> excels = gson.fromJson(json, TypeToken.getParameterized(Map.class, Integer.class, c).getType());
                    stream = excels.values().stream();
                } else {
                    // Nested Map
                    Map<Integer, Map<Integer, T>> excels = gson.fromJson(json, TypeToken.getParameterized(Map.class, Integer.class, TypeToken.getParameterized(Map.class, Integer.class, c).getType()).getType());
                    stream = excels.values().stream().flatMap(m -> m.values().stream());
                }
            } else {
                throw new Exception("Invalid excel file: " + fileName);
            }

            // Sanity check
            if (stream == null) return 0;

            // Mutable integer
            AtomicInteger count = new AtomicInteger();

            stream.forEach(o -> {
                GameResource res = (GameResource) o;
                res.onLoad();

                count.getAndIncrement();

                if (map != null) {
                    map.put(res.getId(), res);
                }
            });

            if (map != null) {
                map.forEach((k, v) -> {
                    if (v instanceof GameResource) {
                        ((GameResource) v).onFinalize();
                    }
                });
            }

            return count.get();
        }
    }

 // Might be better to cache
    private static void loadFloorInfos() {
        // Load floor infos
        LunarCore.getLogger().info("Loading floor infos... this may take a while.");
        File floorDir = new File(LunarCore.getConfig().getResourceDir() + "/Config/LevelOutput/RuntimeFloor/");
        boolean missingGroupInfos = false;

        if (!floorDir.exists()) {
            LunarCore.getLogger().warn("Floor infos are missing, please check your resources folder: {resources}/Config/LevelOutput/RuntimeFloor. Teleports and natural world spawns may not work!");
            return;
        }

        // Load floor infos
        for (var excel : GameData.getMapEntranceExcelMap().values()) {
            String name = "P" + excel.getPlaneID() + "_F" + excel.getFloorID();
            File file = new File(LunarCore.getConfig().getResourceDir() + "/Config/LevelOutput/RuntimeFloor/" + name + ".json");
            
            if (!file.exists()) {
                LunarCore.getLogger().warn("Missing floor info: " + name);
                continue;
            }
            
            try (FileReader reader = new FileReader(file)) {
                FloorInfo floor = gson.fromJson(reader, FloorInfo.class);
                GameData.getFloorInfos().put(name, floor);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Load group infos
        for (FloorInfo floor : GameData.getFloorInfos().values()) {
            for (FloorGroupSimpleInfo simpleGroup : floor.getSimpleGroupList()) {
                // Dont load "deprecated" groups
                if (simpleGroup.isIsDelete()) {
                    continue;
                }
                
                // Get file from resource directory
                File file = new File(LunarCore.getConfig().getResourceDir() + "/" + simpleGroup.getGroupPath());
                if (!file.exists()) continue;

                // TODO optimize
                try (FileReader reader = new FileReader(file)) {
                    GroupInfo group = gson.fromJson(reader, GroupInfo.class);
                    group.setId(simpleGroup.getID());
                    
                    // Hacky way to load only groups that arent required for main missions
                    if (group.getOwnerMainMissionID() > 0 && group.getOwnerMainMissionID() < 2000000) {
                        continue;
                    }
                    
                    // Load groups into the floor info
                    floor.getGroupList().add(group);
                    floor.getGroups().put(simpleGroup.getID(), group);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            // Check if we are missing group infos
            if (floor.getGroups().size() == 0) {
                LunarCore.getLogger().warn("Floor " + floor.getFloorID() + " is missing group infos.");
                missingGroupInfos = true;
            }

            // Post load callback to cache floor info
            floor.onLoad();
        }
        
        // Notify the server owner if we are missing any files
        if (missingGroupInfos) {
            LunarCore.getLogger().warn("Group infos are missing, please check your resources folder: {resources}/Config/LevelOutput/SharedRuntimeGroup. Teleports, monster battles, and natural world spawns may not work!");
        }
        
        // Done
        LunarCore.getLogger().info("Loaded " + GameData.getFloorInfos().size() + " floor infos.");
    }

    // Might be better to cache
    private static void loadMazeAbilities() {
        // Loaded configs count
        int count = 0;
        
        // Load summon unit configs
        for (var summonUnitExcel : GameData.getSummonUnitExcelMap().values()) {
            if (summonUnitExcel.isIsClient()) {
                count++;
                continue;
            }
            
            // Get file
            File file = new File(LunarCore.getConfig().getResourceDir() + "/" + summonUnitExcel.getJsonPath());
            if (!file.exists()) continue;
            
            try (FileReader reader = new FileReader(file)) {
                SummonUnitInfo info = gson.fromJson(reader, SummonUnitInfo.class);
                info.buildMazeSkillActions();
                
                summonUnitExcel.setInfo(info);
                count++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // Notify the server owner if we are missing any files
        /*
        if (count < GameData.getSummonUnitExcelMap().size()) {
            LunarCore.getLogger().warn("Summon unit configs are missing, please check your resources folder: {resources}/Config/ConfigSummonUnit. Character summon techniques may not work!");
        }
        */
        
        // Reset loaded count
        count = 0;

        // Load maze abilities
        for (var avatarExcel : GameData.getAvatarExcelMap().values()) {
            // Get file
            File file = new File(LunarCore.getConfig().getResourceDir() + "/Config/ConfigAdventureAbility/LocalPlayer/LocalPlayer_" + avatarExcel.getNameKey() + "_Ability.json");
            if (!file.exists()) continue;

            try (FileReader reader = new FileReader(file)) {
                SkillAbilityInfo avatarSkills = gson.fromJson(reader, SkillAbilityInfo.class);

                if (avatarSkills.parse(avatarExcel)) {
                    count++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // Notify the server owner if we are missing any files
        if (count < GameData.getAvatarExcelMap().size()) {
            LunarCore.getLogger().warn("Maze abilities are missing, please check your resources folder: {resources}/Config/ConfigAdventureAbility/LocalPlayer. Character techniques may not work!");
        }

        // Done
        LunarCore.getLogger().info("Loaded " + count + " maze abilities for avatars.");
    }
    
    // Might be better to cache
    private static void loadRogueDialogueEvent() {
        // Loaded configs count
        int count = 0;
        // Load dialogue event configs
        for (var npcEventExcel : GameData.getRogueNPCExcelMap().values()) {

            // Get file
            if (npcEventExcel.getNPCJsonPath().isEmpty()) {
                count++;
                continue;
            }
            File file = new File(LunarCore.getConfig().getResourceDir() + "/" + npcEventExcel.getNPCJsonPath());
            if (!file.exists()) {
                continue;
            }

            try (FileReader reader = new FileReader(file)) {
                RogueNPCConfigInfo info = gson.fromJson(reader, RogueNPCConfigInfo.class);
                npcEventExcel.setRogueNpcConfig(info);
                
                // Load dialogue option
                for (var dialogue : info.DialogueList) {
                    if (dialogue.getOptionPath() == null) {
                        count++;
                        continue;
                    }
                    File optionFile = new File(LunarCore.getConfig().getResourceDir() + "/" + dialogue.getOptionPath());
                    if (!file.exists()) {
                        continue;
                    }
                    RogueDialogueEventConfigInfo optionInfo = gson.fromJson(new FileReader(optionFile), RogueDialogueEventConfigInfo.class);
                    dialogue.setOptionInfo(optionInfo);
                }
                count++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Notify the server owner if we are missing any files
        if (count < GameData.getRogueDialogueEventList().size()) {
            LunarCore.getLogger().warn("Rogue dialogue event configs are missing, please check your resources folder: {resources}/Config/Level/Rogue/. Rogue event may not work!");
        }
        // Done
        LunarCore.getLogger().info("Loaded " + count + " rogue events.");
    }

    private static void loadRogueMapGen() {
        File file = new File(LunarCore.getConfig().getDataDir() + "/RogueMapGen.json");
        if (!file.exists()) {
            LunarCore.getLogger().warn("RogueMapGen not found in data folder. Simulated universe will not work.");
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            Map<Integer, int[]> rogue = gson.fromJson(reader, TypeToken.getParameterized(Map.class, Integer.class, int[].class).getType());

            for (var entry : rogue.entrySet()) {
                GameDepot.getRogueMapGen().put(entry.getKey().intValue(), entry.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void loadActivityScheduleConfig() {
        File file = new File(LunarCore.getConfig().getDataDir() + "/ActivityScheduling.json");
        if (!file.exists()) return;

        try (FileReader reader = new FileReader(file)) {
            List<ActivityScheduleData> activityScheduleConfig = gson.fromJson(reader, TypeToken.getParameterized(List.class, ActivityScheduleData.class).getType());
            GameDepot.getActivityScheduleExcels().addAll(activityScheduleConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
