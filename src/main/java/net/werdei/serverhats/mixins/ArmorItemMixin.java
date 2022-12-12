package net.werdei.serverhats.mixins;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.werdei.serverhats.Config;
import net.werdei.serverhats.ServerHats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ArmorItem.class)
public class ArmorItemMixin
{
    @ModifyVariable(method = "dispenseArmor", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/mob/MobEntity;getPreferredEquipmentSlot(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/EquipmentSlot;"))
    private static EquipmentSlot allowDispenserEquipping(EquipmentSlot equipmentSlot, BlockPointer pointer, ItemStack armor)
    {
        if (Config.mobsCanEquipHats && ServerHats.isItemAllowed(armor))
            return EquipmentSlot.HEAD;
        else
            return equipmentSlot;
    }
}
