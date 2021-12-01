package net.werdei.serverhats;

import com.mojang.brigadier.StringReader;
import net.fabricmc.api.ModInitializer;
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

    private static HashSet<Item> assignedItems = null;

    @Override
    public void onInitialize()
    {
        ConfigLoader.load(Config.class, CONFIG_FILE_NAME);
        ConfigLoader.save(Config.class, CONFIG_FILE_NAME);
    }

    public static void assignEquipmentSlots()
    {
        if (assignedItems != null) return;

        assignedItems = new HashSet<>();
        if (Config.allowAllItems)
            assignSlotsToAllItems();
        else
            assignSlotsToListedItems();
        log("Successfully added ability to equip " + assignedItems.size() + " items");
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

    private static void allowItem(Item item)
    {
        if (assignedItems.contains(item))
            throw new RuntimeException("Item " + item.getName() + " is already allowed.");
        EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(new ItemStack(item));
        if (equipmentSlot != EquipmentSlot.MAINHAND)
            throw new RuntimeException("Item already assigned to equipment slot \"" + equipmentSlot.getName() + "\"");

        ((ItemExtensions) item).fabric_setEquipmentSlotProvider(HeadEquipmentSlotProvider.PROVIDER);
        assignedItems.add(item);
    }

    private static void disallowItem(Item item)
    {
        if (!assignedItems.contains(item))
            throw new RuntimeException("Item " + item.getName() + " is already not allowed.");

        ((ItemExtensions) item).fabric_setEquipmentSlotProvider(null);
        assignedItems.remove(item);
    }

    public static boolean isItemAllowed(Item item)
    {
        if (assignedItems == null) return false;
        return assignedItems.contains(item);
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
