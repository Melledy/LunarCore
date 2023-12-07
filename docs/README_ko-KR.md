![LunarCore](https://socialify.git.ci/Melledy/LunarCore/image?description=1&descriptionEditable=A%20game%20server%20reimplementation%20for%20version%201.5.0%20of%20a%20certain%20turn-based%20anime%20game%20for%20educational%20purposes.%20&font=Inter&forks=1&issues=1&language=1&name=1&owner=1&pulls=1&stargazers=1&theme=Light)
<div align="center"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/Melledy/LunarCore?logo=java&style=for-the-badge"> <img alt="GitHub" src="https://img.shields.io/github/license/Melledy/LunarCore?style=for-the-badge"> <img alt="GitHub last commit" src="https://img.shields.io/github/last-commit/Melledy/LunarCore?style=for-the-badge"> <img alt="GitHub Workflow Status" src="https://img.shields.io/github/actions/workflow/status/Melledy/LunarCore/build.yml?branch=development&logo=github&style=for-the-badge"></div>

<div align="center"><a href="https://discord.gg/cfPKJ6N5hw"><img alt="Discord - Grasscutter" src="https://img.shields.io/discord/1163718404067303444?label=Discord&logo=discord&style=for-the-badge"></a></div>

[EN](../README.md) | [简中](README_zh-CN.md) | [繁中](README_zh-TW.md) | [JP](README_ja-JP.md) | [RU](README_ru-RU.md) | [FR](README_fr-FR.md) | [KR](README_ko-KR.md)

**주의: **추가 지원, 질문 또는 토론이 필요한 경우, [Discord](https://discord.gg/cfPKJ6N5hw) 를 확인하세요.

### 주목할 만한 기능
- 기본적인 게임 기능: 로그인, 팀 설정, 인벤토리, 기본 장면/엔티티 관리
- 몬스터 전투 작동
- 자연계 몬스터/소품/NPC 생성
- 대부분의 캐릭터 기술 처리
- NPC 상점 처리
- 뽑기 시스템
- 메일 시스템
- 친구 시스템(어시스트는 아직 작동하지 않음)
- 잊혀진 홀 (1.4.0 기능 포함)
- 시뮬레이션된 우주(실행은 가능하지만 많은 기능이 누락됨)

# 서버 및 클라이언트 실행

### 전제 조건
* [Java 17 JDK](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)

### 추천
* [MongoDB 4.0+](https://www.mongodb.com/try/download/community)

서버 컴파일하기 ###
1. 시스템 터미널을 열고 다음을 사용하여 서버를 컴파일합니다. `./gradlew jar`
2. 서버 디렉터리에 `resources`라는 이름의 폴더를 만듭니다.
3. [https://github.com/Dimbreath/StarRailData](https://github.com/Dimbreath/StarRailData) 에서 `Config`, `TextMap`, `ExcelBin` 폴더를 다운로드하여 리소스 폴더에 넣습니다.
4. [https://gitlab.com/Melledy/LunarCore-Configs](https://gitlab.com/Melledy/LunarCore-Configs) 에서 `Config` 폴더를 다운로드하여 리소스 폴더에 넣습니다. 시스템에서 요청하는 파일을 모두 교체하세요. 이 파일들은 월드 스폰을 위한 것으로 서버에 매우 중요합니다.
5. 시스템 터미널에서 `java -jar LunarCore.jar`로 서버를 실행합니다. Lunar Core에는 데이터베이스를 위한 내부 MongoDB 서버가 내장되어 있으므로 Mongodb를 설치할 필요가 없습니다. 하지만 어쨌든 Mongodb를 설치하는 것을 적극 권장합니다.
6. 설정에서 `autoCreateAccount`가 true로 설정되어 있으면 계정 생성을 건너뛸 수 있습니다. 그렇지 않은 경우 서버 콘솔에서 `/account` 명령을 사용하여 계정을 생성합니다.

### 클라이언트와 연결하기(피들러)
1. **게임 데이터를 다운로드하려면 클라이언트로 공식 서버와 호오버스 계정에 한 번 이상 로그인합니다.
2. [Fiddler Classic](https://www.telerik.com/fiddler) 을 설치하여 실행합니다.
3. 피들러가 https 트래픽을 복호화하도록 설정합니다. (도구 -> 옵션 -> HTTPS -> HTTPS 트래픽 복호화) '서버 인증서 오류 무시'도 체크되어 있는지 확인합니다.
4. 피들러 클래식의 피들러스크립트 탭에 다음 코드를 복사하여 붙여넣습니다:

```
import System;
import System.Windows.Forms;
import Fiddler;
import System.Text.RegularExpressions;

class Handlers
{
    static function OnBeforeRequest(oS: Session) {
        if (oS.host.EndsWith(".starrails.com") || oS.host.EndsWith(".hoyoverse.com") || oS.host.EndsWith(".mihoyo.com") || oS.host.EndsWith(".bhsr.com")) {
            oS.host = "localhost"; // 이 주소는 다른 IP 주소로 대체할 수도 있습니다.
        }
    }
};
```

5. 계정 이름으로 로그인하며, 비밀번호는 아무거나 설정할 수 있습니다.

### 서버 명령
서버 명령은 서버 콘솔이나 게임 내에서 실행할 수 있습니다. 모든 플레이어의 친구 목록에 "서버"라는 이름의 더미 사용자가 있으며, 이 사용자에게 게임 내 명령을 사용하도록 메시지를 보낼 수 있습니다.

```
/account {create | delete} [username] (reserved player uid). 계정을 만들거나 삭제합니다.
/avatar lv(level) p(ascension) r(eidolon) s(skill levels). 현재 아바타의 속성을 설정합니다.
/clear {relics | lightcones | materials | items}. 플레이어 인벤토리에서 필터링된 아이템을 제거합니다.
/gender {male | female}. 플레이어 성별을 설정합니다.
/give [item id] x[amount] lv[number]. 대상 플레이어에게 아이템을 부여합니다.
/giveall {materials | avatars | lightcones | relics}. 대상 플레이어에게 아이템을 부여합니다.
/heal. 아바타를 치료합니다.
/help. 사용 가능한 명령 목록을 표시합니다.
/kick @[player id]. 플레이어를 서버에서 내쫓습니다.
/mail [content]. 대상 플레이어에게 시스템 메일을 보냅니다.
/permission {add | remove | clear} [permission]. 대상 플레이어에게 권한을 부여/제거합니다.
/refill. 오픈 월드에서 스킬 포인트를 다시 채웁니다.
/reload. 서버 구성을 다시 로드합니다.
/scene [scene id] [floor id]. 플레이어를 지정된 장면으로 순간이동시킵니다.
/spawn [monster/prop id] x[amount] s[stage id]. 대상 플레이어 근처에 몬스터나 소품을 스폰합니다.
/unstuck @[player id]. 오프라인 플레이어가 로딩되지 않는 장면에 있을 경우 플레이어를 고정 해제합니다.
/worldlevel [world level]. 대상 플레이어의 평형 레벨을 설정합니다.
```
