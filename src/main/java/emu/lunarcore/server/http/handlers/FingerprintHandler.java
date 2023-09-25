package emu.lunarcore.server.http.handlers;

import org.jetbrains.annotations.NotNull;

import emu.lunarcore.server.http.objects.FingerprintReqJson;
import emu.lunarcore.server.http.objects.FingerprintResJson;
import emu.lunarcore.server.http.objects.FingerprintResJson.FingerprintDataJson;
import emu.lunarcore.util.JsonUtils;
import io.javalin.http.ContentType;
import io.javalin.http.Context;
import io.javalin.http.Handler;

public class FingerprintHandler implements Handler {

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        FingerprintResJson res = new FingerprintResJson();

        FingerprintReqJson req = JsonUtils.decode(ctx.body(), FingerprintReqJson.class);

        if (req == null) {
            res.retcode = -202;
            res.message = "Error";
        }

        res.message = "OK";
        res.data = new FingerprintDataJson(req.device_fp);

        // Result
        ctx.contentType(ContentType.APPLICATION_JSON);
        ctx.result(JsonUtils.encode(res));
    }

}
