package top.fifthlight.touchcontroller.mixin;

import net.minecraftforge.client.settings.KeyModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.fifthlight.touchcontroller.TouchController;

@Mixin(KeyModifier.class)
public class KeyModifierMixin {
    @Inject(method = "getModifier", at = @At("HEAD"), cancellable = true, remap = false)
    private static void overrideGetModifier(CallbackInfoReturnable<KeyModifier> cir) {
        var modifier = TouchController.getCurrentModifier();
        if (modifier != null) {
            cir.setReturnValue(modifier);
        }
    }
}
