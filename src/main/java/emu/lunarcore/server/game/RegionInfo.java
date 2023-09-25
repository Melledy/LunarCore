package emu.lunarcore.server.game;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import emu.lunarcore.LunarRail;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity(value = "regions", useDiscriminator = false)
public class RegionInfo {
    @Id private String id;
    private String name;
    private String desc;

    private String gateAddress;
    private String gameAddress;

    @Setter private boolean up;

    @Deprecated
    public RegionInfo() {
        // Morphia only
    }

    public RegionInfo(GameServer server) {
        this.id = server.getServerConfig().getId();
        this.name = server.getServerConfig().getName();
        this.desc = server.getServerConfig().getDescription();
        this.gateAddress = LunarRail.getHttpServer().getServerConfig().getDisplayAddress();
        this.gameAddress = server.getServerConfig().getDisplayAddress();
        this.up = true;
    }

    public void save() {
        LunarRail.getAccountDatabase().save(this);
    }
}
