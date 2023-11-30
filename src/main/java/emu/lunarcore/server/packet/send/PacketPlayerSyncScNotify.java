package emu.lunarcore.server.packet.send;

import java.util.Collection;

import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.avatar.AvatarHeroPath;
import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.BoardDataSyncOuterClass.BoardDataSync;
import emu.lunarcore.proto.PlayerSyncScNotifyOuterClass.PlayerSyncScNotify;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketPlayerSyncScNotify extends BasePacket {

    @Deprecated // This constructor does not create a proto
    private PacketPlayerSyncScNotify() {
        super(CmdId.PlayerSyncScNotify);
    }

    public PacketPlayerSyncScNotify(Player player) {
        this();

        var data = PlayerSyncScNotify.newInstance()
                .setBasicInfo(player.toProto());

        this.setData(data);
    }
    
    public PacketPlayerSyncScNotify(BoardDataSync boardData) {
        this();

        var data = PlayerSyncScNotify.newInstance()
                .setBoardDataSync(boardData);

        this.setData(data);
    }

    public PacketPlayerSyncScNotify(GameAvatar avatar) {
        this();

        var data = PlayerSyncScNotify.newInstance();
        data.getMutableAvatarSync().addAvatarList(avatar.toProto());
        
        // Also update hero basic type info if were updating the main character
        if (avatar.getHeroPath() != null) {
            data.getMutableBasicTypeInfoList().add(avatar.getHeroPath().toProto());
        }

        this.setData(data);
    }

    public PacketPlayerSyncScNotify(GameAvatar avatar, GameItem item) {
        this();

        var data = PlayerSyncScNotify.newInstance();
        data.getMutableAvatarSync().addAvatarList(avatar.toProto());

        this.addItemToProto(data, item);

        this.setData(data);
    }

    public PacketPlayerSyncScNotify(GameItem item) {
        this();

        var data = PlayerSyncScNotify.newInstance();

        this.addItemToProto(data, item);

        this.setData(data);
    }
    
    public PacketPlayerSyncScNotify(GameAvatar... avatars) { // Ugly workaround
        this();

        var data = PlayerSyncScNotify.newInstance();
        
        for (var avatar : avatars) {
            // Sync avatar
            data.getMutableAvatarSync().addAvatarList(avatar.toProto());
            
            // Also update hero basic type info if were updating the main character
            if (avatar.getHeroPath() != null) {
                data.getMutableBasicTypeInfoList().add(avatar.getHeroPath().toProto());
            }
        }
        
        this.setData(data);
    }

    public PacketPlayerSyncScNotify(Collection<GameItem> items) {
        this();

        var data = PlayerSyncScNotify.newInstance();

        for (GameItem item : items) {
            this.addItemToProto(data, item);
        }

        this.setData(data);
    }

    private void addItemToProto(PlayerSyncScNotify data, GameItem item) {
        switch (item.getExcel().getItemMainType()) {
            case Material -> {
                data.addMaterialList(item.toMaterialProto());
            }
            case Relic -> {
                if (item.getCount() > 0) {
                    data.addRelicList(item.toRelicProto());
                } else {
                    data.addDelRelicList(item.getInternalUid());
                }
            }
            case Equipment -> {
                if (item.getCount() > 0) {
                    data.addEquipmentList(item.toEquipmentProto());
                } else {
                    data.addDelEquipmentList(item.getInternalUid());
                }
            }
            default -> {
    
            }
        }
    }
    
    public PacketPlayerSyncScNotify(AvatarHeroPath heroPath) {
        this();

        var data = PlayerSyncScNotify.newInstance()
                .addBasicTypeInfoList(heroPath.toProto());

        this.setData(data);
    }
}
