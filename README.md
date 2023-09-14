# Woody
### THIS IS BASED OFF TRIUMPH-GUI. SHOUT OUT TO TRIUMPHTEAM.
triumph-gui: https://github.com/TriumphTeam/triumph-gui

No BS High-performance, Minimal and Safe Menus Library

If you need support, Check out the official discord server for help using my plugin/libraries [right here](https://discord.gg/Zj6KBS7UwX)

## Why use menu libraries?
Menu libraries have stuck around our community for a long time.

When making menus yourself you need to adjust it properly to these key features:
- Performance
- Safety
- Proper code
- Features
- Way of usage

and whether you want to make something like a `Menu`, make your own library or "mini-library" for you or even just use raw spigot inventories,

You have to think of the key features and implement them properly, you also might need a lot of menus so that's a lot of events and boring stuff.

That's where menu libraries/frameworks come in and save the day.

You have menu libraries and you use one or some of them, and they tend to have this eyecandy syntax of making menus

you also seem to notice you don't write as much as you would when using the raw spigot inventories and handling all the events yourself and making 20 classes where one is for each inventory

It's all even handled in one singular event.

Some libraries **Such as InventoryFramework, triumph-gui, or mine** even provide other kinds of menus such as Paginated, and more variants of inventories that would be a pain to implement.

This is why you would want to use a menu library, to save the hassle of making your own raw inventories and events

## Why Woody? Why not triumph-gui? Why not InventoryFramework? Why not anything else?
Woody is minimal, Woody is also feature rich, Woody is fast.
triumph-gui is great, and especially IF which is amazing.

Now I don't know about OTHER frameworks, but Woody provides some special features such as:
- Nice Support; everyone likes nice support, I have a lot of free time! (mostly, even if I try to have a life, and no I'm not obese)
- MenuItem editing AFTER CREATION; 
  I had this problem of trying to edit gui/menu items after creation in some libraries like triumph-gui and others
  When making this library I'd also been mentioned a couple times how frustrating it can get to edit a menu item
  after creation, even with a menu library, (triumph-gui v3.1.2 case) you need to:

    Get the item stack from the gui item

    Get the item meta from the item stack

    Manually edit the item meta

    Manually set the item meta

    Update the item

    For Gui it would look something like this:

    ```java
    GuiItem guiItem = gui.getGuiItem(20); // 20 just as an example
    ItemStack item = guiItem.getItemStack();
    ItemMeta meta = item.getItemMeta();
    // do what you want with the meta/item
    gui.updateItem(20, item);
    ```

    Horrendous! What is this? it may even look the same in raw spigot inventories! maybe slightly better, slightly worse.
    Here is the Woody way:

    ```java
    menu.getItem(20).editor() // edit.
        .done(); // when you execute ItemEditor#done() it will literally change the itemStack and action of that object.
    // on v1.3.4 and under you needed to set the item or update it but thats no longer the case.
    ```
    Now you're going to likely write a lot less, have realistically the same performance impact and enjoy readable & concise code.

There can be a lot more too, other than these key features (for now), you might find it as a regular time-saving menu libraries

## Features, How to add, and Example Usage

### Key features (Including but not limited to):
- Updating safely recreates all the items; leading to *EXPECTED* automatic updating.
- Supports legacy versions **<1.13.2** (and that impacts performance ._.)
- Fast support (Or at least I try my best :D)
- Actually fast and cares about performance
- JVM friendly
- Minimal
- And more.

This library is hosted on Jitpack.
```xml
<repositories>
  <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
  </repository>
  ...
</repositories>

<dependency>
    <groupId>com.github.coderFlameyosFlow</groupId>
    <artifactId>WoodyMenus</artifactId>
    <version>LATEST</version>
</dependency>
```
or gradle (kotlin) alternative:
```kotlin
repositories {
    maven("https://jitpack.io/")
}

dependencies {
    implementation("com.github.coderFlameyosFlow:WoodyMenus:LATEST")
}
```

### CHANGE `LATEST` TO THE LATEST RELEASE.

Example usage:
```java
import org.bukkit.plugin.java.JavaPlugin;

import me.flame.menus.menu.*;

public class MyPlugin extends JavaPlugin {
    private final Menu menu;

    @Override
    public void onEnable() {
        menu = createExampleMenu();
    }

    ...

    private Menu createExampleMenu() {
        Menu exampleMenu = Menus.menu()
              .title("&cExample Menu") // by default colorized
              .rows(6) // if you go above 6 or under 1 you'll get an IllegalArgumentException
              .create();
        exampleMenu = Menus.getFactory().createMenu("&cExample Menu", 6); // alternative
        exampleMenu.setClickAction(event -> { // InventoryClickEvent
            event.setCancelled(true); 
        });

        MenuItem item = ItemBuilder.of(Material.IRON_SWORD).buildItem(event -> {
            getLogger().info("An Iron Sword got Clicked!");
        }); // those args are optional you can execute .buildItem(), or even .build() if you want a normal ItemStack
        exampleMenu.addItem(item);
        exampleMenu.get(1) // Woody's indexes start from 0, this is #2, and yes this is an Optional<MenuItem>
                   .filter(item -> item.getItemStack().getType() == Material.IRON_SWORD)
                   .map(MenuItem::getUniqueId)
                   .ifPresent(uuid -> getLogger().info(uuid.toString())); // man I love Optionals
        // there is also exampleMenu.getItem(1) which is nullable so it's better if you use kotlin but worse if you use java (mostly)
        return exampleMenu;
    }
}
```
