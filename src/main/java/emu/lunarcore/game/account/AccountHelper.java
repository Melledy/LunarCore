package emu.lunarcore.game.account;

import emu.lunarcore.LunarRail;

/**
 * Helper class for handling account related stuff
 */
public class AccountHelper {

    public static boolean createAccount(String username, String password, int reservedUid) {
        Account account = LunarRail.getAccountDatabase().getObjectByField(Account.class, "username", username);
        
        if (account != null) {
            return false;
        }
        
        account = new Account(username);
        account.setReservedPlayerUid(reservedUid);
        account.save();
        
        return true;
    }
    
    public static boolean deleteAccount(String username) {
        Account account = LunarRail.getAccountDatabase().getObjectByField(Account.class, "username", username);

        if (account == null) {
            return false;
        }
        
        return LunarRail.getAccountDatabase().delete(account);
    }
    
}
