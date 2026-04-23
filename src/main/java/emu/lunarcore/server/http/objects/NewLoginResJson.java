package emu.lunarcore.server.http.objects;

public class NewLoginResJson {
    public String message;
    public int retcode;
    public VerifyData data;

    public static class VerifyData {
        public TokenData token = new TokenData();
        public VerifyAccountUserData user_info = new VerifyAccountUserData();
        public VerifyAccountExtUserData ext_user_info = new VerifyAccountExtUserData();
        public String reactivate_action_ticket = "";
        public String bind_email_action_ticket = "";

        public VerifyData(String accountUid, String email, String token) {
            this.token.token = token;
            this.user_info.aid = accountUid;
            this.user_info.email = email;
        }
    }

    public static class TokenData {
        public String token = "";
        public int token_type = 1;
    }

    public static class VerifyAccountUserData {
        public String account_name = "";
        public String aid = "";
        public String area_code = "**";
        public String country = "";
        public String email = "";
        public int is_email_verify = 0;
        public LinkData[] links = new LinkData[0];
        public String mid = "";
        public String mobile = "";
        public String realname = "";
        public String identity_code = "";
        public String safe_area_code = "";
        public String safe_mobile  = "";
        public String rebind_area_code = "";
        public String rebind_mobile = "";
        public String rebind_mobile_time = "";
        public String password_time = "0";
        public int is_adult = 0;
        public String unmasked_email = "";
        public int unmasked_email_type = 0;
    }
    
    public static class VerifyAccountExtUserData {
        public String guardian_email = "";
        public String birth = "";
    }

    public static class LinkData {
        public String nickname = "";
        public String thirdparty = "";
        public String union_id = "";
    }
}
