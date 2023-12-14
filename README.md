<!--suppress HtmlDeprecatedTag, XmlDeprecatedElement -->
<center><img alt="screenshot" src="https://cdn.modrinth.com/data/VD1aynYU/images/7feef9e9ba223616e0462725ea7f5d860aea946f.png" /></center>

<center>
Tabs to swap between nearby screens like chests, crafting stations, and even entities.<br/>
Requires <a href="https://modrinth.com/mod/connector">Connector</a> and <a href="https://modrinth.com/mod/forgified-fabric-api">FFAPI</a> on forge.<br/>
<i>Colloquially: Inventory Tabs 4</i><br/>
For creative mode, try <a href="https://modrinth.com/mod/sidekick">Sidekick</a>!
</center>

---

While a supported screen is open, a paginated row of tabs is shown representing nearby interactive blocks and entities.

By clicking on a tab, you'll instantly swap to the associated screen, without the walking and clicking.

Operates entirely on the client side, with only small tweaks when installed on-server.

### Basic Features

 - Swap between crafting stations, storage, and your inventory without leaving their screens.
 - Supports tabs for all vanilla blocks and entities, with configuration available for modded blocks/entities/items. 
 - Tabs spills over onto multiple pages to fit full storage rooms worth of chests.
 - Chest tabs can be labelled using signs, item frames, or (while server modded) anvil-renaming for organisation.
 - Stacks held on your cursor carry over to the next screen as long as your inventory isn't full.
 - Keyboard navigation (default `TAB` and `SHIFT+TAB`) can be used to swap between tabs.
 - Chest screens are normalized to be aligned with other screens, instead of being 1 pixel lower (configurable).
 - Compact design supports advanced recipe viewers like [EMI](https://modrinth.com/mod/emi). `Left (Compressed)` effects are recommended.
 - Allows a few non-vanilla actions for convenience - be wary of this for strict anticheat servers!
   - Allows accessing your full regular inventory while riding a horse or chest boat.
   - Allows accessing other vehicle inventories while riding a vehicle (usually requires sneaking).

<center><img alt="EMI Preview" src="https://cdn.modrinth.com/data/VD1aynYU/images/7356435a874c5f5c587b59f4b71461da2e997df1.png" /></center>

### Modpack Configuration

Inventory Tabs 4 is designed from the ground up to be friendlier to modpacks.<br/>
It's configured via `config/inventory_tabs.toml`, which includes comments providing extra context - like what each tab provider does. 

You can enable the `configLogging` option to log  helpful information for setting up the mod for your modpack when loading into a world.

If tabs are appearing on a screen they don't fit well with, the screen can be blacklisted:

```
[screenOverrides]
	"fwaystones:waystone" = false
```

If tabs are being made for an inappropriate block, you can manually disable their tab provider:

```
[blockProviderOverrides]
	"cool_mod:incompatible_block" = ""
```

Or manually override it to a relevant one:

```
[blockProviderOverrides]
	"#cool_mod:crafting_stations" = "inventory_tabs:block_unique"
	"cool_mod:single_chest" = "inventory_tabs:block_simple"
	"cool_mod:doubleable_chest" = "inventory_tabs:block_chest"
```

Make sure you test your configuration! These options are _not_ intended to be crash-proof.

Overrides also work for entities and items, via `[entityProviderOverrides]` and `[itemProviderOverrides]` respectively.

If too many inappropriate blocks are being matched, you may want to disable the default matching logic for a provider entirely:

```
[registryProviderDefaults]
	"inventory_tabs:block_simple" = false
```

The `block_simple` provider uses a blacklist instead of a whitelist, so it generates a lot of false-positive tabs. It's enabled by default to help with finding good/bad tabs - but if you're making a modpack, you'll probably turn it off!

### Mod Architecture

Each visible tab on the screen is a [Tab](https://github.com/sisby-folk/inventory-tabs/blob/1.20/src/main/java/folk/sisby/inventory_tabs/tabs/Tab.java), which is responsible for rendering itself, knowing how to be opened, and knowing when it should be removed from the list of tabs (e.g. when no longer in range).

These tabs are held by the [Tab Manager](https://github.com/sisby-folk/inventory-tabs/blob/1.20/src/main/java/folk/sisby/inventory_tabs/TabManager.java), which is effectively the "Tab Screen". It adds onto regular screen methods to do its own rendering, ticking, and mouse click handling. It's also responsible for checking if any new tabs should be added.

Tabs are added via registered [Tab Providers](https://github.com/sisby-folk/inventory-tabs/blob/1.20/src/main/java/folk/sisby/inventory_tabs/TabProviders.java). Basic providers like [Vehicle Inventory](https://github.com/sisby-folk/inventory-tabs/blob/1.20/src/main/java/folk/sisby/inventory_tabs/providers/VehicleInventoryTabProvider.java) check simple conditions and add a special tab. The more advanced [Registry Providers](https://github.com/sisby-folk/inventory-tabs/blob/1.20/src/main/java/folk/sisby/inventory_tabs/providers/RegistryTabProvider.java) are designed to be assigned a specific set of [blocks](https://github.com/sisby-folk/inventory-tabs/blob/1.20/src/main/java/folk/sisby/inventory_tabs/providers/BlockTabProvider.java), [entity types](https://github.com/sisby-folk/inventory-tabs/blob/1.20/src/main/java/folk/sisby/inventory_tabs/providers/EntityTabProvider.java), or [items](https://github.com/sisby-folk/inventory-tabs/blob/1.20/src/main/java/folk/sisby/inventory_tabs/providers/ItemTabProvider.java) that they're responsible for providing tabs for - which is frozen at reload time - and then searching for those around the player every tick. Using these generics, simpler concrete providers are made ([Ender Chests](https://github.com/sisby-folk/inventory-tabs/blob/1.20/src/main/java/folk/sisby/inventory_tabs/providers/EnderChestTabProvider.java), [Unique Block](https://github.com/sisby-folk/inventory-tabs/blob/1.20/src/main/java/folk/sisby/inventory_tabs/providers/UniqueBlockTabProvider.java).)


### Addons

```
repositories {
	maven { url "https://repo.sleeping.town/" }
}
dependencies {
   modImplementation "folk.sisby:inventory-tabs:1.0.3"
}
```

Addons for Inventory Tabs 4 can add new tab types and tab providers, and add advanced matching logic to current providers, provide custom tab layouts for their screens (including inverted tabs), and signal when specific tabs should be marked as open.

For a practical example, check out [Portable Crafting](https://github.com/sisby-folk/portable-crafting)!

---

### Afterword

All mods are built on the work of many others - We're the fourth set of maintainers of this mod!<br/>
This version is a partial rewrite, following the work of CakeWhip, LiamMCW, and Andrew6rant.

This mod is included in [Tinkerer's Quilt](https://modrinth.com/modpack/tinkerers-quilt) - our modpack about rediscovering vanilla.

We're open to suggestions for how to implement stuff better - if you see something wonky and have an idea - let us know.
