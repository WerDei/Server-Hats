package net.werdei.serverhats.mixins;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.werdei.serverhats.Config;
import net.werdei.serverhats.ServerHats;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin
{
    @Shadow @Final
    private static Map<Item, DispenserBehavior> BEHAVIORS;

    private static DispenserBehavior DEFAULT_BEHAVIOUR = null;

    @Inject(method = "getBehaviorForItem", at = @At("RETURN"), cancellable = true)
    protected void dispenserEquip(ItemStack stack, CallbackInfoReturnable<DispenserBehavior> cir)
    {
        if (DEFAULT_BEHAVIOUR == null)
            setupDefaultBehaviour();
        if (Config.dispenserEquipping && ServerHats.isItemAllowed(stack.getItem()) && cir.getReturnValue() == DEFAULT_BEHAVIOUR)
            cir.setReturnValue(ArmorItem.DISPENSER_BEHAVIOR);
    }

    private static void setupDefaultBehaviour()
    {
        //noinspection SuspiciousMethodCalls
        DEFAULT_BEHAVIOUR = BEHAVIORS.get(new Object());
    }
}
