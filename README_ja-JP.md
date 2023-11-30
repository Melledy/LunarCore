# Lunar Core

**_Language_**
[EN](README.md) | [简体中文](README_zh-CN.md) | [繁體中文](README_zh-TW.md) | [日本語](README_ja-JP.md)

#
これは、教育目的でとあるターン制アニメゲームのバージョン1.5.0を再実装したゲームサーバーです。追加のサポート、質問、または議論がある場合は、[discord](https://discord.gg/cfPKJ6N5hw)をご覧ください。

### 注目すべき機能
- 基本ゲーム機能：ログイン、チームのセットアップ、バッグ、基本的なシーン/エンティティの管理
- モンスター戦闘
- オーバーワールドのモンスター/プロップ/NPCのスポーン
- ほぼ全ての秘技
- NPCショップ
- ガチャシステム
- メールシステム
- フレンドシステム（アシストはまだ機能していません）
- 忘却の庭（1.4.0の機能付き）
- 模擬宇宙（ランは終了できますが、多くの機能が不足しています）

# サーバーとクライアントの実行

### 必要条件
* [Java 17 JDK](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)

### お勧め
* [MongoDB 4.0+](https://www.mongodb.com/try/download/community)

### サーバーのコンパイル
1. [https://gitlab.com/Melledy/LunarCore-Protos](https://gitlab.com/Melledy/LunarCore-Protos) からファイルをダウンロードし、protoフォルダをサーバーディレクトリに配置します。
2. システムのターミナルを開き、`./gradlew jar` でサーバーをコンパイルします。
3. サーバーディレクトリに `resources` という名前のフォルダを作成します。
4. [https://github.com/Dimbreath/StarRailData](https://github.com/Dimbreath/StarRailData) から `Config`、`TextMap`、および `ExcelBin` フォルダをダウンロードし、それらをリソースフォルダに配置します。
5. [https://gitlab.com/Melledy/LunarCore-Configs](https://gitlab.com/Melledy/LunarCore-Configs) から `Config` フォルダをダウンロードし、それをリソースフォルダに配置します。システムが問い合わせているファイルはすべて置き換えます。これらはワールドの生成に関するもので、サーバーにとって非常に重要です。
6. システムのターミナルから `java -jar LunarCore.jar` を使用してサーバーを実行します。Lunar Coreにはデータベースのための組み込みの内部MongoDBサーバーが付属しているため、MongoDBのインストールは必要ありません。ただし、MongoDBのインストールを強くお勧めします。
7. 設定で `autoCreateAccount` をtrueに設定している場合は、アカウントの作成をスキップできます。そうでない場合は、サーバーコンソールで `/account` コマンドを使用してアカウントを作成します。

### クライアントとの接続（Fiddler）
1. **同じクライアントで公式サーバーとHoyoverseアカウントに少なくとも一度ログインしてゲームデータをダウンロードしてください。**
2. [Fiddler Classic](https://www.telerik.com/fiddler) をインストールし、実行します。
3. Fiddlerをhttpsトラフィックを復号化するように設定します（ツール -> オプション -> HTTPS -> HTTPSトラフィックを復号化）。 `サーバー証明書のエラーを無視する` がチェックされていることを確認してください。
4. Fiddler ClassicのFiddlerscriptタブに以下のコードをコピーして貼り付けます：

```javascript
import System;
import System.Windows.Forms;
import Fiddler;
import System.Text.RegularExpressions;

class Handlers
{
    static function OnBeforeRequest(oS: Session) {
        if (oS.host.EndsWith(".starrails.com") || oS.host.EndsWith(".hoyoverse.com") || oS.host.EndsWith(".mihoyo.com") || oS.host.EndsWith(".bhsr.com")) {
            oS.host = "localhost"; // これは別のIPアドレスに置き換えることもできます。
        }
    }
};
```

5. 作成したアカウント名と任意のパスワードでログインします。

### サーバーコマンド
サーバーコマンドはサーバーコンソールまたはゲーム内で実行できます。各プレイヤーのフレンドリストには、ゲーム内でコマンドを使用するための "Server" という名前のユーザーがいます。

```
/account {create | delete} [username] (予約プレイヤーuid). アカウントを作成または削除します。
/avatar lv(level) p(ascension) r(eidolon) s(skill levels). 現在のアバターのプロパティを設定します
/clear {relics | lightcones | materials | items}. プレイヤーのインベントリからフィルタリングされたアイテムを削除します。
/gender {male | female}. プレイヤーの性別を設定します。
/give [item id] x[amount]. ターゲットのプレイヤーにアイテムを与えます。
/giveall {materials | avatars}. ターゲットのプレイヤーにアイテムを与えます。
/help. 利用可能なコマンドの一覧を表示します。
/mail [content]. ターゲットのプレイヤーにシステムメールを送信します。
/permission {add | remove | clear} [permission]. ターゲットのプレイヤーから権限を付与/削除します。
/reload. サーバーコンフィギュレーションを再読み込みします。
/scene [scene id] [floor id]. プレイヤーを指定したシーンにテレポートします。
/spawn [monster/prop id] x[amount] s[stage id]. ターゲットのプレイヤーの近くにモンスターまたはプロップを生成します。
/unstuck @[player id]. オフラインプレイヤーが読み込み不可のシーンにいる場合、スタックを解除します。
/worldlevel [world level]. ターゲットのプレイヤーの平衡レベルを設定します。
/refill. SPを回復します。
```
