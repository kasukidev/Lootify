# 🎁 Lootify

**Lootify** is a lightweight Minecraft plugin for creating and managing custom loot tables with weighted chances.  
It uses **SQLite** for reliable and efficient storage — perfect for random drop systems, crate rewards, or rotating loot mechanics.

---

## ✨ Features
- 🎲 Define and manage loot tables easily.
- ⚖️ Support for **weighted item chances**.
- 💾 Built-in **SQLite persistence** (no external setup).
- 🧩 Simple **developer API** for plugin integration.
- ⚙️ Caching for fast access and minimal performance impact.
- 🧬 **Protocol Buffers (protobuf)** powered serialization for efficient, portable data handling.
- 💬 In-game commands for managing loot tables.

---

## 📦 Installation
1. Download the latest release of **Lootify** from the Releases page.
2. Drop the `.jar` into your server’s `plugins/` directory.
3. Restart the server — Lootify will automatically set up its configuration and database.

> ⚠️ **Developers:**  
If you’re integrating Lootify into your own plugin, **add it to your `plugin.yml`**:
```yaml
depend: [Lootify]
```

---

## 🧰 Developer Example

Here’s an example of how to use **Lootify’s API** to roll a random item from a loot table and apply it to your own plugin logic.

```java
package cc.insidious.mercury.bah.task;

import cc.insidious.lootify.api.LootifyAPI;
import cc.insidious.lootify.api.loottable.ILootTableHandler;
import cc.insidious.lootify.api.loottable.helper.LootTableHelper;
import org.bukkit.inventory.ItemStack;
import java.util.Optional;

public class LootifyExample {

    private final ILootTableHandler lootTableHandler;

    public LootifyExample() {
        // Access the Lootify API
        this.lootTableHandler = LootifyAPI.getInstance().get(ILootTableHandler.class);
    }

    public void doThing() {
        // Retrieve the loot table and roll a random entry
        Optional<ItemStack> entry = lootTableHandler.getFromCache("LOOTIFY_EXAMPLE")
                .map(wrapper -> new LootTableHelper(lootTableHandler).getRandomEntry(wrapper));

        entry.ifPresent(item -> {
            // Example: Do something with your item
            System.out.println("The retrieved item was: " + item.getType());
        });
    }
}
```

### 💡 How It Works
- `LootifyAPI.getInstance()` → Retrieves the main API instance.
- `ILootTableHandler` → Handles all loot table fetching, caching, and management.
- `LootTableHelper#getRandomEntry()` → Randomly selects a weighted entry from a given table.
- The loot table (`"LOOTIFY_EXAMPLE"`) must exist in Lootify beforehand.

---

## 🧱 Registering a Loot Table

To register your own custom loot tables during plugin initialization, implement Lootify’s `IRegistrationHandler` interface.

```java
package cc.insidious.lootify.registration.data;

import cc.insidious.lootify.LootifyPlugin;
import cc.insidious.lootify.api.loottable.ILootTableHandler;
import cc.insidious.lootify.api.loottable.helper.LootTableHelper;
import cc.insidious.lootify.api.registration.IRegistrationHandler;

import java.util.stream.Stream;

public class LootTableRegistrationHandler implements IRegistrationHandler {
    private final LootifyPlugin instance;
    private final ILootTableHandler lootTableHandler;

    public LootTableRegistrationHandler(LootifyPlugin instance) {
        this.instance = instance;
        this.lootTableHandler = instance.getLootifyAPI().get(ILootTableHandler.class);
    }

    @Override
    public void registerObjects() {
        LootTableHelper lootifyHelper = new LootTableHelper(lootTableHandler);

        // Register one or more loot tables to be managed by Lootify
        Stream.of("LOOTIFY_EXAMPLE")
                .forEachOrdered(lootifyHelper::setupLootTable);
    }
}
```

### 🧭 When to Register
You should call your registration logic (for example, `new LootTableRegistrationHandler(this).registerObjects();`)
inside your plugin’s `onEnable()` method or equivalent initialization stage.

---

## ⚙️ Commands & Permissions

Lootify includes built-in commands for managing loot tables directly in-game.

### 📜 Commands

| Command | Aliases | Description | Permission |
|----------|----------|--------------|-------------|
| `/lootify` | `/loottable` | Opens the main Loot Table menu. | `lootify.admin` |
| `/lootify delete <id>` | - | Deletes a loot table by ID from cache and database. | `lootify.admin` |

### 🔒 Permissions

| Node | Description |
|------|--------------|
| `lootify.admin` | Allows access to all Lootify commands and menu interfaces. |

---

## 🧩 Technical Details
Lootify uses:
- **SQLite** for persistent local storage.
- **Protocol Buffers (protobuf)** for efficient binary serialization of loot tables and cached data.
- **Spigot/Paper API** for server-side integration.

---

## 🪪 License
This project is licensed under the [MIT License](license).

---

### 💬 Support
For help, issues, or contributions — visit [GitHub](https://github.com/kasukidev/Lootify).
