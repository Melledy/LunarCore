# Lunar Core

**_Language_**
[EN](README.md) | [简体中文](README_zh-CN.md) | [繁體中文](README_zh-TW.md) | [日本語](README_ja-JP.md)

#
出於教育目的的某一個回合制不知名動漫遊戲1.5.0版本伺服器端的逆向工程。如果需要任何額外的支持、問題或者討論，請查看我們的[Discord](https://discord.gg/cfPKJ6N5hw)。

### 當前功能
- 基本遊戲功能：登錄、隊伍配置、背包、基本場景/實體管理
- 戰鬥功能
- 自然世界怪物/道具/NPC生成
- 大多數角色技能
- NPC商店
- 躍遷/抽卡系統
- 郵件系統
- 好友系統（支援角色尚未實現）
- 忘卻之庭（帶有1.4.0功能）
- 模擬宇宙（可以運行，但缺少許多功能）

# 運行伺服器端和用戶端

### 必需條件
* [Java 17 JDK](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)

### 推薦安裝
* [MongoDB 4.0+](https://www.mongodb.com/try/download/community)

### 編譯伺服器端核心
1. 從 [https://gitlab.com/Melledy/LunarCore-Protos](https://gitlab.com/Melledy/LunarCore-Protos) 下載文件並將proto文件夾放入伺服器目錄
2. 打開系統終端，使用 `./gradlew jar` 編譯伺服器端核心
3. 在伺服器目錄中創建一個名為 `resources` 的文件夾
4. 從 [https://github.com/Dimbreath/StarRailData](https://github.com/Dimbreath/StarRailData) 下載 `Config`、`TextMap` 和 `ExcelBin` 文件夾，並將它們放入資源文件夾
5. 從 [https://gitlab.com/Melledy/LunarCore-Configs](https://gitlab.com/Melledy/LunarCore-Configs) 下載 `Config` 文件夾，並將其放入資源文件夾。替換系統詢問的任何文件。這些文件用於世界生成，對伺服器非常重要。
6. 從系統終端使用 `java -jar LunarCore.jar` 運行伺服器端。Lunar Core帶有一個內建的MongoDB資料庫服務，因此不需要安裝MongoDB。但是還是強烈建議安裝MongoDB。
7. 如果在配置中將 `autoCreateAccount` 設置為true，則可以跳過創建帳戶的步驟。否則，需要在伺服器控制台使用 `/account` 命令創建一個帳戶。

### 與用戶端連接（Fiddler）
1. **使用用戶端至少一次登錄到官方伺服器和Hoyoverse帳戶以下載遊戲數據。**
2. 安裝並運行 [Fiddler Classic](https://www.telerik.com/fiddler)。
3. 將Fiddler設置為解密https流量（工具 -> 選項 -> HTTPS -> 解密HTTPS流量），確保選中 `忽略伺服器證書錯誤 (Ignore server certificate errors)`。
4. 將以下代碼複製並黏貼到Fiddler Classic的Fiddlerscript選項卡中：

```javascript
import System;
import System.Windows.Forms;
import Fiddler;
import System.Text.RegularExpressions;

class Handlers
{
static function OnBeforeRequest(oS: Session) {
if (oS.host.EndsWith(".starrails.com") || oS.host.EndsWith(".hoyoverse.com") || oS.host.EndsWith(".mihoyo.com") || oS.host.EndsWith(".bhsr.com")) {
oS.host = "localhost"; // 這也可以替換為其他IP位址。
}
}
};
```

5. 使用您的帳戶名稱登入，密碼可以隨機輸入。

### 伺服器命令
伺服器命令可以在伺服器控制台或遊戲中運行。每個玩家的好友列表中都有一個名為 "Server" 的虛擬用戶，您可以向其發送消息以使用遊戲中的命令。

```
/account {create | delete} [username] (玩家UID). 創建或刪除一個帳戶。
/avatar lv(level) p(ascension) r(eidolon) s(skill levels) 設置當前角色的屬性。
/clear {relics | lightcones | materials | items} 從玩家庫存中刪除過濾的物品。
/gender {male | female} 設置目標玩家性別。
/give [item id] x[amount] 給予目標玩家指定物品。
/giveall {materials | avatars} 給予目標玩家所有物品／角色。
/help 顯示可用命令列表。
/mail [content] 發送系統郵件給目標玩家。
/permission {add | remove | clear} [permission] 向目標玩家授予/移除權限。
/reload 重載伺服器配置。
/scene [scene id] [floor id] 將玩家傳送到指定的場景。
/spawn [monster/prop id] x[amount] s[stage id] 在目標玩家附近生成怪物或實體。
/unstuck @[player id]. 如果離線目標玩家卡在無法載入的場景中，將會把目標玩家傳送到初始場景。
/worldlevel [world level]. 設置目標玩家的均衡等級。
``` 
