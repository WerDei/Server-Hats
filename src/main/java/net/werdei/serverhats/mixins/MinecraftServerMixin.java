package net.werdei.serverhats.mixins;

import net.minecraft.server.MinecraftServer;
import net.werdei.serverhats.ServerHats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin
{
    @Inject(method = "runServer()V", at = @At("HEAD"))
    public void onRunServer(CallbackInfo callbackInfo)
    {
        ServerHats.reloadConfig();
    }
}
