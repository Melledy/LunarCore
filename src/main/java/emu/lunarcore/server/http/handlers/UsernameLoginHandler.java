package emu.lunarcore.server.http.handlers;

import org.jetbrains.annotations.NotNull;

import emu.lunarcore.LunarCore;
import emu.lunarcore.game.account.Account;
import emu.lunarcore.game.account.AccountHelper;
import emu.lunarcore.server.http.objects.LoginAccountReqJson;
import emu.lunarcore.server.http.objects.LoginResJson;
import emu.lunarcore.server.http.objects.LoginResJson.VerifyData;
import emu.lunarcore.util.JsonUtils;
import io.javalin.http.ContentType;
import io.javalin.http.Context;
import io.javalin.http.Handler;

public class UsernameLoginHandler implements Handler {

    public UsernameLoginHandler() {

    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        // Setup response
        LoginResJson res = new LoginResJson();

        // Parse request
        LoginAccountReqJson req = JsonUtils.decode(ctx.body(), LoginAccountReqJson.class);

        // Validate
        if (req == null) {
            res.retcode = -202;
            res.message = "Error logging in";
            return;
        }

        // Login - Get account data
        Account account = LunarCore.getAccountDatabase().getObjectByField(Account.class, "username", req.account);

        if (account == null) {
            // Auto create an account for the player if allowed in the config
            if (LunarCore.getConfig().getServerOptions().autoCreateAccount) {
                account = Account.AccountHelper.createAccount(req.account, null, 0);
            } else {
                res.retcode = -201;
                res.message = "Username not found.";
            }
        }

        if (account != null) {
            res.message = "OK";
            res.data = new VerifyData(account.getUid(), account.getEmail(), account.generateDispatchToken());
        }

        // Send result
        ctx.contentType(ContentType.APPLICATION_JSON);
        ctx.result(JsonUtils.encode(res));
    }

}
