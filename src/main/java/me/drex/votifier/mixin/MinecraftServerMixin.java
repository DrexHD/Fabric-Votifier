package me.drex.votifier.mixin;

import me.drex.votifier.Votifier;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void votifier$enable(CallbackInfo ci) {
        new Votifier((MinecraftServer) (Object) this);
    }

    @Inject(method = "shutdown", at = @At(value = "HEAD"))
    private void votifier$disable(CallbackInfo ci) {
        Votifier.getInstance().stop();
    }


}
