package net.werdei.serverhats;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.ItemPredicateArgumentType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("FieldMayBeFinal")
public class ServerHats implements ModInitializer
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String LOG_PREFIX = "[ServerHats]: ";

    private static ArrayList<Predicate<ItemStack>> allowedItemPredicates = null;
    private static HashSet<Item> restrictedItems = null;
    private static boolean itemListsInitialized = false;

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
        if (info == null) info = ServerHats::log;
        if (warning == null) warning = ServerHats::warn;

        Config.load();
        Config.save();

        recalculateItemLists(info, warning);

        String itemCount = Config.allowAllItems ? "all" : Integer.toString(allowedItemPredicates.size());
        info.sendMessage("Successfully added ability to equip " + itemCount + " items");
    }

    public static void recalculateItemLists(OnOutput info, OnOutput warning)
    {
        itemListsInitialized = false;
        allowedItemPredicates = new ArrayList<>();

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
            var itemPredicateArgumentType = new ItemPredicateArgumentType();
            try
            {
                var itemPredicate = itemPredicateArgumentType.parse(reader).create(null);
                addAllowedItemPredicate(itemPredicate);
            }
            catch (CommandSyntaxException e)
            {
                warning.sendMessage("Skipping \"" + string + "\": " + e.getMessage());
            }
        });
        itemListsInitialized = true;
    }

    private static void addAllowedItemPredicate(Predicate<ItemStack> predicate)
    {
        allowedItemPredicates.add(predicate);
    }

    public static boolean isItemAllowed(ItemStack stack)
    {
        if (!itemListsInitialized) return false;
        if (Config.allowAllItems) return !restrictedItems.contains(stack.getItem());
        return allowedItemPredicates.stream().anyMatch(p -> p.test(stack));
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
