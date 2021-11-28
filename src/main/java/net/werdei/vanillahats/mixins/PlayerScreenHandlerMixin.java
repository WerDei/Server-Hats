package net.werdei.vanillahats.mixins;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.werdei.vanillahats.Config;
import net.werdei.vanillahats.ServerHats;
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
        if (!Config.shiftClickEquipping && ServerHats.isItemSlotAssigned(slots.get(index).getStack().getItem()))
            return EquipmentSlot.MAINHAND;
        else
            return equipmentSlot;
    }
}