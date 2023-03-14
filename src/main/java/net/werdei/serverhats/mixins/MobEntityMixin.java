package net.werdei.serverhats.mixins;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.werdei.serverhats.Config;
import net.werdei.serverhats.ServerHats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MobEntity.class)
public class MobEntityMixin
{
    @ModifyVariable(method = "tryEquip", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/mob/MobEntity;getPreferredEquipmentSlot(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/EquipmentSlot;"))
    private EquipmentSlot allowMobsToEquipHats(EquipmentSlot equipmentSlot, ItemStack stack)
    {
        if (Config.mobsCanEquipHats && ServerHats.isItemAllowed(stack))
            return EquipmentSlot.HEAD;
        else
            return equipmentSlot;
    }
}
