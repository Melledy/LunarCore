package emu.lunarcore.game.account;

import emu.lunarcore.LunarCore;

/**
 * Helper class for handling account related stuff
 */
public class AccountHelper {

    public static boolean createAccount(String username, String password, int reservedUid) {
        Account account = LunarCore.getAccountDatabase().getObjectByField(Account.class, "username", username);
        
        if (account != null) {
            return false;
        }
        
        account = new Account(username);
        account.setReservedPlayerUid(reservedUid);
        account.save();
        
        return true;
    }
    
    public static boolean deleteAccount(String username) {
        Account account = LunarCore.getAccountDatabase().getObjectByField(Account.class, "username", username);

        if (account == null) {
            return false;
        }
        
        return LunarCore.getAccountDatabase().delete(account);
    }
    
}
