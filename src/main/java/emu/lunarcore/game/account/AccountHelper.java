package emu.lunarcore.game.account;

import emu.lunarcore.LunarCore;

/**
 * Helper class for handling account related stuff
 */
public class AccountHelper {

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
