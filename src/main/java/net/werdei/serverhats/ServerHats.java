package net.werdei.serverhats;

import com.mojang.brigadier.StringReader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
    private static HashSet<Item> restrictedItems = null;

    @Override
    public void onInitialize()
    {
        CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> HatsCommand.register(dispatcher)));
    }

    public static void reloadConfig()
    {
        reloadConfig(null, null);
    }

    public static void reloadConfig(OnOutput info, OnOutput warning)
    {
        Config.load();
        Config.save();

        if (info == null) info = ServerHats::log;
        if (warning == null) warning = ServerHats::warn;

        recalculateItemLists(info, warning);

        String itemCount = Config.allowAllItems ? "all" : Integer.toString(allowedItems.size());
        log("Successfully added ability to equip " + itemCount + " items");
    }

    public static void recalculateItemLists(OnOutput info, OnOutput warning)
    {
        allowedItems = new HashSet<>();

        if (restrictedItems == null)
        {
            restrictedItems = new HashSet<>();
            Registry.ITEM.stream()
                    .filter(item -> LivingEntity.getPreferredEquipmentSlot(new ItemStack(item)) == EquipmentSlot.HEAD)
                    .forEach(item -> restrictedItems.add(item));
        }

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

                    tag.values().forEach(item ->
                    {
                        try
                        {
                            allowItem(item);
                        }
                        catch (Exception e)
                        {
                            warning.sendMessage("Skipping \"" + string + "\": " + e.getMessage());
                        }
                    });
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
                warning.sendMessage("Skipping \"" + string + "\": " + e.getMessage());
            }
        });
    }

    private static void allowItem(Item item)
    {
        throwExceptionIfItemIsRestricted(item);
        allowedItems.add(item);
    }

    public static boolean isItemAllowed(Item item)
    {
        if (allowedItems == null) return false;
        if (Config.allowAllItems) return !restrictedItems.contains(item);
        return allowedItems.contains(item);
    }

    public static void throwExceptionIfItemIsRestricted(Item item)
    {
        if (restrictedItems != null && restrictedItems.contains(item))
            throw new RuntimeException("The item can already be equipped in a helmet slot!");
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

    public interface OnOutput
    {
        void sendMessage(String message);
    }
}
