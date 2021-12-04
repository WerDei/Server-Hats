package net.werdei.serverhats.mixins.ItemPredicateArgumentType;

import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net/minecraft/command/argument/ItemPredicateArgumentType$TagPredicate")
public interface TagPredicateAccessor
{
    @Accessor
    Tag<Item> getTag();
}
