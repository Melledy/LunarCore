package emu.lunarcore.game.gacha;

import java.io.FileReader;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import emu.lunarcore.GameConstants;
import emu.lunarcore.LunarCore;
import emu.lunarcore.data.GameData;
import emu.lunarcore.data.excel.ItemExcel;
import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.enums.ItemMainType;
import emu.lunarcore.game.enums.ItemRarity;
import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.GachaItemOuterClass.GachaItem;
import emu.lunarcore.proto.GetGachaInfoScRspOuterClass.GetGachaInfoScRsp;
import emu.lunarcore.proto.ItemListOuterClass.ItemList;
import emu.lunarcore.proto.ItemOuterClass.Item;
import emu.lunarcore.server.game.BaseGameService;
import emu.lunarcore.server.game.GameServer;
import emu.lunarcore.server.packet.Retcode;
import emu.lunarcore.server.packet.send.PacketDoGachaScRsp;
import emu.lunarcore.util.JsonUtils;
import emu.lunarcore.util.Utils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.Getter;

@Getter
public class GachaService extends BaseGameService {
    private final Int2ObjectMap<GachaBanner> gachaBanners;
    private WatchService watchService;
    private Thread watchThread;

    private int[] yellowAvatars = new int[] {1003, 1004, 1101, 1107, 1104, 1209, 1211};
    private int[] yellowWeapons = new int[] {23000, 23002, 23003, 23004, 23005, 23012, 23013};
    private int[] purpleAvatars = new int[] {1001, 1002, 1008, 1009, 1013, 1103, 1105, 1106, 1108, 1109, 1110, 1111, 1201, 1202, 1206, 1207, 1210};
    private int[] purpleWeapons = new int[] {21000, 21001, 21002, 21003, 21004, 21005, 21006, 21007, 21008, 21009, 21010, 21011, 21012, 21013, 21014, 21015, 21016, 21017, 21018, 21019, 21020};
    private int[] blueWeapons = new int[] {20000, 20001, 20002, 20003, 20004, 20005, 20006, 20007, 20008, 20009, 20010, 20011, 20012, 20013, 20014, 20015, 20016, 20017, 20018, 20019, 20020};
    private int[] defaultFeaturedIds = new int[] {23002, 1003, 1101, 1104, 23000, 23003};
    
    private static int starglightId = 252;
    private static int embersId = 251;

    public GachaService(GameServer server) {
        super(server);
        this.gachaBanners = new Int2ObjectOpenHashMap<>();
        
        try {
            this.watch();
        } catch (Exception e) {
            LunarCore.getLogger().error("Watch service error: ", e);
        }
    }

    public int randomRange(int min, int max) {
        return ThreadLocalRandom.current().nextInt(max - min + 1) + min;
    }

    public int getRandom(int[] array) {
        return array[randomRange(0, array.length - 1)];
    }
    
    private String getBannerFileName() {
        return LunarCore.getConfig().getDataDir() + "/Banners.json";
    }
    
    public void watch() throws Exception {
        // Load banners first
        this.loadBanners();
        
        // Create watch service
        this.watchService = FileSystems.getDefault().newWatchService();
        Path watchPath = Paths.get(LunarCore.getConfig().getDataDir());
        watchPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);

        // Start watch thread
        this.watchThread = new Thread(() -> {
            WatchKey key = null;
            try {
                while ((key = watchService.take()) != null) {
                    for (var event : key.pollEvents()) {
                        if (event.context() == null) {
                            continue;
                        }
                        
                        if (event.context() instanceof Path path && path.toString().equals("Banners.json")) {
                            loadBanners();
                        }
                    }
                    
                    key.reset();
                }
            } catch (Exception e) {
                LunarCore.getLogger().error("Watch service thread error: ", e);
            }
        });
        this.watchThread.start();
    }

    public synchronized void loadBanners() {
        this.getGachaBanners().clear();
        
        try (FileReader fileReader = new FileReader(getBannerFileName())) {
            List<GachaBanner> banners = JsonUtils.loadToList(fileReader, GachaBanner.class);
            for (GachaBanner banner : banners) {
                getGachaBanners().put(banner.getId(), banner);
            }
        } catch (Exception e) {
            LunarCore.getLogger().warn("No gacha banners loaded!");
        }
    }

    public synchronized void doPulls(Player player, int gachaId, int times) {
        // Sanity checks
        if (times != 10 && times != 1) return;
        
        // Prevent player from using gacha if they are at max light cones
        if (player.getInventory().getTabByItemType(ItemMainType.Equipment).getSize() >= player.getInventory().getTabByItemType(ItemMainType.Equipment).getMaxCapacity()) {
            player.sendPacket(new PacketDoGachaScRsp(Retcode.EQUIPMENT_EXCEED_LIMIT));
            return;
        }

        // Get banner
        GachaBanner banner = this.getGachaBanners().get(gachaId);
        if (banner == null) {
            player.sendPacket(new PacketDoGachaScRsp(Retcode.GACHA_ID_NOT_EXIST));
            return;
        }

        // Spend currency
        if (banner.getGachaType().getCostItem() > 0) {
            GameItem costItem = player.getInventory().getMaterialByItemId(banner.getGachaType().getCostItem());
            if (costItem == null || costItem.getCount() < times) {
                player.sendPacket(new PacketDoGachaScRsp(Retcode.FAIL));
                return;
            }

            player.getInventory().removeItem(costItem, times);
        }
        
        // Add gacha ceiling
        if (banner.getGachaType() == GachaType.Normal || banner.getGachaType() == GachaType.Newbie) {
            player.getGachaInfo().addCeilingNum(times);
            player.save();
        }

        // Roll
        PlayerGachaBannerInfo gachaInfo = player.getGachaInfo().getBannerInfo(banner.getGachaType());
        IntList wonItems = new IntArrayList(times);

        for (int i = 0; i < times; i++) {
            int random = this.randomRange(1, 10000);
            int itemId = 0;

            int bonusYellowChance = gachaInfo.getPity5() >= 74 ? 100 * (gachaInfo.getPity5() - 73): 0;
            int yellowChance = 60 + (int) Math.floor(100f * (gachaInfo.getPity5() / 73f)) + bonusYellowChance;
            int purpleChance = 10000 - (510 + (int) Math.floor(790f * (gachaInfo.getPity4() / 8f)));

            if (random <= yellowChance || gachaInfo.getPity5() >= 89) {
                if (banner.getRateUpItems5().length > 0) {
                    int eventChance = this.randomRange(1, 100);

                    if (eventChance <= banner.getEventChance() || gachaInfo.getFailedFeaturedItemPulls() >= 1) {
                        itemId = getRandom(banner.getRateUpItems5());
                        gachaInfo.setFailedFeaturedItemPulls(0);
                    } else {
                        // Lost the 50/50... rip
                        gachaInfo.addFailedFeaturedItemPulls(1);
                    }
                }

                if (itemId == 0) {
                    int typeChance = this.randomRange(banner.getGachaType().getMinItemType(), banner.getGachaType().getMaxItemType());
                    if (typeChance == 1) {
                        itemId = getRandom(this.yellowAvatars);
                    } else {
                        itemId = getRandom(this.yellowWeapons);
                    }
                }

                // Pity
                gachaInfo.addPity4(1);
                gachaInfo.setPity5(0);
            } else if (random >= purpleChance || gachaInfo.getPity4() >= 9) {
                if (banner.getRateUpItems4().length > 0) {
                    int eventChance = this.randomRange(1, 100);

                    if (eventChance >= 50) {
                        itemId = getRandom(banner.getRateUpItems4());
                    }
                }

                if (itemId == 0) {
                    int typeChance = this.randomRange(banner.getGachaType().getMinItemType(), banner.getGachaType().getMaxItemType());
                    if (typeChance == 1) {
                        itemId = getRandom(this.purpleAvatars);
                    } else {
                        itemId = getRandom(this.purpleWeapons);
                    }
                }

                // Pity
                gachaInfo.addPity5(1);
                gachaInfo.setPity4(0);
            } else {
                itemId = getRandom(this.blueWeapons);

                // Pity
                gachaInfo.addPity4(1);
                gachaInfo.addPity5(1);
            }

            // Add winning item
            wonItems.add(itemId);
        }

        // Add to character
        List<GachaItem> list = new ArrayList<>();
        int stardust = 0, starglitter = 0;

        for (int itemId : wonItems) {
            ItemExcel itemData = GameData.getItemExcelMap().get(itemId);
            if (itemData == null) {
                continue;
            }

            // Create gacha item
            GachaItem gachaItem = GachaItem.newInstance();
            gachaItem.setTransferItemList(ItemList.newInstance());
            gachaItem.setTokenItem(ItemList.newInstance());
            int addStardust = 0, addStarglitter = 0;

            // Dupe check
            if (itemData.getItemMainType() == ItemMainType.AvatarCard) {
                int avatarId = itemData.getId();
                GameAvatar avatar = player.getAvatars().getAvatarById(avatarId);
                if (avatar != null) {
                    int dupeLevel = avatar.getRank();
                    int dupeItemId = avatar.getExcel().getRankUpItemId(); 
                    GameItem dupeItem = player.getInventory().getMaterialByItemId(avatar.getExcel().getRankUpItemId());
                    if (dupeItem != null) {
                        dupeLevel += dupeItem.getCount();
                    }

                    if (dupeLevel < 6) {
                        // Not max const
                        addStarglitter = 8;
                        // Add 1 rank
                        gachaItem.getTransferItemList().addItemList(Item.newInstance().setItemId(dupeItemId).setNum(1));
                        player.getInventory().addItem(dupeItemId, 1);
                    } else {
                        // Is max rank
                        addStarglitter = 20;
                    }

                    if (itemData.getRarity() == ItemRarity.SuperRare) {
                        addStarglitter *= 2.5;
                    }
                } else {
                    // New
                    gachaItem.setIsNew(true);
                }
            } else {
                // Is weapon
                switch (itemData.getRarity()) {
                case SuperRare:
                    addStarglitter = 40;
                    break;
                case VeryRare:
                    addStarglitter = 8;
                    break;
                case Rare:
                    addStardust = 20;
                    break;
                default:
                    break;
                }
            }

            // Create item
            GameItem item = new GameItem(itemData);
            gachaItem.setGachaItem(item.toProto());
            player.getInventory().addItem(item);

            // Add embers/starlight
            stardust += addStardust;
            starglitter += addStarglitter;

			if (addStardust > 0) {
				gachaItem.getTokenItem().addItemList(Item.newInstance().setItemId(embersId).setNum(addStardust));
			} if (addStarglitter > 0) {
			    gachaItem.getTokenItem().addItemList(Item.newInstance().setItemId(starglightId).setNum(addStarglitter));
			}
			
			// Add to gacha item list rsp
			list.add(gachaItem);
        }

        // Add stardust/starglitter
        if (stardust > 0) {
            player.getInventory().addItem(embersId, stardust);
        } if (starglitter > 0) {
            player.getInventory().addItem(starglightId, starglitter);
        }

        // Packets
        player.sendPacket(new PacketDoGachaScRsp(player, banner, times, list));
    }
    
    public List<GameItem> exchangeGachaCeiling(Player player, int avatarId) {
        // Sanity check
        if (player.getGachaInfo().getCeilingNum() < GameConstants.GACHA_CEILING_MAX || player.getGachaInfo().isCeilingClaimed()) {
            return null;
        }
        
        // Make sure the player is getting a valid avatar
        if (!Utils.arrayContains(this.getYellowAvatars(), avatarId)) {
            return null;
        }
        
        // Add items
        List<GameItem> items = new ArrayList<>();
        
        if (player.getAvatars().hasAvatar(avatarId)) {
            // Add eidolon if player already has the avatar
            items.add(new GameItem(avatarId + 10000));
        } else {
            items.add(new GameItem(avatarId));
        }
        
        player.getInventory().addItems(items);
        player.getGachaInfo().setCeilingClaimed(true);
        player.save();
        
        return items;
    }

    public synchronized GetGachaInfoScRsp toProto(Player player) {
        var proto = GetGachaInfoScRsp.newInstance();

        for (GachaBanner banner : getGachaBanners().values()) {
            proto.addGachaInfoList(banner.toProto(this, player));
        }

        return proto;
    }
}
