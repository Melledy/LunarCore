package emu.lunarcore.server.http.handlers;

import org.jetbrains.annotations.NotNull;

import emu.lunarcore.LunarCore;
import emu.lunarcore.game.account.Account;
import emu.lunarcore.game.account.AccountHelper;
import emu.lunarcore.server.http.objects.NewLoginReqJson;
import emu.lunarcore.server.http.objects.NewLoginResJson;
import emu.lunarcore.server.http.objects.NewLoginResJson.VerifyData;
import emu.lunarcore.util.JsonUtils;
import io.javalin.http.ContentType;
import io.javalin.http.Context;
import io.javalin.http.Handler;

public class AppLoginHandler implements Handler {

    public AppLoginHandler() {

    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        // Setup response
        NewLoginResJson res = new NewLoginResJson();

        // CRITICAL TODO: DECRYPT THE REQUEST

        // Parse request
        NewLoginReqJson req = JsonUtils.decode(ctx.body(), NewLoginReqJson.class);

        // Validate
        if (req == null) {
            res.retcode = -202;
            res.message = "Error logging in";
            return;
        }
        
        // Force account name
        String accountName = LunarCore.getConfig().getLoginOptions().accountName;
        
        if (accountName.isBlank()) {
            accountName = "player";
        }

        // Login - Get account data
        Account account = LunarCore.getAccountDatabase().getObjectByField(Account.class, "username", accountName);

        if (account == null) {
            // Auto create an account for the player if allowed in the config
            if (LunarCore.getConfig().getServerOptions().autoCreateAccount) {
                account = AccountHelper.createAccount(accountName, null, 0);
            } else {
                res.retcode = -201;
                res.message = "Username not found.";
            }
        } 
        
        if (account != null) {
            res.message = "OK";
            res.data = new VerifyData(account.getUid(), account.getEmail(), account.generateDispatchTokenV2());
        }

        // Send result
        ctx.contentType(ContentType.APPLICATION_JSON);
        ctx.result(JsonUtils.encode(res));
    }

}
