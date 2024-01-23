# Woody - High-performance, feature-rich (somewhat bloated) no BS menus library
**README WIP** Left to do a few updates.

This is ~~full of stolen code and~~ based off [triumph-gui](https://github.com/TriumphTeam/triumph-gui)

If you need support, Check out the official discord server for help using my plugin/libraries [right here](https://discord.gg/Zj6KBS7UwX)

Here's how to add Woody to your ~~bad~~ code:
```xml
<repositories>
    <repository>
        <groupId>com.github.coderFlameyosFlow.WoodyMenus</groupId>
        <artifactId>core</artifactId>
        <version>2.0.2</version>
    </repository>
</repositories>
```

or the gradle kotlin edition: ~~guess your code isn't that bad after all~~
```kt
repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.coderFlameyosFlow.WoodyMenus:core:2.0.2")
}
```

Adding states to your code to have dynamically changing values in your lore:
```java
MenuItem item = ItemBuilder.of(Material.STONE)
        .lore()
