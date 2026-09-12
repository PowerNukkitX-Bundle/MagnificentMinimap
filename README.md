<h1 align="center">Magnificent Minimap for PowerNukkitX</h1>

<p align="center">
  A real-time minimap for Minecraft Bedrock players on PowerNukkitX servers.
</p>

<p align="center">
  <img alt="PowerNukkitX" src="https://img.shields.io/badge/PowerNukkitX-3.0.4%2B-blue">
  <img alt="Minecraft Bedrock" src="https://img.shields.io/badge/Minecraft-Bedrock-brightgreen">
  <img alt="Java" src="https://img.shields.io/badge/Java-21-orange">
  <img alt="License" src="https://img.shields.io/badge/License-MIT-yellow">
</p>

Magnificent Minimap brings the look and feel of the original **Magnificent Minimap** Bedrock add-on to a PowerNukkitX server.

Players get a live map directly in their HUD — **no client mod, behavior pack or manual resource-pack installation required**. The required resource pack is bundled inside the plugin JAR and is sent by PowerNukkitX when a player joins.

> [!NOTE]
> This is an unofficial PowerNukkitX port of the original [Magnificent Minimap](https://www.curseforge.com/minecraft-bedrock/addons/magnificent-minimap) add-on.

## ✨ Features

- 🗺️ Real-time terrain minimap
- 🧭 Compass around the minimap
- 🔄 Optional rotating map
- ⭕ Round or square minimap
- 👥 Other players shown on the map
- 🔍 Configurable zoom and render detail
- 🐾 Enlarged minimap while sneaking
- 🎨 Five different terrain rendering styles
- ⚡ Configurable update rate
- 📦 Resource pack bundled directly in the plugin
- 🎮 Works with normal Minecraft Bedrock clients

<p align="center">
  <img
    src="https://media.forgecdn.net/attachments/1338/99/screenshot-2025-09-27-233413-png.png"
    alt="Magnificent Minimap in-game"
    width="780">
</p>

<p align="center"><sub>Original Magnificent Minimap visual. The PowerNukkitX port uses the same minimap UI/resources.</sub></p>

## 📥 Installation

1. Download the latest `MagnificentMinimap.jar` from the GitHub Releases page.
2. Put the JAR into your PowerNukkitX `plugins/` folder.
3. Restart the server.
4. Join the server.
5. Accept the server resource pack when Minecraft asks for it.

That's it.

You **do not** need to install a separate `.mcpack`, behavior pack, UI Queue or Script API dependency.

```text
server/
├── plugins/
│   └── MagnificentMinimap.jar
└── ...
```

> [!IMPORTANT]
> Players must load the server resource pack. Without it, the minimap UI cannot be displayed correctly.

## 🎮 Commands

| Command | Description |
|---|---|
| `/minimap` | Enable or disable your minimap |
| `/minimap toggle` | Enable or disable your minimap |
| `/minimap reload` | Reload the plugin configuration and clear the map cache |

`/minimap reload` requires the `magnificentminimap.admin` permission and is intended for server administrators.

The player toggle is currently **session based**. If a player disconnects and joins again, the minimap is enabled again.

## ⚙️ Configuration

The default configuration is designed to work well for most servers:

```yaml
enabled: true
fps: 5
zoom: 7.0
render-detail: 0

rotate-minimap: false
round-minimap: true
enlarge-on-sneak: true
render-players: true

style: VANILLA
cache-ttl-ms: 5000
```

After changing the configuration, use:

```text
/minimap reload
```

### Common settings

| Setting | What it does |
|---|---|
| `enabled` | Enables or disables the minimap globally |
| `fps` | How often the minimap is refreshed |
| `zoom` | Changes how much of the surrounding world is visible |
| `render-detail` | Changes the minimap render resolution (`0` - `3`) |
| `rotate-minimap` | Rotates the map with the player's view |
| `round-minimap` | Uses the round minimap instead of the square version |
| `enlarge-on-sneak` | Makes the minimap larger while sneaking |
| `render-players` | Shows other players in the same world |
| `style` | Selects the terrain rendering style |
| `cache-ttl-ms` | Controls how long terrain data is cached |

### Performance

Higher `fps` and `render-detail` values require more server work.

For larger servers, start with:

```yaml
fps: 5
render-detail: 0
cache-ttl-ms: 5000
```

and increase the quality only if your server has enough performance headroom.

## 🎨 Rendering Styles

The minimap supports five terrain styles.

### `VANILLA`

The default. Uses Minecraft-like map shading and is a good choice for normal gameplay.

```yaml
style: VANILLA
```

### `MAGNIFICENT`

Uses stronger light and shadow differences to make terrain elevation easier to see.

```yaml
style: MAGNIFICENT
```

### `MODERN`

A stronger slope-based shading style with more visible terrain depth.

```yaml
style: MODERN
```

### `SIMPLE`

Displays the map colors without height shading.

```yaml
style: SIMPLE
```

### `HEIGHT`

Displays the terrain as a grayscale height map.

```yaml
style: HEIGHT
```

## 🧭 Rotation

By default, north stays at the top of the map:

```yaml
rotate-minimap: false
```

To rotate the entire map together with the player's view:

```yaml
rotate-minimap: true
```

## ⭕ Round / Square Map

Round minimap:

```yaml
round-minimap: true
```

Square minimap:

```yaml
round-minimap: false
```

## 👥 Player Markers

Other online players in the same world can be displayed directly on the minimap:

```yaml
render-players: true
```

Disable player markers with:

```yaml
render-players: false
```

## 🔎 Enlarged Minimap

When enabled, sneaking temporarily enlarges the minimap:

```yaml
enlarge-on-sneak: true
```

<p align="center">
  <img
    src="https://media.forgecdn.net/attachments/1338/101/screenshot-2025-09-27-233745-png.png"
    alt="Enlarged Magnificent Minimap"
    width="780">
</p>

## ❓ Troubleshooting

### The minimap does not appear

Make sure the Minecraft client accepted and loaded the server resource pack. Reconnecting after the resource pack has been downloaded can also help.

### I only see strange symbols or UI elements

The bundled resource pack was not loaded correctly. Restart Minecraft, reconnect to the server and make sure server resource packs are enabled/accepted.

### The minimap causes too much server load

Reduce the update rate and/or render detail:

```yaml
fps: 3
render-detail: 0
```

Increasing `cache-ttl-ms` can also reduce repeated terrain lookups.

### `/minimap toggle` is not persistent

This is currently expected. The minimap is enabled again when the player reconnects.

## ❤️ Credits

This project is a PowerNukkitX adaptation of the original **Magnificent Minimap** Minecraft Bedrock add-on.

- Original project: [Magnificent Minimap on CurseForge](https://www.curseforge.com/minecraft-bedrock/addons/magnificent-minimap)
- Original author: **qduoubp**
- Original resource-pack credits: **Kamii**
- PowerNukkitX port: **Buddelbubi / PowerNukkitX-Bundle**

The original project is distributed under the **MIT License**. Third-party license information for the bundled assets is included with this project.

## 📜 License

See the repository license for the licenses applying to this project and the bundled Magnificent Minimap resources.

---

<p align="center">
  <b>Explore your Bedrock world without losing your way.</b>
</p>
