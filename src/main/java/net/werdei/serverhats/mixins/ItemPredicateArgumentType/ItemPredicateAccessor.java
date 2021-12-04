package net.werdei.serverhats.mixins.ItemPredicateArgumentType;

import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net/minecraft/command/argument/ItemPredicateArgumentType$ItemPredicate")
public interface ItemPredicateAccessor
{
    @Accessor
    Item getItem();
}
