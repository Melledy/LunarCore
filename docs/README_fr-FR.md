![LunarCore](https://socialify.git.ci/Melledy/LunarCore/image?description=1&descriptionEditable=A%20game%20server%20reimplementation%20for%20version%201.5.0%20of%20a%20certain%20turn-based%20anime%20game%20for%20educational%20purposes.%20&font=Inter&forks=1&issues=1&language=1&name=1&owner=1&pulls=1&stargazers=1&theme=Light)
<div align="center"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/Melledy/LunarCore?logo=java&style=for-the-badge"> <img alt="GitHub" src="https://img.shields.io/github/license/Melledy/LunarCore?style=for-the-badge"> <img alt="GitHub last commit" src="https://img.shields.io/github/last-commit/Melledy/LunarCore?style=for-the-badge"> <img alt="GitHub Workflow Status" src="https://img.shields.io/github/actions/workflow/status/Melledy/LunarCore/build.yml?branch=development&logo=github&style=for-the-badge"></div>

<div align="center"><a href="https://discord.gg/cfPKJ6N5hw"><img alt="Discord - Grasscutter" src="https://img.shields.io/discord/1163718404067303444?label=Discord&logo=discord&style=for-the-badge"></a></div>

[EN](../README.md) | [简中](README_zh-CN.md) | [繁中](README_zh-TW.md) | [JP](README_ja-JP.md) | [RU](README_ru-RU.md) | [FR](README_fr-FR.md) | [KR](README_ko-KR.md)

**Attention:** Pour tout soutien supplémentaire, questions ou discussions, consultez notre [Discord](https://discord.gg/cfPKJ6N5hw).

### Caractéristiques notables
- Fonctionnalités de base du jeu : Connexion, configuration de l'équipe, inventaire, gestion de base des scènes et des entités
- Les batailles de monstres fonctionnent
- Apparition de monstres/prop/NPC dans le monde naturel
- La plupart des techniques de personnages sont gérées
- Les boutiques de PNJ sont gérées
- Système de Gacha
- Système de courrier
- Système d'amis (les aides ne fonctionnent pas encore)
- Salle oubliée (avec les fonctionnalités de la 1.4.0)
- Univers simulé (les runs peuvent être terminés, mais il manque de nombreuses fonctionnalités)

# Exécution du serveur et du client

### Prérequis
* [Java 17 JDK](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)

### Recommandé
* [MongoDB 4.0+](https://www.mongodb.com/try/download/community)

### Compilation du serveur
1. Ouvrez votre terminal système, et compilez le serveur avec `./gradlew jar`
2. Créez un dossier nommé `resources` dans le répertoire de votre serveur.
3. Téléchargez les dossiers `Config`, `TextMap`, et `ExcelBin` depuis [https://github.com/Dimbreath/StarRailData](https://github.com/Dimbreath/StarRailData) et placez-les dans votre dossier resources.
4. Téléchargez le dossier `Config` depuis [https://gitlab.com/Melledy/LunarCore-Configs](https://gitlab.com/Melledy/LunarCore-Configs) et placez-le dans votre dossier resources. Remplacez tous les fichiers demandés par votre système. Ceux-ci sont destinés à l'apparition des mondes et sont très importants pour le serveur.
5. Lancez le serveur avec `java -jar LunarCore.jar` depuis votre terminal. Lunar Core est livré avec un serveur MongoDB interne intégré pour sa base de données, donc aucune installation de Mongodb n'est nécessaire. Cependant, il est fortement recommandé d'installer Mongodb de toute façon.
6. Si vous avez mis `autoCreateAccount` à true dans la configuration, alors vous pouvez sauter la création d'un compte. Sinon, utilisez la commande `/account` dans la console du serveur pour en créer un.

### Connexion avec le client (Fiddler)
1. **Connectez-vous avec le client à un serveur officiel et à un compte Hoyoverse au moins une fois pour télécharger les données du jeu**.
2. Installez et lancez [Fiddler Classic](https://www.telerik.com/fiddler).
3. Configurez fiddler pour décrypter le trafic https. (Tools -> Options -> HTTPS -> Decrypt HTTPS traffic) Assurez-vous que `ignore server certificate errors` est également coché.
4. Copiez et collez le code suivant dans l'onglet Fiddlerscript de Fiddler Classic :

```
import System;
import System.Windows.Forms;
import Fiddler;
import System.Text.RegularExpressions;

class Handlers
{
    static function OnBeforeRequest(oS: Session) {
        if (oS.host.EndsWith(".starrails.com") || oS.host.EndsWith(".hoyoverse.com") || oS.host.EndsWith(".mihoyo.com") || oS.host.EndsWith(".bhsr.com")) {
            oS.host = "localhost"; // Elle peut également être remplacée par une autre adresse IP.
        }
    }
};
```

5. Connectez-vous avec votre nom de compte, le mot de passe peut être défini comme vous le souhaitez.

### Commandes du serveur
Les commandes du serveur peuvent être exécutées dans la console du serveur ou dans le jeu. Il y a un utilisateur fictif nommé "Server" dans la liste d'amis de chaque joueur auquel vous pouvez envoyer un message pour utiliser les commandes dans le jeu.

```
/account {create | delete} [username] (reserved player uid). Crée ou supprime un compte.
/avatar lv(level) p(ascension) r(eidolon) s(skill levels). Définit les propriétés de l'avatar actuel.
/clear {relics | lightcones | materials | items}. Supprime les objets filtrés de l'inventaire du joueur.
/gender {male | female}. Définit le sexe du joueur.
/give [item id] x[amount] lv[number]. Donne un objet au joueur ciblé.
/giveall {materials | avatars | lightcones | relics}. Donne des objets au joueur ciblé.
/heal. Guérit vos avatars.
/help. Affiche une liste des commandes disponibles.
/kick @[player id]. Expulse un joueur du serveur.
/mail [content]. Envoie un message système au joueur ciblé.
/permission {add | remove | clear} [permission]. Donne/retire une permission au joueur ciblé.
/refill. Recharge vos points de compétence en monde ouvert.
/reload. Recharge la configuration du serveur.
/scene [scene id] [floor id]. Téléporte le joueur vers la scène spécifiée.
/spawn [monster/prop id] x[amount] s[stage id]. Fait apparaître un monstre ou un accessoire à proximité du joueur ciblé.
/unstuck @[player id]. Décroche un joueur hors ligne s'il se trouve dans une scène qui ne se charge pas.
/worldlevel [world level]. Fixe le niveau d'équilibre du joueur ciblé.
```
