package net.werdei.serverhats;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.command.argument.ItemPredicateArgumentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.tag.TagKey;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.werdei.serverhats.mixins.ItemPredicateArgumentType.ItemPredicateAccessor;
import net.werdei.serverhats.mixins.ItemPredicateArgumentType.TagPredicateAccessor;

import java.util.function.Predicate;

public class HatsCommand
{
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {
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
        allowedItemsArgument.then(CommandManager.literal("add").then(CommandManager.argument(argumentName, ItemPredicateArgumentType.itemPredicate()).executes((context ->
                allowItems(context.getSource(), ItemPredicateArgumentType.getItemPredicate(context, argumentName))))));
        allowedItemsArgument.then(CommandManager.literal("remove").then(CommandManager.argument(argumentName, ItemPredicateArgumentType.itemPredicate()).executes((context ->
                disallowItems(context.getSource(), ItemPredicateArgumentType.getItemPredicate(context, argumentName))))));
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
                    s -> source.sendFeedback(new LiteralText(s), true),
                    s -> source.sendError(new LiteralText(s)));
        }
        catch (Exception e)
        {
            source.sendError(new LiteralText("Could not load a configuration file: " + e.getMessage()));
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
                source.sendError(new LiteralText(name + " is already set to " + value));
                return 0;
            }
            field.setBoolean(null, value);
            source.sendFeedback(new LiteralText(name + " is now set to " + value), true);

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
            var field = Config.class.getField(name);
            source.sendFeedback(new LiteralText(name + " is currently set to " + field.get(null)), true);
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
            source.sendFeedback(new LiteralText(name + " currently contains: "), true);
            StringBuilder contents = new StringBuilder();
            boolean first = true;
            for (var obj : array)
            {
                contents.append(first ? "" : ", ");
                first = false;
                contents.append(obj.toString());
            }
            source.sendFeedback(new LiteralText(contents.toString()), true);
            return 1;
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    private static int allowItems(ServerCommandSource source, Predicate<ItemStack> itemPredicate)
    {
        if (Config.allowAllItems)
        {
            source.sendError(new LiteralText("Cannot modify allowed item list while \"allowAllItems\" is set to \"true\""));
            return 0;
        }

        var input = extractIdFromPredicate(itemPredicate);

        if (Config.addAllowedItemId(input.id, input.isTag))
        {
            ServerHats.recalculateItemLists(
                    s -> source.sendFeedback(new LiteralText(s), true),
                    s -> source.sendError(new LiteralText(s)));
            source.sendFeedback(new LiteralText("Successfully updated allowedItems list"), true);
            return 1;
        }
        else
        {
            source.sendError(new LiteralText((input.isTag ? "Item tag " : "Item ") + input.id + " is already in the allowedItems list"));
            return 0;
        }
    }

    private static int disallowItems(ServerCommandSource source, Predicate<ItemStack> itemPredicate)
    {
        if (Config.allowAllItems)
        {
            source.sendError(new LiteralText("Cannot modify allowed item list while \"allowAllItems\" is set to \"true\""));
            return 0;
        }

        var input = extractIdFromPredicate(itemPredicate);

        if (Config.removeAllowedItemId(input.id, input.isTag))
        {
            ServerHats.recalculateItemLists(
                    s -> source.sendFeedback(new LiteralText(s), true),
                    s -> source.sendError(new LiteralText(s)));
            source.sendFeedback(new LiteralText("Successfully updated allowedItems list"), true);
            return 1;
        }
        else
        {
            source.sendError(new LiteralText((input.isTag ? "Item tag " : "Item ") + input.id + " is already not in the allowedItems list"));
            return 0;
        }
    }


    private static PredicateExtraction extractIdFromPredicate(Predicate<ItemStack> itemPredicate)
    {
        try
        {
            TagKey<Item> tagKey = ((TagPredicateAccessor) itemPredicate).getTag();
            return new PredicateExtraction(tagKey.id(), true);
        }
        catch (Exception ignored)
        {
            var item = ((ItemPredicateAccessor) itemPredicate).getItem();
            return new PredicateExtraction(Registry.ITEM.getId(item), false);
        }
    }

    private record PredicateExtraction(
        Identifier id,
        boolean isTag
    ){}
}
