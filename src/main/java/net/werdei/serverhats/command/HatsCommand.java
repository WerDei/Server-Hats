package net.werdei.serverhats.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.werdei.serverhats.Config;
import net.werdei.serverhats.ServerHats;

public class HatsCommand
{
    private static RegistryWrapper<Item> itemRegistryWrapper;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess)
    {
        itemRegistryWrapper = commandRegistryAccess.createWrapper(RegistryKeys.ITEM);

        var rootArgument = CommandManager.literal("hats").requires(source ->
                source.hasPermissionLevel(2)
        ).then(CommandManager.literal("reload").executes((context) ->
                reload(context.getSource())
        ));

        for (var configField: Config.class.getDeclaredFields())
        {
            if (configField.getType() == boolean.class)
            {
                var configName = configField.getName();
                var configArgumentBuilder = CommandManager.literal(configName);
                configArgumentBuilder.executes(context ->
                        getSimpleConfigValue(context.getSource(), configName));
                configArgumentBuilder.then(CommandManager.argument("value", BoolArgumentType.bool()).executes((context ->
                        setBooleanConfigValue(context.getSource(), configName, BoolArgumentType.getBool(context, "value")))));
                rootArgument.then(configArgumentBuilder);
            }
        }

        var allowedItemsArgument = CommandManager.literal("allowedItems");
        String argumentName = "item or item tag";
        allowedItemsArgument.then(CommandManager.literal("add").then(CommandManager.argument(argumentName, StringArgumentType.greedyString()).executes((context ->
                allowItems(context.getSource(), StringArgumentType.getString(context, argumentName))))));
        allowedItemsArgument.then(CommandManager.literal("remove").then(CommandManager.argument(argumentName, StringArgumentType.greedyString()).executes((context ->
                disallowItems(context.getSource(), StringArgumentType.getString(context, argumentName))))));
        allowedItemsArgument.executes(context ->
                getArrayConfigValue(context.getSource(), "allowedItems"));
        rootArgument.then(allowedItemsArgument);

        var command = dispatcher.register(rootArgument);
        dispatcher.register(CommandManager.literal("serverhats").redirect(command));
    }

    private static int reload(ServerCommandSource source)
    {
        try
        {
            ServerHats.reloadConfig(
                    s -> source.sendFeedback(() -> Text.literal(s), true),
                    s -> source.sendError(Text.literal(s)));
        }
        catch (Exception e)
        {
            source.sendError(Text.literal("Could not load a configuration file: " + e.getMessage()));
            return 1;
        }
        return 0;
    }

    private static int setBooleanConfigValue(ServerCommandSource source, String name, boolean value)
    {
        try
        {
            var field = Config.class.getField(name);
            if (field.getBoolean(null) == value)
            {
                source.sendError(Text.literal(name + " is already set to " + value));
                return 0;
            }
            field.setBoolean(null, value);
            source.sendFeedback(() -> Text.literal(name + " is now set to " + value), true);

            Config.save();
            return value ? 2 : 1;
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    private static int getSimpleConfigValue(ServerCommandSource source, String name)
    {
        try
        {
            var field = Config.class.getField(name).get(null);
            source.sendFeedback(() -> Text.literal(name + " is currently set to " + field), true);
            return 1;
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    private static int getArrayConfigValue(ServerCommandSource source, String name)
    {
        try
        {
            var field = Config.class.getField(name);
            var array = (Object[]) field.get(null);
            source.sendFeedback(() -> Text.literal(name + " currently contains: "), true);
            StringBuilder contents = new StringBuilder();
            boolean first = true;
            for (var obj : array)
            {
                contents.append(first ? "" : ", ");
                first = false;
                contents.append(obj.toString());
            }
            source.sendFeedback(() -> Text.literal(contents.toString()), true);
            return 1;
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    private static int allowItems(ServerCommandSource source, String string) throws CommandSyntaxException
    {
        if (Config.allowAllItems)
        {
            source.sendError(Text.literal("Cannot modify allowed item list while \"allowAllItems\" is set to \"true\""));
            return 0;
        }

        var itemPredicate = ItemPredicateIdentifier.fromString(string, itemRegistryWrapper);

        if (Config.addAllowedItemId(itemPredicate.id, itemPredicate.isTag))
        {
            ServerHats.recalculateItemLists(
                    s -> source.sendFeedback(() -> Text.literal(s), true),
                    s -> source.sendError(Text.literal(s)));
            source.sendFeedback(() -> Text.literal("Successfully updated allowedItems list"), true);
            return 1;
        }
        else
        {
            source.sendError(Text.literal((itemPredicate.isTag ? "Item tag " : "Item ") + itemPredicate.id + " is already in the allowedItems list"));
            return 0;
        }
    }

    private static int disallowItems(ServerCommandSource source, String string) throws CommandSyntaxException
    {
        if (Config.allowAllItems)
        {
            source.sendError(Text.literal("Cannot modify allowed item list while \"allowAllItems\" is set to \"true\""));
            return 0;
        }

        var itemPredicate = ItemPredicateIdentifier.fromString(string, itemRegistryWrapper);

        if (Config.removeAllowedItemId(itemPredicate.id, itemPredicate.isTag))
        {
            ServerHats.recalculateItemLists(
                    s -> source.sendFeedback(() -> Text.literal(s), true),
                    s -> source.sendError(Text.literal(s)));
            source.sendFeedback(() -> Text.literal("Successfully updated allowedItems list"), true);
            return 1;
        }
        else
        {
            source.sendError(Text.literal((itemPredicate.isTag ? "Item tag " : "Item ") + itemPredicate.id + " is already not in the allowedItems list"));
            return 0;
        }
    }
}
