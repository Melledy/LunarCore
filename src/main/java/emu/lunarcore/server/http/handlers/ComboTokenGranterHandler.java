package emu.lunarcore.server.http.handlers;

import org.jetbrains.annotations.NotNull;

import emu.lunarcore.LunarCore;
import emu.lunarcore.game.account.Account;
import emu.lunarcore.server.http.objects.ComboTokenReqJson;
import emu.lunarcore.server.http.objects.ComboTokenReqJson.LoginTokenData;
import emu.lunarcore.server.http.objects.ComboTokenResJson;
import emu.lunarcore.server.http.objects.ComboTokenResJson.LoginData;
import emu.lunarcore.util.JsonUtils;
import io.javalin.http.ContentType;
import io.javalin.http.Context;
import io.javalin.http.Handler;

public class ComboTokenGranterHandler implements Handler {

    public ComboTokenGranterHandler() {
        
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        // Setup response
        ComboTokenResJson res = new ComboTokenResJson();

        // Parse request
        ComboTokenReqJson req = JsonUtils.decode(ctx.body(), ComboTokenReqJson.class);

        // Validate
        if (req == null || req.data == null) {
            res.retcode = -202;
            res.message = "Error logging in";
            return;
        }

        // Get login data
        LoginTokenData data = JsonUtils.decode(req.data, LoginTokenData.class);

        // Validate 2
        if (data == null) {
            res.retcode = -202;
            res.message = "Invalid login data";
            return;
        }

        // Login
        Account account = LunarCore.getAccountDatabase().getObjectByField(Account.class, "_id", data.uid);

        if (account == null || !account.getDispatchToken().equals(data.token)) {
            res.retcode = -201;
            res.message = "Game account cache information error";
        } else {
            res.message = "OK";
            res.data = new LoginData(account.getUid(), account.generateComboToken());
        }

        // Result
        ctx.contentType(ContentType.APPLICATION_JSON);
        ctx.result(JsonUtils.encode(res));
    }

}
