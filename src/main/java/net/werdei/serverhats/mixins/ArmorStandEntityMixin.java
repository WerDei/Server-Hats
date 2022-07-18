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
    protected abstract EquipmentSlot getSlotFromPosition(Vec3d hitPos);

    @Shadow
    protected abstract boolean isSlotDisabled(EquipmentSlot slot);

    @Shadow
    public abstract boolean isSmall();


    @ModifyVariable(method = "interactAt", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/mob/MobEntity;getPreferredEquipmentSlot(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/EquipmentSlot;"))
    public EquipmentSlot undoSubstitution(EquipmentSlot equipmentSlot, PlayerEntity player, Vec3d hitPos, Hand hand)
    {
        // Cancel the slot substitution when not looking at an armor stand head
        if (equipmentSlot == EquipmentSlot.HEAD && ServerHats.isItemAllowed(player.getStackInHand(hand))
                && !isLookingAtAHead(hitPos) && !isSlotDisabled(EquipmentSlot.MAINHAND))
            return EquipmentSlot.MAINHAND;
        else
            return equipmentSlot;
    }

    private boolean isLookingAtAHead(Vec3d hitPos)
    {
        var y = isSmall() ? hitPos.y * 2 : hitPos.y;
        return y > 1.6;
    }
}
