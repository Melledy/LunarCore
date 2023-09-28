package emu.lunarcore.server.packet.send;

import java.util.Collection;

import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.avatar.HeroPath;
import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.AvatarSyncOuterClass.AvatarSync;
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

    public PacketPlayerSyncScNotify(GameAvatar avatar) {
        this();

        var avatarSync = AvatarSync.newInstance()
                .addAvatarList(avatar.toProto());

        var data = PlayerSyncScNotify.newInstance()
                .setAvatarSync(avatarSync);

        this.setData(data);
    }

    public PacketPlayerSyncScNotify(GameAvatar avatar, GameItem item) {
        this();

        var avatarSync = AvatarSync.newInstance()
                .addAvatarList(avatar.toProto());

        var data = PlayerSyncScNotify.newInstance()
                .setAvatarSync(avatarSync);

        this.addItemToProto(data, item);

        this.setData(data);
    }

    public PacketPlayerSyncScNotify(GameItem item) {
        this();

        var data = PlayerSyncScNotify.newInstance();

        this.addItemToProto(data, item);

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
    
    public PacketPlayerSyncScNotify(HeroPath heroPath) {
        this();

        var data = PlayerSyncScNotify.newInstance()
                .addBasicTypeInfoList(heroPath.toProto());

        this.setData(data);
    }
}
