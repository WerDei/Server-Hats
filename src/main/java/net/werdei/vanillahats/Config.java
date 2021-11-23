package net.werdei.vanillahats;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;

public class Config
{
    private static File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "vanillahats.json");
    public static Config get;

    public String[] hatItems = new String[]{
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


            "azalea",
            "flowering_azalea",
            "scaffolding",
            "big_dripleaf",
            "slime_block",
            "honey_block",
            "composter",
            "glass",
            "tinted_glass",
    };

    public static void load()
    {
        Gson gson = new Gson();
        Config loaded = null;
        try
        {
            loaded = gson.fromJson(new FileReader(file), Config.class);
        }
        catch (FileNotFoundException ignored) {}

        get = new Config();
        if (loaded == null) return;
        if (loaded.hatItems != null)
            get.hatItems = loaded.hatItems;
    }

    public static void save()
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try
        {
            FileWriter writer = new FileWriter(file);
            writer.write(gson.toJson(get));
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
