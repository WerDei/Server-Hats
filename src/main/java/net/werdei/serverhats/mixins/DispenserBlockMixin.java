package net.werdei.serverhats.mixins;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.werdei.serverhats.Config;
import net.werdei.serverhats.ServerHats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin
{
    @Inject(method = "getBehaviorForItem", at = @At("RETURN"), cancellable = true)
    protected void dispenserEquip(ItemStack stack, CallbackInfoReturnable<DispenserBehavior> cir)
    {
        if (Config.dispenserEquipping && ServerHats.isItemAllowed(stack.getItem()))
            cir.setReturnValue(ArmorItem.DISPENSER_BEHAVIOR);
    }
}
