package io.github.kgriff0n.mixin;

import io.github.kgriff0n.util.dummy.DummyClientPlayerEntity;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//Credits: https://github.com/enjarai/show-me-your-skin
@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
    @Inject(method = "hasLabel(Lnet/minecraft/entity/LivingEntity;)Z", cancellable = true, at = @At("HEAD"))
    private <T extends LivingEntity> void fakeHasLabel(T livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if(livingEntity instanceof DummyClientPlayerEntity) {
            cir.setReturnValue(false);
        }
    }
}
