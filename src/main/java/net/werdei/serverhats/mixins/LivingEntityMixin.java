package net.werdei.serverhats.mixins;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntity.class)
public class LivingEntityMixin
{
//    @Inject(method = "getPreferredEquipmentSlot", at = @At("RETURN"), cancellable = true)
//    private static void substituteEquipmentSlot(ItemStack stack, CallbackInfoReturnable<EquipmentSlot> cir)
//    {
//        if (cir.getReturnValue() == EquipmentSlot.MAINHAND && ServerHats.isItemAllowed(stack))
//            cir.setReturnValue(EquipmentSlot.HEAD);
//    }
}
