package emu.lunarcore.server.http.handlers;

import org.jetbrains.annotations.NotNull;

import emu.lunarcore.LunarRail;
import emu.lunarcore.game.account.Account;
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

        // Login
        Account account = LunarRail.getAccountDatabase().getObjectByField(Account.class, "username", req.account);

        if (account == null) {
            res.retcode = -201;
            res.message = "Username not found.";
        } else {
            res.message = "OK";
            res.data = new VerifyData(account.getUid(), account.getEmail(), account.generateDispatchToken());
        }

        // Send result
        ctx.contentType(ContentType.APPLICATION_JSON);
        ctx.result(JsonUtils.encode(res));
    }

}
