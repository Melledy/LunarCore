package emu.lunarcore.game.account;

import java.util.*;
import java.util.stream.Stream;

import dev.morphia.annotations.*;

import emu.lunarcore.LunarCore;
import emu.lunarcore.database.AccountDatabaseOnly;
import emu.lunarcore.util.Crypto;
import emu.lunarcore.util.Snowflake32;

import lombok.Getter;

@Getter
@AccountDatabaseOnly
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

    private Set<String> permissions;

    @Deprecated
    public Account() {

    }

    public Account(String username) {
        this.uid = Long.toString(Snowflake32.newUid());
        this.username = username;
        this.permissions = new HashSet<>();
    }

    public String getEmail() {
        return username;
    }

    public void setReservedPlayerUid(int uid) {
        this.reservedPlayerUid = uid;
    }

    // Permissions

    public Set<String> getPermissions() {
        if (this.permissions == null) {
            this.permissions = new HashSet<>();
            this.save();
        }
        return this.permissions;
    }

    public boolean addPermission(String permission) {
        if (this.getPermissions().contains(permission)) {
            return false;
        }
        this.getPermissions().add(permission);
        this.save();
        return true;
    }

    public static boolean permissionMatchesWildcard(String wildcard, String[] permissionParts) {
        String[] wildcardParts = wildcard.split("\\.");
        if (permissionParts.length < wildcardParts.length) {  // A longer wildcard can never match a shorter permission
            return false;
        }

        for (int i = 0; i < wildcardParts.length; i++) {
            switch (wildcardParts[i]) {
                case "**":  // Recursing match
                    return true;
                case "*":  // Match only one layer
                    if (i >= (permissionParts.length-1)) {
                        return true;
                    }
                    break;
                default:  // This layer isn't a wildcard, it needs to match exactly
                    if (!wildcardParts[i].equals(permissionParts[i])) {
                        return false;
                    }
            }
        }
        // At this point the wildcard will have matched every layer, but if it is shorter then the permission then this is not a match at this point (no **).
        return wildcardParts.length == permissionParts.length;
    }

    public boolean hasPermission(String permission) {
        // Skip if permission isnt required
        if (permission.isEmpty()) {
            return true;
        }

        // Default permissions
        var defaultPermissions = LunarCore.getConfig().getServerOptions().getDefaultPermissions();

        if (defaultPermissions.contains("*")) {
            return true;
        }

        // Add default permissions if it doesn't exist
        List<String> permissions = Stream.of(this.getPermissions(), defaultPermissions)
                .flatMap(Collection::stream)
                .distinct().toList();

        if (permissions.contains(permission)) {
            return true;
        }

        String[] permissionParts = permission.split("\\.");
        for (String p : permissions) {
            if (p.startsWith("-") && permissionMatchesWildcard(p.substring(1), permissionParts)) return false;
            if (permissionMatchesWildcard(p, permissionParts)) return true;
        }

        return permissions.contains("*");
    }

    public boolean removePermission(String permission) {
        boolean res = this.getPermissions().remove(permission);
        if (res) this.save();
        return res;
    }

    public void clearPermission() {
        this.getPermissions().clear();
        this.save();
    }

    // Tokens

    public String generateComboToken() {
        this.comboToken = Crypto.createSessionKey(this.getUid());
        this.save();
        return this.comboToken;
    }

    public String generateDispatchToken() {
        this.dispatchToken = Crypto.createSessionKey(this.getUid());
        this.save();
        return this.dispatchToken;
    }

    // Database

    public void save() {
        LunarCore.getAccountDatabase().save(this);
    }

    /**
     * Helper class for handling account related stuff
     */
    public static class AccountHelper {

        public static Account createAccount(String username, String password, int reservedUid) {
            Account account = LunarCore.getAccountDatabase().getObjectByField(Account.class, "username", username);

            if (account != null) {
                return null;
            }

            account = new Account(username);
            account.setReservedPlayerUid(reservedUid);
            account.save();

            return account;
        }

        public static boolean deleteAccount(String username) {
            Account account = LunarCore.getAccountDatabase().getObjectByField(Account.class, "username", username);

            if (account == null) {
                return false;
            }

            // Delete the player too
            // IMPORTANT: This will only delete the player from the current game server
            if (LunarCore.getGameServer() != null) {
                LunarCore.getGameServer().deletePlayer(account.getUid());
            }

            // Delete the account first
            return LunarCore.getAccountDatabase().delete(account);
        }

    }
}
