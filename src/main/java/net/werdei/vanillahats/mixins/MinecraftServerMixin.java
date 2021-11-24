package net.werdei.vanillahats.mixins;

import net.minecraft.server.MinecraftServer;
import net.werdei.vanillahats.ServerHats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin
{
    @Inject(method = "runServer()V", at = @At("HEAD"))
    public void serverRan(CallbackInfo callbackInfo)
    {
        ServerHats.assignEquipmentSlots();
    }
}
