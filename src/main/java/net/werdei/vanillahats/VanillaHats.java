package net.werdei.vanillahats;

import com.mojang.brigadier.StringReader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.impl.item.ItemExtensions;
import net.minecraft.item.Item;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class VanillaHats implements ModInitializer
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String LOG_PREFIX = "[VanillaHats]: ";
    private static int modifiedItemCount = 0;

    @Override
    public void onInitialize()
    {
        Config.load();
        Config.save();
    }

    public static void assignEquipmentSlots()
    {
        modifiedItemCount = 0;
        List.of(Config.get.hatItems).forEach( string ->
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

                    tag.values().forEach(VanillaHats::assignSlotTo);
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

        log("Successfully added ability to equip " + modifiedItemCount + " items");
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
