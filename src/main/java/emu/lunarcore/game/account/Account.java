package emu.lunarcore.game.account;

import dev.morphia.annotations.*;
import emu.lunarcore.LunarRail;
import emu.lunarcore.util.Crypto;
import emu.lunarcore.util.Snowflake32;
import emu.lunarcore.util.Utils;
import lombok.Getter;

@Getter
@Entity(value = "accounts", useDiscriminator = false)
public class Account {
    @Id private String uid;

    @Indexed(options = @IndexOptions(unique = true))
    @Collation(locale = "simple", caseLevel = true)
    private String username;
    private String password; // Unused for now

    private int reservedPlayerUid;

    private String comboToken; // Combo token
    private String dispatchToken; // Session token for dispatch server

    @Deprecated
    public Account() {

    }

    public Account(String username) {
        this.uid = Long.toString(Snowflake32.newUid());
        this.username = username;
    }

    public String getEmail() {
        return username;
    }

    public void setReservedPlayerUid(int uid) {
        this.reservedPlayerUid = uid;
    }

    // TODO make unique
    public String generateComboToken() {
        this.comboToken = Utils.bytesToHex(Crypto.createSessionKey(32));
        this.save();
        return this.comboToken;
    }

    // TODO make unique
    public String generateDispatchToken() {
        this.dispatchToken = Utils.bytesToHex(Crypto.createSessionKey(32));
        this.save();
        return this.dispatchToken;
    }

    public void save() {
        LunarRail.getAccountDatabase().save(this);
    }
}
