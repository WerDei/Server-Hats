package net.werdei.serverhats.mixins;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.werdei.serverhats.ServerHats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ArmorStandEntity.class)
public abstract class ArmorStandEntityMixin
{
    @Shadow
    public abstract boolean isSmall();


    @ModifyVariable(method = "interactAt", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/mob/MobEntity;getPreferredEquipmentSlot(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/EquipmentSlot;"))
    public EquipmentSlot equipHatsToArmorStands(EquipmentSlot equipmentSlot, PlayerEntity player, Vec3d hitPos, Hand hand)
    {
        // Equip an armor stand with a hat when looking at its head
        if (ServerHats.isItemAllowed(player.getStackInHand(hand)) && isLookingAtAHead(hitPos))
            return EquipmentSlot.HEAD;
        else
            return equipmentSlot;
    }

    private boolean isLookingAtAHead(Vec3d hitPos)
    {
        var y = isSmall() ? hitPos.y * 2 : hitPos.y;
        return y > 1.6;
    }
}
