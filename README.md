# Vanilla-Hats
Server-side Fabric mod that allows players equip items as hats.

To equip an allowed item, simply put it in the helmet slot - no commands necessary!

### Configuration
After loading the mod, open up the `vanillahats.json` file in the `config\` folder. You will see something like this:

```
{
  "hatItems": [
    "#banners",
    "#fence_gates",
    "feather",
    ...
    ...
    ...
    "tinted_glass"
  ]
}
```

You can add any item IDs and item tags to this list, and after a restart players will be able to euip these items in a head slot.

By default it includes items that have special rendering rules in vanilla Minecraft:
```
"#banners",
"#fence_gates",

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

Feel free to change it up to your own liking!

### Known issues

* While in multiplayer, players in creative mode cannot equip items to a helmet slot
* When nothing is equpped in the head slot, shift-clicking an item equips it. That might get annoying if you're trying to keep your head empty
