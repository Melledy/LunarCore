package emu.lunarcore;

public class HotfixData {
    public String assetBundleUrl = "";
    public String baseAssetBundleUrl = "";
    public String exResourceUrl = "";
    public String luaUrl = "";
    public String ifixUrl = "";
    
    private int customMdkResVersion = 0;
    private int customIfixVersion = 0;
    
    public String getMdkResVersion() {
        if (this.customMdkResVersion != 0) {
            return Integer.toString(this.customMdkResVersion);
        } else if (this.luaUrl != null && !this.luaUrl.isBlank()) {
            return this.luaUrl.split("/")[this.luaUrl.split("/").length - 1].split("_")[1];
        }
        
        return null;
    }
    
    public String getIfixVersion() {
        if (this.customIfixVersion != 0) {
            return Integer.toString(this.customIfixVersion);
        } else if (this.ifixUrl != null && !this.ifixUrl.isBlank()) {
            return this.ifixUrl.split("/")[this.ifixUrl.split("/").length - 1].split("_")[1];
        }
        
        return null;
    }
}
