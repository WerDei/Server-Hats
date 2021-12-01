package net.werdei.serverhats;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

public class HatsCommand
{
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        var command = dispatcher.register(CommandManager.literal("hats").requires(source ->
                source.hasPermissionLevel(2)
        ).then(CommandManager.literal("reload").executes((context) ->
                reload(context.getSource())
        )));
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
}
