package emu.lunarcore.server.http;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import emu.lunarcore.Config.HttpServerConfig;
import emu.lunarcore.LunarCore;
import emu.lunarcore.LunarCore.ServerType;
import emu.lunarcore.proto.DispatchRegionDataOuterClass.DispatchRegionData;
import emu.lunarcore.server.game.RegionInfo;
import emu.lunarcore.server.http.handlers.*;
import emu.lunarcore.util.Utils;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.javalin.http.Context;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class HttpServer {
    private final Javalin app;
    private final ServerType type;
    
    private List<String> modes;
    private boolean started;
    
    private long nextRegionUpdate;
    private Object2ObjectMap<String, RegionInfo> regions;
    private String regionList;

    public HttpServer(ServerType type) {
        this.type = type;
        this.app = Javalin.create();
        this.modes = new LinkedList<>();
        this.regions = new Object2ObjectOpenHashMap<>();

        this.addRoutes();
    }

    public Javalin getApp() {
        return this.app;
    }

    public ServerType getType() {
        return type;
    }

    public HttpServerConfig getServerConfig() {
        return LunarCore.getConfig().getHttpServer();
    }

    private HttpConnectionFactory getHttpFactory() {
        HttpConfiguration httpsConfig = new HttpConfiguration();
        SecureRequestCustomizer src = new SecureRequestCustomizer();
        src.setSniHostCheck(false);
        httpsConfig.addCustomizer(src);
        return new HttpConnectionFactory(httpsConfig);
    }

    private SslContextFactory.Server getSSLContextFactory() {
        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath(LunarCore.getConfig().getKeystore().getPath());
        sslContextFactory.setKeyStorePassword(LunarCore.getConfig().getKeystore().getPassword());
        sslContextFactory.setSniRequired(false);
        sslContextFactory.setRenegotiationAllowed(false);
        return sslContextFactory;
    }
    
    public void forceRegionListRefresh() {
        this.nextRegionUpdate = 0;
    }
    
    public String getRegionList() {
        synchronized (this.regions) {
            // Check if region list needs to be cached
            if (System.currentTimeMillis() > this.nextRegionUpdate || this.regionList == null) {
                // Clear regions first
                this.regions.clear();
                
                // Pull region infos from database
                LunarCore.getAccountDatabase().getObjects(RegionInfo.class)
                    .forEach(region -> {
                        this.regions.put(region.getId(), region);
                    });

                // Serialize to proto
                DispatchRegionData regionData = DispatchRegionData.newInstance();
                regions.values().stream().map(RegionInfo::toProto).forEach(regionData::addRegionList);
                
                // Set region list cache
                this.regionList = Utils.base64Encode(regionData.toByteArray());
                this.nextRegionUpdate = System.currentTimeMillis() + getServerConfig().regionListRefresh;
            }
        }
        
        return regionList;
    }

    public void start() {
        if (this.started) return;
        this.started = true;

        // Http server
        if (getServerConfig().isUseSSL()) {
            ServerConnector sslConnector = new ServerConnector(getApp().jettyServer().server(), getSSLContextFactory(), getHttpFactory());
            sslConnector.setHost(getServerConfig().getBindAddress());
            sslConnector.setPort(getServerConfig().getBindPort());
            getApp().jettyServer().server().addConnector(sslConnector);

            getApp().start();
        } else {
            getApp().start(getServerConfig().getBindAddress(), getServerConfig().getBindPort());
        }

        // Done
        LunarCore.getLogger().info("Http Server running as: " + this.modes.stream().collect(Collectors.joining(", ")));
        LunarCore.getLogger().info("Http Server started on " + getServerConfig().getBindPort());
    }

    private void addRoutes() {
        // Add routes based on what type of server this is
        if (this.getType().runDispatch()) {
            this.addDispatchRoutes();
            this.addLogServerRoutes();
        }
        if (this.getType().runGame()) {
            this.addGateServerRoutes();
        }

        // Fallback handler
        getApp().error(404, this::notFoundHandler);
    }

    private void addDispatchRoutes() {
        // Get region info
        getApp().get("/query_dispatch", new QueryDispatchHandler(this));

        // Captcha -> api-account-os.hoyoverse.com
        getApp().post("/account/risky/api/check", new HttpJsonResponse("{\"retcode\":0,\"message\":\"OK\",\"data\":{\"id\":\"none\",\"action\":\"ACTION_NONE\",\"geetest\":null}}"));

        // === AUTHENTICATION === hkrpg-sdk-os-static.hoyoverse.com

        // Username & Password login (from client). Returns a session key to the client.
        getApp().post("/hkrpg_global/mdk/shield/api/login", new UsernameLoginHandler());
        // Cached session key verify (from registry). Returns a session key to the client.
        getApp().post("/hkrpg_global/mdk/shield/api/verify", new TokenLoginHandler());

        // Exchange session key for login token (combo token)
        getApp().post("/hkrpg_global/combo/granter/login/v2/login", new ComboTokenGranterHandler());

        // Config
        getApp().get("/hkrpg_global/combo/granter/api/getConfig", new HttpJsonResponse("{\"retcode\":0,\"message\":\"OK\",\"data\":{\"protocol\":true,\"qr_enabled\":false,\"log_level\":\"INFO\",\"announce_url\":\"\",\"push_alias_type\":0,\"disable_ysdk_guard\":true,\"enable_announce_pic_popup\":false,\"app_name\":\"崩�??RPG\",\"qr_enabled_apps\":{\"bbs\":false,\"cloud\":false},\"qr_app_icons\":{\"app\":\"\",\"bbs\":\"\",\"cloud\":\"\"},\"qr_cloud_display_name\":\"\",\"enable_user_center\":true,\"functional_switch_configs\":{}}}"));
        getApp().get("/hkrpg_global/mdk/shield/api/loadConfig", new HttpJsonResponse("{\"retcode\":0,\"message\":\"OK\",\"data\":{\"id\":24,\"game_key\":\"hkrpg_global\",\"client\":\"PC\",\"identity\":\"I_IDENTITY\",\"guest\":false,\"ignore_versions\":\"\",\"scene\":\"S_NORMAL\",\"name\":\"崩�??RPG\",\"disable_regist\":false,\"enable_email_captcha\":false,\"thirdparty\":[\"fb\",\"tw\",\"gl\",\"ap\"],\"disable_mmt\":false,\"server_guest\":false,\"thirdparty_ignore\":{},\"enable_ps_bind_account\":false,\"thirdparty_login_configs\":{\"tw\":{\"token_type\":\"TK_GAME_TOKEN\",\"game_token_expires_in\":2592000},\"ap\":{\"token_type\":\"TK_GAME_TOKEN\",\"game_token_expires_in\":604800},\"fb\":{\"token_type\":\"TK_GAME_TOKEN\",\"game_token_expires_in\":2592000},\"gl\":{\"token_type\":\"TK_GAME_TOKEN\",\"game_token_expires_in\":604800}},\"initialize_firebase\":false,\"bbs_auth_login\":false,\"bbs_auth_login_ignore\":[],\"fetch_instance_id\":false,\"enable_flash_login\":false}}"));

        // === EXTRA ===

        // hkrpg-sdk-os.hoyoverse.com
        getApp().post("/hkrpg_global/combo/granter/api/compareProtocolVersion", new HttpJsonResponse("{\"retcode\":0,\"message\":\"OK\",\"data\":{\"modified\":false,\"protocol\":null}}"));
        getApp().get("/hkrpg_global/mdk/agreement/api/getAgreementInfos", new HttpJsonResponse("{\"retcode\":0,\"message\":\"OK\",\"data\":{\"marketing_agreements\":[]}}"));

        // sdk-os-static.hoyoverse.com
        getApp().get("/combo/box/api/config/sdk/combo", new HttpJsonResponse("{\"retcode\":0,\"message\":\"OK\",\"data\":{\"vals\":{\"kibana_pc_config\":\"{ \\\"enable\\\": 0, \\\"level\\\": \\\"Info\\\",\\\"modules\\\": [\\\"download\\\"] }\\n\",\"network_report_config\":\"{ \\\"enable\\\": 0, \\\"status_codes\\\": [206], \\\"url_paths\\\": [\\\"dataUpload\\\", \\\"red_dot\\\"] }\\n\",\"list_price_tierv2_enable\":\"false\\n\",\"pay_payco_centered_host\":\"bill.payco.com\",\"telemetry_config\":\"{\\n \\\"dataupload_enable\\\": 0,\\n}\",\"enable_web_dpi\":\"true\"}}}"));
        getApp().get("/combo/box/api/config/sw/precache", new HttpJsonResponse("{\"retcode\":0,\"message\":\"OK\",\"data\":{\"vals\":{\"url\":\"\",\"enable\":\"false\"}}}"));

        // sg-public-data-api.hoyoverse.com
        getApp().get("/device-fp/api/getFp", new FingerprintHandler());
        getApp().get("/device-fp/api/getExtList", new HttpJsonResponse("{\"retcode\":0,\"message\":\"OK\",\"data\":{\"code\":200,\"msg\":\"ok\",\"ext_list\":[],\"pkg_list\":[],\"pkg_str\":\"/vK5WTh5SS3SAj8Zm0qPWg==\"}}"));

        // abtest-api-data-sg.hoyoverse.com
        getApp().post("/data_abtest_api/config/experiment/list", new HttpJsonResponse("{\"retcode\":0,\"success\":true,\"message\":\"\",\"data\":[{\"code\":1000,\"type\":2,\"config_id\":\"14\",\"period_id\":\"6125_197\",\"version\":\"1\",\"configs\":{\"cardType\":\"direct\"}}]}"));
    
        // Add mode
        this.modes.add("DISPATCH");
    }

    private void addLogServerRoutes() {
        // hkrpg-log-upload-os.hoyoverse.com
        getApp().post("/sdk/dataUpload", new HttpJsonResponse("{\"code\":0}"));

        // log-upload-os.hoyoverse.com
        getApp().post("/crashdump/dataUpload", new HttpJsonResponse("{\"code\":0}"));
        getApp().post("/apm/dataUpload", new HttpJsonResponse("{\"code\":0}"));

        // minor-api-os.hoyoverse.com
        getApp().post("/common/h5log/log/batch", new HttpJsonResponse("{\"retcode\":0,\"message\":\"success\",\"data\":null}"));
    }

    private void addGateServerRoutes() {
        // Gateway info
        getApp().get("/query_gateway", new QueryGatewayHandler());
        
        // Add mode
        this.modes.add("GATESERVER");
    }

    private void notFoundHandler(Context ctx) {
        ctx.status(404);
        ctx.contentType(ContentType.TEXT_PLAIN);
        ctx.result("not found");
    }
}
