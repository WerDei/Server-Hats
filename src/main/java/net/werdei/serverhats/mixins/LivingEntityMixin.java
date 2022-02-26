package net.werdei.serverhats.mixins;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.werdei.serverhats.ServerHats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin
{
    @Inject(method = "getPreferredEquipmentSlot", at = @At("RETURN"), cancellable = true)
    private static void substituteEquipmentSlot(ItemStack stack, CallbackInfoReturnable<EquipmentSlot> cir)
    {
        if (cir.getReturnValue() == EquipmentSlot.MAINHAND && ServerHats.isItemAllowed(stack))
            cir.setReturnValue(EquipmentSlot.HEAD);
    }
}
