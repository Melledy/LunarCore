![LunarCore](https://socialify.git.ci/Melledy/LunarCore/image?description=1&descriptionEditable=A%20game%20server%20reimplementation%20for%20version%201.5.0%20of%20a%20certain%20turn-based%20anime%20game%20for%20educational%20purposes.%20&font=Inter&forks=1&issues=1&language=1&name=1&owner=1&pulls=1&stargazers=1&theme=Light)
<div align="center"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/Melledy/LunarCore?logo=java&style=for-the-badge"> <img alt="GitHub" src="https://img.shields.io/github/license/Melledy/LunarCore?style=for-the-badge"> <img alt="GitHub last commit" src="https://img.shields.io/github/last-commit/Melledy/LunarCore?style=for-the-badge"> <img alt="GitHub Workflow Status" src="https://img.shields.io/github/actions/workflow/status/Melledy/LunarCore/build.yml?branch=development&logo=github&style=for-the-badge"></div>

<div align="center"><a href="https://discord.gg/cfPKJ6N5hw"><img alt="Discord - Grasscutter" src="https://img.shields.io/discord/1163718404067303444?label=Discord&logo=discord&style=for-the-badge"></a></div>

[EN](../README.md) | [简中](README_zh-CN.md) | [繁中](README_zh-TW.md) | [JP](README_ja-JP.md) | [RU](README_ru-RU.md) | [FR](README_fr-FR.md) | [KR](README_ko-KR.md)

**Внимание:** Для получения дополнительной поддержки, вопросов или обсуждений заходите на наш [Discord](https://discord.gg/cfPKJ6N5hw).

### Примечательные особенности
- Основные возможности игры: Вход в игру, настройка команды, инвентарь, базовое управление сценой/содержимым
- Работают сражения с монстрами
- Спавны монстров/природы/NPC в естественном мире
- Работает большинство техник персонажей
- Работают магазины Npc
- Система гача
- Почтовая система
- Система друзей (помощники пока не работают)
- Забытый зал (с функциями 1.4.0)
- Симулированная вселенная (Запуск может быть закончен, но многие функции отсутствуют)

# Запуск сервера и клиента

### Необходимые условия
* [Java 17 JDK](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)

### Рекомендуем
* [MongoDB 4.0+](https://www.mongodb.com/try/download/community)

Компиляция сервера
1. Откройте системный терминал и скомпилируйте сервер с помощью `./gradlew jar`.
2. Создайте папку с именем `resources` в каталоге сервера.
3. Скачайте папки `Config`, `TextMap` и `ExcelBin` с сайта [https://github.com/Dimbreath/StarRailData](https://github.com/Dimbreath/StarRailData) и поместите их в папку resources.
4. Скачайте папку `Config` с сайта [https://gitlab.com/Melledy/LunarCore-Configs](https://gitlab.com/Melledy/LunarCore-Configs) и поместите ее в папку resources. Замените все файлы, которые запрашивает ваша система. Они предназначены для спавна мира и очень важны для сервера.
5. Запустите сервер с помощью команды `java -jar LunarCore.jar` из системного терминала. Lunar Core поставляется со встроенным внутренним сервером MongoDB для своей базы данных, поэтому установка Mongodb не требуется. Однако настоятельно рекомендуется установить Mongodb в любом случае.
6. Если в конфиге `autoCreateAccount` установлено значение true, то создание учетной записи можно пропустить. В противном случае используйте команду `/account` в консоли сервера для ее создания.

### Подключение к клиенту (Fiddler)
1. **Войдите с клиентом на официальный сервер и в аккаунт Hoyoverse хотя бы один раз, чтобы загрузить игровые данные**.
2. Установите и запустите [Fiddler Classic](https://www.telerik.com/fiddler).
3. Настройте fiddler на расшифровку https-трафика. (Tools -> Options -> HTTPS -> Decrypt HTTPS traffic) Убедитесь, что `ignore server certificate errors` также отмечен.
4. Скопируйте и вставьте следующий код во вкладку Fiddlerscript в Fiddler Classic:

```
import System;
import System.Windows.Forms;
import Fiddler;
import System.Text.RegularExpressions;

class Handlers
{
    static function OnBeforeRequest(oS: Session) {
        if (oS.host.EndsWith(".starrails.com") || oS.host.EndsWith(".hoyoverse.com") || oS.host.EndsWith(".mihoyo.com") || oS.host.EndsWith(".bhsr.com")) {
            oS.host = "localhost"; // Его также можно заменить другим IP-адресом.
        }
    }
};
```

5. Войдите в систему под своим именем, пароль может быть любым.

### Команды сервера
Команды сервера можно выполнять в консоли сервера или в игре. В списке друзей каждого игрока есть фиктивный пользователь с именем "Сервер", которому можно написать сообщение, чтобы использовать внутриигровые команды.

```
/account {create | delete} [username] (reserved player uid). Создает или удаляет учетную запись.
/avatar lv(level) p(ascension) r(eidolon) s(skill levels). Устанавливает свойства текущего аватара.
/clear {relics | lightcones | materials | items}. Удаляет отфильтрованные предметы из инвентаря игрока.
/gender {male | female}. Устанавливает пол игрока.
/give [item id] x[amount] lv[number]. Дает целевому игроку предмет.
/giveall {materials | avatars | lightcones | relics}. Дает целевому игроку предметы.
/heal. Лечит ваши аватары.
/help. Отображает список доступных команд.
/kick @[player id]. Выгоняет игрока с сервера.
/mail [content]. Отправляет целевому игроку системное письмо.
/permission {add | remove | clear} [permission]. Дает/снимает разрешение с выбранного игрока.
/refill. Пополнение очков навыков в открытом мире.
/reload. Перезагружает конфигурацию сервера.
/scene [scene id] [floor id]. Телепортирует игрока в указанную сцену.
/spawn [monster/prop id] x[amount] s[stage id]. Порождает монстра или реквизит рядом с игроком.
/unstuck @[player id]. Отключает оффлайн-игрока, если он находится в сцене, которая не загружается.
/worldlevel [world level]. Устанавливает равновесный уровень целевого игрока.
```
