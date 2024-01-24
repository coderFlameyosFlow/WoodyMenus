## Woody - High-performance, feature-rich no BS menus library
[![](https://dcbadge.vercel.app/api/server/Zj6KBS7UwX)](https://discord.gg/Zj6KBS7UwX)

This is ~~full of stolen code~~ based off [triumph-gui](https://github.com/TriumphTeam/triumph-gui)

## Why Woody?
- Woody is really easy

  Woody is very easy to get started by looking at a few java documentation.
  Even easier when you come from triumph-gui because it's somewhat similar to triumph-gui.
  Just create the menu via `Menu.create` or use `Menu.builder` to build a Menu.
  
  Creating a paginated menu is JUST as easy.

- Woody is very Flexible

  With all these actions for menus, default click actions, outside, top, bottom,
  actions before animations, actions after page change, interfaces, etc, you just CAN'T resist the incredible flexibility of Woody.

  and not just the actions and some of common interfaces.
  
- Woody is powerful and fast
  
  Woody has an amazing API for you to work with and powerful features designed to make your coding journey fun!
  Such as:
  - Dynamically changing States to add in **MenuItem** lore.

    A straight forward example:
    ```java
    MenuItem item = ...;
    // Let's declare the lore of "item" to be this:
    /*
     * Hello World!
     * The coolest player currently alive is FlameyosFlow out of %players% players in the server
     */
    item.addState(State.of("%players%", () -> Bukkit.getOnlinePlayers().size(), item));

    Menu menu = ...;
    menu.setUpdateStatesOnUpdate(true); // this exists for performance reasons
    ```
  - Menu#getFiller and PaginatedMenu#getPageDecoration to make decorating menus easy in a fast manner.

    For example: ~~This will make your code better since your code sux anyways~~
    ```java
    Menu menu = ...; // we'll call this "present" Menu object "menu"
    Filler filler = menu.getFiller();
    filler.fillBorder(Material.STONE); // most recommended when working with glass panes on newer versions
    filler.fillBorder(new ItemStack(Material.STONE)); // most recommended when working with glass panes on legacy
    filler.fillBorder(ItemBuilder.of(Material.STONE).buildItem()); // most recommended for ANYTHING else

    // More exist, such as Filler#fill to fill every slot where [slot != null]
    // Filler#fillRow, Filler#fillArea, etc.

    // For PageDecoration just use PaginatedMenu#getPageDecoration and it should be looking just the same.
    ```
  - Easy **MenuItem** editing via `MenuItem#editor` like no other library.

    A straight forward example: ~~if you use an item meta no wonder everyone says your code sux fr~~
    ```java
    MenuItem item = ...; // an already existing MenuItem needs to be present, let's call this "item"
    item.editor()
        .setLore("I am an edited lore", "I now exist! :D")
        .setName("Wussuh I'm edited")
        .enchant(Enchantment.DAMAGE_ALL)
        .setCustomModelData(53) // completely random model data
        .done(); // no need to update the item on any menu, it will automatically change :D
    ```
  - Amazing native/legacy **adventure** support. (I cutely asked [IF's](https://github.com/stefvanschie/IF) owner for this :D)
    
    To use Adventure just use `TextHolder` or `CompHolder` parameters instead of `String`
  - Easy layout building using MenuLayoutBuilder
    
    To build using a string array of layouts here's how you do it:
    ```java
    Map<Character, MenuItem> items = Map.of(
        'X', ItemBuilder.of(Material.STONE).buildItem(),
        'Y', ItemBuilder.of(Material.CARROT).buildItem((slot, event) -> Bukkit.getLogger().info("A carrot got clicked!")
    );

    Menu menu = MenuLayoutBuilder.bind(items).pattern("XXXXXXXXX", "XYY   YYX", "XXXXXXXXX").createMenu("&4Sick Menu Building", EnumSet.allOf(Modifier.class));
   ```
   ```

  - Powerful Animating API for your menus (Inspired by [this pull request](https://github.com/TriumphTeam/triumph-gui/pull/49) from TWO YEARS AGO for triumph-gui)
    
    An example of a few frames animating every 10 ticks (500ms)
    ```java
    Menu menu = ...;
    Animation animation = Animation.builder(menu)
                   .frames(
                       Frame.builder(menu).addItems(
                           ItemBuilder.of(Material.STONE).buildItem(),
                           ItemBuilder.of(Material.CARROT).buildItem(),
                           ...
                        ).build(),
                        Frame.builder(menu).addItems(
                           ItemBuilder.of(Material.CAKE).buildItem(),
                           ItemBuilder.of(Material.GRASS_BLOCK).buildItem(),
                           ...
                        ).build(),
                        ...
                   )
                   .delay(10)
                   .type(Animation.Type.REPEATED) // happens infinitely until the last viewer of the menu closes the menu
                   .build();
    menu.addAnimation(animation);
    // will be animated autonomously on the first open of a menu after 0 viewers
    ```
  - SuperB flexible MenuIterator (by Mqzn originally :D)
    A straight forward example:
    ```java
    Menu menu = ...;
    for (MenuItem item : menu) {
        // normal I guess 🤷‍♂️
    }

    // the power of this iterator
    MenuIterator iterator = menu.iterator(1, 6); // will start from row 1, and column 6
    // work with the Iterator object

    // or some REAL power
    MenuIterator iterator = menu.iterator(IterationDirection.VERTICAL);
    // work with the Iterator object
    // now the direction of this iterator will go vertically like (0, 9, 18, 27, 36, 45, 1, 10, 19, etc.) instead of
    // horizontally like (0, 1, 2, 3, 4, 5, etc.)
    
    or an even more custom one:
    MenuIterator iterator = menu.iterator(IterationDirection.RIGHT_DOWNWARDS_ONLY);
    // work with the Iterator object
    // now the direction will go from 0 to 50 like this: (refer to the X's)
    /*
    XYYYYYYYY
    YXYYYYYYY
    YYXYYYYYY
    YYYXYYYYY
    YYYYXYYYY
    YYYYYXYYY
    */
    
    // these iterations are not dependant on the size of the menu or the starting row and column, these directions
    // work for ALL cases as long as you don't mess up the starting row and/or column.
    ```

- And **SO MUCH MORE!**

There are just SO many stuff about woody you can't resist!

## Getting Started
Obviously you can't **resist all these cool features** and both of us know we're gonna use half to all of these features!

Here's how to add Woody to your ~~bad~~ code:
```xml
<repositories>
    <repository>
        <groupId>com.github.coderFlameyosFlow.WoodyMenus</groupId>
        <artifactId>core</artifactId>
        <version>2.0.6</version>
    </repository>
</repositories>
```

or the gradle kotlin edition: (~~guess your code isn't that bad after all~~)
```kt
repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.coderFlameyosFlow.WoodyMenus:core:2.0.6")
}
```

You don't need to setup anything in your onEnable ~~(since 2.0.0)~~ or anything, just code right away!
 
And now you can start writing good code and make your Family, Friends and Linus Torvalds proud!
