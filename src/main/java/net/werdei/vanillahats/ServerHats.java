package net.werdei.vanillahats;

import com.mojang.brigadier.StringReader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.impl.item.ItemExtensions;
import net.minecraft.item.Item;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.werdei.config.ConfigLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ServerHats implements ModInitializer
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String LOG_PREFIX = "[ServerHats]: ";
    private static final String CONFIG_FILE_NAME = "serverhats.json";
    private static int modifiedItemCount = -1;

    @Override
    public void onInitialize()
    {
        ConfigLoader.load(Config.class, CONFIG_FILE_NAME);
        ConfigLoader.save(Config.class, CONFIG_FILE_NAME);
    }

    public static void assignEquipmentSlots()
    {
        if (modifiedItemCount >= 0) return;

        modifiedItemCount = 0;
        if (Config.allowAllItems)
            assignSlotsToAllItems();
        else
            assignSlotsToListedItems();
        log("Successfully added ability to equip " + modifiedItemCount + " items");
    }

    private static void assignSlotsToAllItems()
    {
        Registry.ITEM.forEach(ServerHats::assignSlotTo);
    }

    private static void assignSlotsToListedItems()
    {
        List.of(Config.allowedItems).forEach(string ->
        {
            StringReader reader = new StringReader(string);
            try
            {
                if (reader.peek() == '#')
                {
                    reader.expect('#');
                    Identifier id = Identifier.fromCommandInput(reader);
                    Tag<Item> tag = ServerTagManagerHolder.getTagManager().getTag(Registry.ITEM_KEY, id, (identifier) ->
                            new RuntimeException("Unknown item tag '" + identifier + "'"));

                    tag.values().forEach(ServerHats::assignSlotTo);
                }
                else
                {
                    Identifier id = Identifier.fromCommandInput(reader);
                    Item item = Registry.ITEM.getOrEmpty(id).orElseThrow(() ->
                            new RuntimeException("Unknown item identifier '" + id + "'"));

                    assignSlotTo(item);
                }
            }
            catch (Exception e)
            {
                warn("Error modifying \"" + string + "\": " + e.getMessage());
            }
        });
    }

    private static void assignSlotTo(Item item)
    {
        ((ItemExtensions) item).fabric_setEquipmentSlotProvider(HeadEquipmentSlotProvider.PROVIDER);
        modifiedItemCount++;
    }

    public static void log(Object message)
    {
        LOGGER.info(LOG_PREFIX + message);
    }

    public static void warn(Object message)
    {
        LOGGER.warn(LOG_PREFIX + message);
    }
}
