package net.werdei.serverhats.mixins;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.werdei.serverhats.ServerHats;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = {"net/minecraft/screen/PlayerScreenHandler$1"})
public class PlayerScreenHandlerSlotMixin
{
    @Final
    @Shadow()
    EquipmentSlot field_7834;

    @Inject(method = "canInsert(Lnet/minecraft/item/ItemStack;)Z", at = @At("RETURN"), cancellable = true)
    public void allowItemEquipping(ItemStack stack, CallbackInfoReturnable<Boolean> cir)
    {
        if (field_7834 != EquipmentSlot.HEAD || !ServerHats.isItemAllowed(stack.getItem())) return;
            cir.setReturnValue(true);
    }
}
