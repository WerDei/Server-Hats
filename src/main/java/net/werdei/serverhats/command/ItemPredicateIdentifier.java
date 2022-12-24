package net.werdei.serverhats.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryWrapper;
import net.minecraft.command.argument.ItemStringReader;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class ItemPredicateIdentifier
{
    public Identifier id;
    public boolean isTag;

    public ItemPredicateIdentifier(Identifier id, boolean isTag)
    {
        this.id = id;
        this.isTag = isTag;
    }

    public static ItemPredicateIdentifier fromString(String string, CommandRegistryWrapper<Item> registryWrapper) throws CommandSyntaxException
    {
        var reader = new StringReader(string);
        var reader2 = new StringReader(string);
        var result = ItemStringReader.itemOrTag(registryWrapper, reader);

        if (result.map(item -> true, tag -> false))
            return new ItemPredicateIdentifier(Identifier.fromCommandInput(reader2), false);
        else
        {
            reader2.expect('#');
            return new ItemPredicateIdentifier(Identifier.fromCommandInput(reader2), true);
        }
    }
}
