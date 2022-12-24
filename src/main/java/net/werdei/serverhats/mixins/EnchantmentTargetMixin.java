package net.werdei.serverhats.mixins;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.werdei.serverhats.Config;
import net.werdei.serverhats.ServerHats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = {
        "net/minecraft/enchantment/EnchantmentTarget$4",  // WEARABLE (don't believe the intellij extension, correct order is from bytecode)
        "net/minecraft/enchantment/EnchantmentTarget$6",  // VANISHABLE
})
public class EnchantmentTargetMixin
{
    @Inject(method = "isAcceptableItem(Lnet/minecraft/item/Item;)Z", at = @At("RETURN"), cancellable = true)
    public void isAcceptableItem(Item item, CallbackInfoReturnable<Boolean> cir)
    {
        if (!Config.enchanting) return;
        cir.setReturnValue(cir.getReturnValue() || ServerHats.isItemAllowed(new ItemStack(item)));
    }
}
