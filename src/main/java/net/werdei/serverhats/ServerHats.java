package net.werdei.serverhats;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryWrapper;
import net.minecraft.command.argument.ItemStringReader;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import net.werdei.serverhats.command.HatsCommand;
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
    private static boolean itemListsInitialized = false;
    private static CommandRegistryWrapper<Item> registryWrapper;

    @Override
    public void onInitialize()
    {

        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) ->
        {
            HatsCommand.register(dispatcher, registryAccess);
            registryWrapper = registryAccess.createWrapper(Registry.ITEM_KEY);
        }));
    }

    public static void reloadConfig()
    {
        reloadConfig(null, null);
    }

    public static void reloadConfig(OnOutput info, OnOutput warning)
    {
        if (registryWrapper == null)
            ServerHats.warn("ORDER LUL");

        if (info == null) info = ServerHats::log;
        if (warning == null) warning = ServerHats::warn;

        Config.load();
        Config.save();

        recalculateItemLists(info, warning);

        String itemCount = Config.allowAllItems ? "all" : Integer.toString(allowedItems.size());
        info.sendMessage("Successfully added ability to equip " + itemCount + " items");
    }

    public static void recalculateItemLists(OnOutput info, OnOutput warning)
    {
        itemListsInitialized = false;
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
            try
            {
                var either = ItemStringReader.itemOrTag(registryWrapper, new StringReader(string));

                either.ifLeft(itemResult -> addAllowedItem(itemResult.item().value(), warning));
                either.ifRight(tagResult -> tagResult.tag().forEach(item -> addAllowedItem(item.value(), warning)));
            }
            catch (CommandSyntaxException e)
            {
                warning.sendMessage("Skipping \"" + string + "\": " + e.getMessage());
            }
        });
        itemListsInitialized = true;
    }

    private static void addAllowedItem(Item item, OnOutput warning)
    {
        if (restrictedItems.contains(item))
            warning.sendMessage("Skipping \"" + item.getName() + "\": The item can already be equipped in a helmet slot");
        else
            allowedItems.add(item);
    }

    public static boolean isItemAllowed(ItemStack stack)
    {
        if (!itemListsInitialized) return false;
        if (Config.allowAllItems) return !restrictedItems.contains(stack.getItem());
        return allowedItems.contains(stack.getItem());
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
