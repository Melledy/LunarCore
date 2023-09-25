package emu.lunarcore.server.http.objects;

public class FingerprintResJson {
    public String message;
    public int retcode;
    public FingerprintDataJson data;

    public static class FingerprintDataJson {
        public String device_fp;
        public String message;
        public int code;

        public FingerprintDataJson(String fp) {
            this.code = 200;
            this.message = "OK";
            this.device_fp = fp;
        }
    }
}
