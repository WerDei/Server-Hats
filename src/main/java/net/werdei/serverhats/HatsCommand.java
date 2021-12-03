package net.werdei.serverhats;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

public class HatsCommand
{
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        var rootArgument = CommandManager.literal("hats").requires(source ->
                source.hasPermissionLevel(2)
        ).then(CommandManager.literal("reload").executes((context) ->
                reload(context.getSource())
        ));

        var setArgument = CommandManager.literal("set");
        for (var configField: Config.class.getDeclaredFields())
        {
            if (configField.getType() == boolean.class)
            {
                var configName = configField.getName();
                setArgument.then(CommandManager.literal(configName).then(CommandManager.argument("value", BoolArgumentType.bool()).executes((context ->
                        setConfigValue(context.getSource(), configName, BoolArgumentType.getBool(context, "value"))))));
            }
        }
        rootArgument.then(setArgument);

        var command = dispatcher.register(rootArgument);
        dispatcher.register(CommandManager.literal("serverhats").redirect(command));
    }

    private static int reload(ServerCommandSource source)
    {
        try
        {
            ServerHats.reloadConfig();
        }
        catch (Exception e)
        {
            source.sendError(new LiteralText("Could not load a configuration file: " + e.getMessage()));
            return 1;
        }
        source.sendFeedback(new LiteralText("Configuration file reloaded successfully"), true);
        return 0;
    }

    private static int setConfigValue(ServerCommandSource source, String name, boolean value)
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
}
