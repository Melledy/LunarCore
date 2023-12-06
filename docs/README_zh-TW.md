![LunarCore](https://socialify.git.ci/Melledy/LunarCore/image?description=1&descriptionEditable=A%20game%20server%20reimplementation%20for%20version%201.5.0%20of%20a%20certain%20turn-based%20anime%20game%20for%20educational%20purposes.%20&font=Inter&forks=1&issues=1&language=1&name=1&owner=1&pulls=1&stargazers=1&theme=Light)
<div align="center"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/Melledy/LunarCore?logo=java&style=for-the-badge"> <img alt="GitHub" src="https://img.shields.io/github/license/Melledy/LunarCore?style=for-the-badge"> <img alt="GitHub last commit" src="https://img.shields.io/github/last-commit/Melledy/LunarCore?style=for-the-badge"> <img alt="GitHub Workflow Status" src="https://img.shields.io/github/actions/workflow/status/Melledy/LunarCore/build.yml?branch=development&logo=github&style=for-the-badge"></div>

<div align="center"><a href="https://discord.gg/cfPKJ6N5hw"><img alt="Discord - Grasscutter" src="https://img.shields.io/discord/1163718404067303444?label=Discord&logo=discord&style=for-the-badge"></a></div>

[EN](../README.md) | [简中](README_zh-CN.md) | [繁中](README_zh-TW.md) | [JP](README_ja-JP.md) | [RU](README_ru-RU.md) | [FR](README_fr-FR.md) | [KR](README_ko-KR.md)

**請注意:** 如果需要任何額外的支持、問題或者討論，請查看我們的 [Discord](https://discord.gg/cfPKJ6N5hw).

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
1. 打開系統終端，使用 `./gradlew jar` 編譯伺服器端核心
2. 在伺服器目錄中創建一個名為 `resources` 的文件夾
3. 從 [https://github.com/Dimbreath/StarRailData](https://github.com/Dimbreath/StarRailData) 下載 `Config`、`TextMap` 和 `ExcelBin` 文件夾，並將它們放入資源文件夾
4. 從 [https://gitlab.com/Melledy/LunarCore-Configs](https://gitlab.com/Melledy/LunarCore-Configs) 下載 `Config` 文件夾，並將其放入資源文件夾。替換系統詢問的任何文件。這些文件用於世界生成，對伺服器非常重要。
5. 從系統終端使用 `java -jar LunarCore.jar` 運行伺服器端。Lunar Core帶有一個內建的MongoDB資料庫服務，因此不需要安裝MongoDB。但是還是強烈建議安裝MongoDB。
6. 如果在配置中將 `autoCreateAccount` 設置為true，則可以跳過創建帳戶的步驟。否則，需要在伺服器控制台使用 `/account` 命令創建一個帳戶。

### 與用戶端連接（Fiddler）
1. **使用用戶端至少一次登錄到官方伺服器和Hoyoverse帳戶以下載遊戲數據。**
2. 安裝並運行 [Fiddler Classic](https://www.telerik.com/fiddler)。
3. 將Fiddler設置為解密https流量（工具 -> 選項 -> HTTPS -> 解密HTTPS流量），確保選中 `忽略伺服器證書錯誤 (Ignore server certificate errors)`。
4. 將以下代碼複製並黏貼到Fiddler Classic的Fiddlerscript選項卡中：

```
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
/give [item id] x[amount] lv[number] 給予目標玩家指定物品。
/giveall {materials | avatars} 給予目標玩家所有物品／角色。
/heal. 治癒你的角色。
/help 顯示可用命令列表。
/kick @[player id]. 將一名玩家踢出伺服器。
/mail [content] 發送系統郵件給目標玩家。
/permission {add | remove | clear} [permission] 向目標玩家授予/移除權限。
/refill. 在開放世界中補充戰技點。
/reload 重載伺服器配置。
/scene [scene id] [floor id] 將玩家傳送到指定的場景。
/spawn [monster/prop id] x[amount] s[stage id] 在目標玩家附近生成怪物或實體。
/unstuck @[player id]. 如果離線目標玩家卡在無法載入的場景中，將會把目標玩家傳送到初始場景。
/worldlevel [world level]. 設置目標玩家的均衡等級。
``` 
