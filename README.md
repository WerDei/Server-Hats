# Server Hats
Server-side Fabric mod that allows players equip items as hats.  
To equip an allowed item, simply put it in the helmet slot - no commands necessary!  
Requires Fabric API  

## Configuration
After loading the mod once, open up the `serverhats.json` file in the `config\` folder. You will see a few parameters:

### "allowAllItems"
A boolean value. When set to `true`, allows any item to be equipped to a head slot.

By default, it is set to `false`.  

### "allowedItems"
A list of items that are allowed to be equipped in a head slot. Ignored when "allowAllItems" is set to `true`.  
Can contain any item IDs and item tags.

By default, it includes some items that have special rendering rules in vanilla Minecraft:  
```
"#banners",
"feather",
"end_rod",
"lightning_rod",
"spyglass",

"amethyst_cluster",
"large_amethyst_bud",
"medium_amethyst_bud",
"small_amethyst_bud",

"acacia_fence_gate",
"birch_fence_gate",
"dark_oak_fence_gate",
"jungle_fence_gate",
"oak_fence_gate",
"spruce_fence_gate",
"crimson_fence_gate",
"warped_fence_gate",
```

And some other ones I thought looked cool\funny:
```
"azalea",
"flowering_azalea",
"scaffolding",
"big_dripleaf",
"slime_block",
"honey_block",
"composter",
"glass",
"tinted_glass",
```

## Known issues
* While in multiplayer, players in creative mode cannot equip items to a helmet slot
