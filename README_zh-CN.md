# 月球核心
**_语言_**

[EN](README.md) | [简中](README_zh-CN.md)
#
出于研究目的的某个回合制动漫游戏的1.5.0版本服务端的重新实现。如果需要任何额外的支持、问题或者讨论，请查看我们的[discord](https://discord.gg/cfPKJ6N5hw).

### 现已实现功能
- 基本游戏功能：登录、队伍配置、背包、基本场景/实体管理
- 战斗功能
- 怪物/npc/道具生成
- 机翻：大多数字符技术都得到了处理（看不懂，等大佬pr）
- npc交易（商店）
- 抽卡（祈愿）
- 邮件系统
- 好友系统（助攻功能尚未实现）
- 机翻：被遗忘的大厅[具有 1.4.0 功能]（看不懂是啥，等大佬pr）
- 模拟宇宙（可以运行，但缺少许多功能）

# 运行服务端和客户端

### 运行环境（必装）
* [Java 17 JDK](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)

### 推荐安装（数据库，非必要）
* [MongoDB 4.0+](https://www.mongodb.com/try/download/community)

###编译服务端jar核心（纯机翻）
1. 从 https://gitlab.com/Melledy/LunarCore-Protos 下载源码并将 proto 文件夹放入服务器目录中
2. 在源码目录打开系统终端（cmd），使用./gradlew jar
3. 在服务器目录中创建一个名为的文件夹resources
4. 从 https://github.com/Dimbreath/StarRailData 下载 、（这里应该是res） 和 文件夹（不知道），并将它们放入 resources 文件夹中。ConfigTextMapExcelBin
5. 从 https://gitlab.com/Melledy/LunarCore-Configs 下载文件夹并将它们放入您的资源文件夹中。替换系统询问的任何文件。这些是用于世界生成的，对服务器来说非常重要。Config
6. 从系统终端运行服务器。Lunar Core 为其数据库配备了内置的内部 MongoDB 服务器，因此无需安装 Mongodb。但是，强烈建议无论如何都要安装Mongodb。java -jar LunarCore.jar
7. 如果在config中设置autoCreateAccount为 true，则可以跳过创建帐户。否则，请使用服务器控制台中的命令创建一个，创建命令为/account

### 与客户端建立连接 （Fiddler）（纯机翻）
1. 使用客户端登录官方服务器和Hoyoverse账号至少一次，下载游戏数据。
2. 安装并运行 Fiddler Classic。
3. 设置 fiddler 以解密 https 流量。（工具 -> 选项 -> HTTPS ->解密 HTTPS 流量）确保也已检查。ignore server certificate errors
4. 将以下代码复制并粘贴到 Fiddler 经典版的“Fiddlerscript”选项卡中：

```
import System;
import System.Windows.Forms;
import Fiddler;
import System.Text.RegularExpressions;

class Handlers
{
    static function OnBeforeRequest(oS: Session) {
        if (oS.host.EndsWith(".starrails.com") || oS.host.EndsWith(".hoyoverse.com") || oS.host.EndsWith(".mihoyo.com") || oS.host.EndsWith(".bhsr.com")) {
            oS.host = "localhost"; // This can also be replaced with another IP address.
        }
    }
};
```

5. 使用您的帐户名登录，密码可以设置为任何内容。

### 服务端指令
服务器命令可以在服务器控制台或游戏中运行。每个玩家的好友列表中都有一个名为“Server”的虚拟用户，您可以向其发送消息以使用游戏内命令。

```
/account {create | delete} [用户名称] (玩家uid). Creates（创建） 或者 deletes（删除）
/avatar lv(level) p(ascension) r(eidolon) s(skill levels). 设置当前角色属性（比如等级）
/clear {relics | lightcones | materials | items}. 删除玩家的某些东西，比如item（物品）
/gender {male | female}. 设置玩家的的性别（机翻）
/give [item id] x[amount]. 给与玩家特定物品
/giveall {materials | avatars}. 给与玩家某一类的所有物品（比如所有角色avatars）
/help. 查看可用命令列表
/mail [content]. 给目标玩家发送一封邮件
/permission {add | remove | clear} [permission]. 给目标玩家添加/移除某个特定权限
/reload. 重新加载服务端配置
/scene [场景id] [floor id]. 把玩家传送到指定场景
/spawn [monster/prop id] x[amount] s[stage id]. 在玩家附近生成怪物/npc/掉落物形态的道具
/unstuck @[player id]. 卡住脱离（如果玩家在一个无法加载的场景中，可以将其脱困）
/worldlevel [world level]. 设置玩家世界等级
```
