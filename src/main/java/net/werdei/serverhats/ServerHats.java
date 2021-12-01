package net.werdei.serverhats;

import com.mojang.brigadier.StringReader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.impl.item.ItemExtensions;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.werdei.configloader.ConfigLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.List;

@SuppressWarnings("FieldMayBeFinal")
public class ServerHats implements ModInitializer
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String LOG_PREFIX = "[ServerHats]: ";
    private static final String CONFIG_FILE_NAME = "serverhats.json";

    private static HashSet<Item> allowedItems = null;

    @Override
    public void onInitialize()
    {
        CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> HatsCommand.register(dispatcher)));
    }

    public static void reloadConfig()
    {
        ConfigLoader.load(Config.class, CONFIG_FILE_NAME);
        ConfigLoader.save(Config.class, CONFIG_FILE_NAME);

        if (allowedItems != null)
            allowedItems.forEach(ServerHats::unassignSlotFrom);
        allowedItems = new HashSet<>();

        if (Config.allowAllItems)
            assignSlotsToAllItems();
        else
            assignSlotsToListedItems();

        log("Successfully added ability to equip " + allowedItems.size() + " items");
    }

    private static void assignSlotsToAllItems()
    {
        Registry.ITEM.forEach(item ->
        {
            try
            {
                allowItem(item);
            }
            catch (RuntimeException ignored) {}
        });
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

                    tag.values().forEach(ServerHats::allowItem);
                }
                else
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


    public static void allowItem(Item item)
    {
        if (allowedItems.contains(item))
            throw new RuntimeException("Item " + item.getName() + " is already allowed.");
        assignSlotTo(item);
        allowedItems.add(item);
    }

    public static void disallowItem(Item item)
    {
        if (!allowedItems.contains(item))
            throw new RuntimeException("Item " + item.getName() + " is already disallowed.");
        unassignSlotFrom(item);
        allowedItems.remove(item);
    }

    public static boolean isItemAllowed(Item item)
    {
        if (allowedItems == null) return false;
        return allowedItems.contains(item);
    }


    private static void assignSlotTo(Item item)
    {
        EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(new ItemStack(item));
        if (equipmentSlot != EquipmentSlot.MAINHAND)
            throw new RuntimeException("Item already assigned to equipment slot \"" + equipmentSlot.getName() + "\"");

        ((ItemExtensions) item).fabric_setEquipmentSlotProvider(HeadEquipmentSlotProvider.PROVIDER);
    }

    private static void unassignSlotFrom(Item item)
    {
        ((ItemExtensions) item).fabric_setEquipmentSlotProvider(null);
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
