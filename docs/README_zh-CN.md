![LunarCore](https://socialify.git.ci/Melledy/LunarCore/image?description=1&descriptionEditable=A%20game%20server%20reimplementation%20for%20version%201.5.0%20of%20a%20certain%20turn-based%20anime%20game%20for%20educational%20purposes.%20&font=Inter&forks=1&issues=1&language=1&name=1&owner=1&pulls=1&stargazers=1&theme=Light)
<div align="center"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/Melledy/LunarCore?logo=java&style=for-the-badge"> <img alt="GitHub" src="https://img.shields.io/github/license/Melledy/LunarCore?style=for-the-badge"> <img alt="GitHub last commit" src="https://img.shields.io/github/last-commit/Melledy/LunarCore?style=for-the-badge"> <img alt="GitHub Workflow Status" src="https://img.shields.io/github/actions/workflow/status/Melledy/LunarCore/build.yml?branch=development&logo=github&style=for-the-badge"></div>

<div align="center"><a href="https://discord.gg/cfPKJ6N5hw"><img alt="Discord - Grasscutter" src="https://img.shields.io/discord/1163718404067303444?label=Discord&logo=discord&style=for-the-badge"></a></div>

[EN](../README.md) | [简中](README_zh-CN.md) | [繁中](README_zh-TW.md) | [JP](README_ja-JP.md) | [RU](README_ru-RU.md) | [FR](README_fr-FR.md) | [KR](README_ko-KR.md)

**注意:** 如果需要任何额外的支持、问题或者讨论，请查看我们的 [Discord](https://discord.gg/cfPKJ6N5hw).

### 显著特点
- 基本游戏功能：登录、队伍配置、背包、基本场景/实体管理
- 战斗功能
- 自然世界怪物/道具/NPC生成
- 大多数角色技能已处理
- NPC商店已处理
- 祈愿系统
- 邮件系统
- 好友系统（好友支援尚未实现）
- 忘却之庭（带有1.4.0功能）
- 模拟宇宙（可以运行，但缺少许多功能）

# 运行服务端和客户端

### 必需条件
* [Java 17 JDK](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)

### 推荐安装
* [MongoDB 4.0+](https://www.mongodb.com/try/download/community)

### 编译服务端核心
1. 打开系统终端，使用 `./gradlew jar` 编译服务端核心
2. 在服务器目录中创建一个名为 `resources` 的文件夹
3. 从 [https://github.com/Dimbreath/StarRailData](https://github.com/Dimbreath/StarRailData) 下载 `Config`、`TextMap` 和 `ExcelBin` 文件夹，并将它们放入资源文件夹
4. 从 [https://gitlab.com/Melledy/LunarCore-Configs](https://gitlab.com/Melledy/LunarCore-Configs) 下载 `Config` 文件夹，并将其放入资源文件夹。替换系统询问的任何文件。这些文件用于世界生成，对服务器非常重要。
5. 从系统终端使用 `java -jar LunarCore.jar` 运行服务端。Lunar Core带有一个内置的MongoDB数据库服务，因此不需要安装MongoDB。但是，强烈建议安装MongoDB。
6. 如果在配置中将 `autoCreateAccount` 设置为true，则可以跳过创建帐户的步骤。否则，需要在服务器控制台使用 `/account` 命令创建一个帐户。

### 与客户端（Fiddler）连接
1. **使用客户端至少一次登录到官方服务器和Hoyoverse账户以下载游戏数据。**
2. 安装并运行 [Fiddler Classic](https://www.telerik.com/fiddler)。
3. 将Fiddler设置为解密https流量（工具 -> 选项 -> HTTPS -> 解密HTTPS流量），确保选中 `忽略服务器证书错误`。
4. 将以下代码复制并粘贴到Fiddler Classic的Fiddlerscript选项卡中：

```javascript
import System;
import System.Windows.Forms;
import Fiddler;
import System.Text.RegularExpressions;

class Handlers
{
    static function OnBeforeRequest(oS: Session) {
        if (oS.host.EndsWith(".starrails.com") || oS.host.EndsWith(".hoyoverse.com") || oS.host.EndsWith(".mihoyo.com") || oS.host.EndsWith(".bhsr.com")) {
            oS.host = "localhost"; // 这也可以替换为其他IP地址。
        }
    }
};
```

5. 使用您的帐户名称登录，密码可以设置为任何值。

### 服务器命令
服务器命令可以在服务器控制台或游戏中运行。每个玩家的好友列表中都有一个名为 "Server" 的虚拟用户，您可以向其发送消息以使用游戏中的命令。

```
/account {create | delete} [username] (保留玩家uid). 创建或删除一个帐户。
/avatar lv(level) p(ascension) r(eidolon) s(skill levels). 设置当前角色的属性。
/clear {relics | lightcones | materials | items}. 从玩家库存中删除过滤的物品。
/gender {male | female}. 设置玩家性别。
/give [item id] x[amount] lv[number]. 给予目标玩家一个物品。
/giveall {materials | avatars}. 给予目标玩家物品。
/heal. 治疗你的角色。
/help 显示可用命令列表。
/kick @[player id]. 将一名玩家踢出服务器。
/mail [content]. 发送系统邮件给目标玩家。
/permission {add | remove | clear} [permission]. 向目标玩家授予/移除权限。
/refill. 在开放世界中补充战技点。
/reload. 重载服务器配置。
/scene [scene id] [floor id]. 将玩家传送到指定的场景。
/spawn [monster/prop id] x[amount] s[stage id]. 在目标玩家附近生成怪物或道具。
/unstuck @[player id]. 如果离线玩家卡在不加载的场景中，解除卡住。
/worldlevel [world level]. 设置目标玩家的均衡等级。
```
