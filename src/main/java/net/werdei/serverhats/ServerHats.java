package net.werdei.serverhats;

import com.mojang.brigadier.StringReader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.werdei.serverhats.utils.Tags;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.List;

@SuppressWarnings("FieldMayBeFinal")
public class ServerHats implements ModInitializer
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String LOG_PREFIX = "[ServerHats]: ";

    private static HashSet<Item> allowedItems = null;

    @Override
    public void onInitialize()
    {
        CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> HatsCommand.register(dispatcher)));
    }

    public static void reloadConfig()
    {
        Config.load();
        Config.save();

        recalculateAllowedItemList();

        String itemCount = Config.allowAllItems ? "all non-wearable" : Integer.toString(allowedItems.size());
        log("Successfully added ability to equip " + itemCount + " items");
    }

    private static void allowItem(Item item)
    {
        allowedItems.add(item);
    }

    public static void recalculateAllowedItemList()
    {
        allowedItems = new HashSet<>();
        List.of(Config.allowedItems).forEach(string ->
        {
            StringReader reader = new StringReader(string);
            try
            {
                if (reader.peek() == '#')
                {
                    reader.expect('#');
                    Identifier id = Identifier.fromCommandInput(reader);
                    Tag<Item> tag = Tags.getItemTag(id);

                    tag.values().forEach(ServerHats::allowItem);
                } else
                {
                    Identifier id = Identifier.fromCommandInput(reader);
                    Item item = Registry.ITEM.getOrEmpty(id).orElseThrow(() ->
                            new RuntimeException("Unknown item identifier '" + id + "'"));

                    allowItem(item);
                }
            }
            catch (Exception e)
            {
                warn("Error modifying \"" + string + "\": " + e.getMessage());
            }
        });
    }

    public static boolean isItemAllowed(Item item)
    {
        if (allowedItems == null) return false;
        if (Config.allowAllItems) return true;
        return allowedItems.contains(item);
    }


    // Logging

    public static void log(Object message)
    {
        LOGGER.info(LOG_PREFIX + message);
    }

    public static void warn(Object message)
    {
        LOGGER.warn(LOG_PREFIX + message);
    }
}
