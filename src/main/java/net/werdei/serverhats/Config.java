package net.werdei.serverhats;

import net.minecraft.util.Identifier;
import net.werdei.configloader.ConfigLoader;

import java.util.ArrayList;
import java.util.List;

public class Config
{
    public static boolean shiftClickEquipping = false;

    public static boolean dispenserEquipping = true;

    public static boolean enchanting = true;

    public static boolean allowAllItems = false;

    public static String[] allowedItems = new String[]{
            "#banners",
            "#beds",
            "feather",
            "end_rod",
            "lightning_rod",
            "spyglass",
            "cod",
            "lead",
            "bone",
            "conduit",

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


            "azalea",
            "flowering_azalea",
            "scaffolding",
            "big_dripleaf",
            "slime_block",
            "honey_block",
            "composter",

            "glass",
            "white_stained_glass",
            "orange_stained_glass",
            "magenta_stained_glass",
            "light_blue_stained_glass",
            "yellow_stained_glass",
            "lime_stained_glass",
            "pink_stained_glass",
            "gray_stained_glass",
            "light_gray_stained_glass",
            "cyan_stained_glass",
            "purple_stained_glass",
            "blue_stained_glass",
            "brown_stained_glass",
            "green_stained_glass",
            "red_stained_glass",
            "black_stained_glass",
            "tinted_glass",
    };


    // Saving and loading

    public static void load()
    {
        ConfigLoader.load(Config.class, getFileName());
    }

    public static void save()
    {
        ConfigLoader.save(Config.class, getFileName());
    }

    private static String getFileName()
    {
        return "serverhats.json";
    }

    // Utilities

    public static boolean addAllowedItemId(Identifier id, boolean isTag)
    {
        var allowedIds = new ArrayList<>(List.of(allowedItems));
        var idStrings = getApplicableIdStrings(id, isTag);

        for (var idString: idStrings)
            if (allowedIds.contains(idString))
                return false;

        allowedIds.add(idStrings.get(idStrings.size()-1));
        allowedItems = allowedIds.toArray(new String[0]);
        save();
        return true;
    }

    public static boolean removeAllowedItemId(Identifier id, boolean isTag)
    {
        var allowedIds = new ArrayList<>(List.of(allowedItems));
        int allowedIdCountBefore = allowedIds.size();

        var idStrings = getApplicableIdStrings(id, isTag);
        idStrings.forEach(allowedIds::remove);

        if (allowedIdCountBefore == allowedIds.size())
            return false;

        allowedItems = allowedIds.toArray(new String[0]);
        save();
        return true;
    }

    private static List<String> getApplicableIdStrings(Identifier id, boolean isTag)
    {
        var out = new ArrayList<String>();
        out.add(id.toString());
        if (id.getNamespace().equals("minecraft"))
            out.add(id.toString().split(":")[1]);
        return out.stream().map(s -> (isTag ? "#" : "") + s).toList();
    }
}
