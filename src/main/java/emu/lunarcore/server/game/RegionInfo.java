package emu.lunarcore.server.game;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import emu.lunarcore.LunarCore;
import emu.lunarcore.database.AccountDatabaseOnly;
import emu.lunarcore.proto.RegionEntryOuterClass.RegionEntry;
import lombok.Getter;
import lombok.Setter;

@Getter
@AccountDatabaseOnly
@Entity(value = "regions", useDiscriminator = false)
public class RegionInfo {
    @Id private String id;
    private String name;
    private String desc;

    private String gateAddress;

    @Setter private boolean up;

    @Deprecated
    public RegionInfo() {
        // Morphia only
    }

    public RegionInfo(GameServer server) {
        this.id = server.getServerConfig().getId();
        this.name = server.getServerConfig().getName();
        this.desc = server.getServerConfig().getDescription();
        this.gateAddress = LunarCore.getHttpServer().getServerConfig().getDisplayAddress();
        this.up = true;
    }
    
    public RegionEntry toProto() {
        var proto = RegionEntry.newInstance()
                .setName(this.getId())
                .setDispatchUrl(this.getGateAddress() + "/query_gateway")
                .setEnvType("2")
                .setDisplayName(this.getName());
        
        return proto;
    }

    public void save() {
        LunarCore.getAccountDatabase().save(this);
    }
}
