# JemLives

![GitHub release (latest by date)](https://img.shields.io/github/v/release/Jemsire/JemLives)
![GitHub stars](https://img.shields.io/github/stars/Jemsire/JemLives?style=social)
![GitHub issues](https://img.shields.io/github/issues/Jemsire/JemLives)
![GitHub pull requests](https://img.shields.io/github/issues-pr/Jemsire/JemLives)
![GitHub license](https://img.shields.io/github/license/Jemsire/JemLives)

A Hytale server plugin that implements a limited lives system. Players start with a set amount of lives and lose them upon death. Once they run out of lives, they are penalized (e.g., kicked) until their lives regenerate.

---

# BETA!!!! - Not a ton of testing just yet and unfinished features!
*Use at your own risk!*

---

## Features

- **Limited Lives System**: Players start with a configurable number of lives (fixed or randomized).
- **Life Loss on Death**: Players lose a life when they die. Configurable to trigger on all deaths or PVP deaths only.
- **PVP Rewards**[Unfinished]: Optional chance to gain a life when killing another player.
- **Lives Regeneration**: Automatically restores lives after a configurable cooldown period.
- **Enhanced Death Messages**: Broadcasts death messages globally with the player's remaining life count.
- **Local Notifications**: Sends private messages to players upon death with their current status.
- **Rich Text Support**: Fully customizable messages with colors, gradients, and styles via TinyMsg.
- **Hot Reload**: Reload configuration without restarting the server using `/jemlives reload`.

## Installation

1. Download the latest release from the [releases page](https://github.com/jemsire/JemLives/releases)
2. Place the `JemLives-x.x.x.jar` file into your Hytale server's `mods` folder
3. Start your server to generate the configuration file
4. (Optional) Edit the `Jemsire_JemLives/LivesConfig.json` file to customize settings
5. In-game type `/jemlives reload` to reload the config if you made changes

## Configuration

After first launch, a configuration file will be created at `Jemsire_JemLives/LivesConfig.json`:

```json
{
  "InitialLivesMin": 3,
  "InitialLivesMax": 3,
  "LoseLivesFromPvpOnly": false,
  "GainLivesFromKills": false,
  "GainLifeChance": 0.1,
  "ZeroLivesAction": "KICK",
  "RegenTimeMinutes": 60,
  "LogLevel": "INFO",
  "DeathAnnouncementFormat": "<orange>{player} <red>has died and now has <orange>{lives} <red>lives.",
  "LocalDeathMessage": "<red>You died! You have <orange>{lives} <red>lives left.",
  "LivesCommandMessage": "<green>You have {lives} lives left.",
  "KickMessage": "You have run out of lives! Come back in {time}.",
  "DeathCauseReplacement": "was",
  "ShowLivesHud": true,
  "HudIconPath": "Hud/Essense.gif"
}
```

### Configuration Options

- **InitialLivesMin** / **InitialLivesMax**: The range for starting lives. If they are equal, players start with that fixed amount.
- **LoseLivesFromPvpOnly**[Unfinished]: If `true`, players only lose lives when killed by another player.
- **GainLivesFromKills**[Unfinished]: If `true`, players have a chance to gain a life when they kill another player.
- **GainLifeChance**[Unfinished]: The probability (0.0 to 1.0) of gaining a life on a PVP kill.
- **ZeroLivesAction**: Action taken when a player reaches 0 lives (e.g., `KICK`).
- **RegenTimeMinutes**: Time in minutes before a player's lives are restored after reaching zero.
- **LogLevel**: Logging level for the plugin (`INFO`, `DEBUG`, `WARN`, `SEVERE`).
- **DeathAnnouncementFormat**: Format for the global death message.
  - Placeholders: `{player}`, `{playerName}`, `{deathCause}`, `{rawDeathCause}`, `{lives}`
- **LocalDeathMessage**: Private message sent to the player who died.
- **LivesCommandMessage**: Message shown when a player uses the `/lives` command.
- **KickMessage**: Message shown when a player is kicked for having 0 lives.
  - Placeholders: `{time}` (remaining time until regen)
- **DeathCauseReplacement**: Replaces "You were" in the default Hytale death messages.
- **ShowLivesHud**: If `true`, shows the lives HUD near the hotbar.
- **HudIconPath**: Path to the icon image, relative to `Common/UI/Custom/` (e.g. `"Hud/Essense.gif"` or `"JemLives/heart.png"`).

### Using images in the HUD

Images in Hytale UI use a **Group** with **Background: PatchStyle(TexturePath: "path")**. The path is relative to your plugin’s `Common/UI/Custom/` folder (same place as `.ui` files).

1. **Add your image**  
   *Sadly .gif is not supported that I have found*

   Put your PNG (e.g. `Essense.png`) in `src/main/resources/Common/UI/Custom/Hud/` (or a subfolder). It will be bundled in the plugin JAR.

2. **Reference it in the .ui file**  
   In a `.ui` file, use:
   ```
   Group #hudIcon {
     Anchor: (Width: 32, Height: 32);
     Background: PatchStyle(TexturePath: "Hud/Essense.gif", Border: 0);
   }
   ```
   `Border: 0` stretches the image to the Anchor size. Use a positive `Border` (e.g. `12`) for 9-slice scaling.

3. **Config**  
   Set **HudIconPath** to the path relative to `Common/UI/Custom/` (e.g. `"Hud/Essense.gif"` or `"JemLives/heart.png"`). The default icon path is `Hud/Essense.gif`.

### Color Formatting

All message strings support **advanced color formatting** using tags or legacy color codes. The plugin uses TinyMsg for rich text formatting.

**Supported Color Formats:**
- **Named Color Tags**: `<red>`, `<blue>`, `<green>`, `<yellow>`, `<gold>`, `<orange>`, etc.
- **Hex Color Tags**: `<color:#FF0000>` or `<#FF0000>`
- **Legacy Color Codes**: `&a`, `&c`, `&e`, etc.

**Advanced Formatting Features:**
- **Gradients**: `<gradient:#FF0000:#00FF00>Rainbow text</gradient>`
- **Text Styles**: `<bold>`, `<italic>`, `<underline>`, `<monospace>`

## Commands

- `/lives` - Check your remaining lives (same as `/jemlives check`)
- `/jemlives check` - Check your remaining lives
- `/jemlives info` - Open the lives info UI
- `/jemlives reload` - Reload the plugin configuration (admin)

### Permissions

| Permission       | Description                                      |
|------------------|--------------------------------------------------|
| `jemlives.check` | Use `/lives` and `/jemlives check`               |
| `jemlives.info`  | Use `/jemlives info` (open lives info page)      |
| `jemlives.reload` | Use `/jemlives reload` (reload config)        |

If a player lacks the required permission, they see a red "You do not have permission to perform this command!" message.

## Project Structure

```
JemLives/
├── src/main/java/com/jemsire/
│   ├── commands/
│   │   └── LivesCommand.java            # Command handler for /lives and /jemlives
│   ├── config/
│   │   ├── LivesConfig.java             # Lives system configuration
│   │   └── PlayerData.java              # Player data model
│   ├── events/
│   │   ├── OnPlayerConnectEvent.java    # Handles player connection
│   │   ├── OnPlayerDeathEvent.java      # Handles player death and life loss
│   │   ├── OnPlayerLeaveEvent.java      # Handles player disconnection
│   │   └── OnPlayerReadyEvent.java      # Handles player readiness
│   ├── plugin/
│   │   └── JemLives.java                # Main plugin class
│   ├── ui/
│   │   ├── LivesHud.java                # Custom HUD implementation
│   │   └── LivesInfoPage.java           # Custom info page UI
│   └── utils/
│       ├── LivesManager.java            # Manages player lives data and persistence
│       ├── LivesHudManager.java         # Manages HUD lifecycle
│       ├── KickManager.java             # Manages player kicks/regeneration
│       ├── ChatBroadcaster.java         # Utility for broadcasting messages
│       ├── ColorUtils.java              # Color parsing utilities
│       ├── Logger.java                  # Logging utility
│       ├── TinyMsg.java                 # Advanced message formatting
│       └── PlaceholderReplacer.java     # Placeholder replacement utility
├── src/main/resources/
│   ├── Common/UI/Custom/
│   │   └── Hud/
│   │       ├── lives_hud.ui             # HUD layout
│   │       ├── LivesInfoPage.ui         # Info page layout
│   │       └── Essense.gif               # HUD icon
│   └── manifest.json                    # Plugin metadata
├── build.gradle.kts                     # Gradle build configuration
└── settings.gradle.kts                  # Gradle project settings
```

## Building from Source

### Prerequisites

- Java Development Kit (JDK) 17/25
- Gradle 8.0 or higher

### Build Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/jemsire/JemLives.git
   cd JemLives
   ```

2. Build the project:
   ```bash
   ./gradlew build
   ```

3. The compiled JAR will be in `build/libs/JemLives-x.x.x.jar`

## Technical Details

### Data Persistence
Player lives are stored in individual JSON files within the `players/` folder of the plugin directory (e.g., `mods/Jemsire_JemLives/players/882cf635-0575-4400-ac75-ccaeb0149521.json`). This ensures that data is persisted across server restarts and remains easily accessible for administrators.

### Troubleshooting

1. **Check Configuration**: Verify `LivesConfig.json` exists in `Jemsire_JemLives/`.
2. **Reload Config**: Use `/jemlives reload` after changes.
3. **Check Server Logs**: Look for `[JemLives]` entries in the console.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Author

**TinyTank800**

- Website: [https://jemsire.com/JemLives](https://jemsire.com/JemLives)

## Support

For issues, feature requests, or questions, please open an [Issue](https://github.com/jemsire/JemLives/issues).
