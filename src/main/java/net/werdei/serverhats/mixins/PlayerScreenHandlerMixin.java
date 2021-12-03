package net.werdei.serverhats.mixins;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.werdei.serverhats.Config;
import net.werdei.serverhats.ServerHats;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerScreenHandler.class)
public abstract class PlayerScreenHandlerMixin extends ScreenHandler
{
    protected PlayerScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId)
    {
        super(type, syncId);
    }

    @ModifyVariable(method = "transferSlot",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/entity/mob/MobEntity;getPreferredEquipmentSlot(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/EquipmentSlot;"))
    public EquipmentSlot preventQuickTransferEquip(EquipmentSlot equipmentSlot, PlayerEntity player, int index)
    {
        if (equipmentSlot == EquipmentSlot.MAINHAND && Config.shiftClickEquipping && ServerHats.isItemAllowed(slots.get(index).getStack().getItem()))
            return EquipmentSlot.HEAD;
        else
            return equipmentSlot;
    }


}
