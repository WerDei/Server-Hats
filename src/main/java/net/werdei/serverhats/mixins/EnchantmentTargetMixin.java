package net.werdei.serverhats.mixins;

import net.minecraft.item.Item;
import net.werdei.serverhats.ServerHats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = {
        "net/minecraft/enchantment/EnchantmentTarget$4", // WEARABLE (don't believe the intellij extension, correct order is from bytecode)
        "net/minecraft/enchantment/EnchantmentTarget$6"  // VANISHABLE
})
public class EnchantmentTargetMixin
{
    @Inject(method = "isAcceptableItem", at = @At("RETURN"), cancellable = true)
    public void isAcceptableItem(Item item, CallbackInfoReturnable<Boolean> cir)
    {
        cir.setReturnValue(cir.getReturnValue() || ServerHats.isItemSlotAssigned(item));
    }
}
