package top.fifthlight.touchcontroller.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.fifthlight.touchcontroller.shield.ShieldCooldown;

/** 攻击时给盾牌记一个收手窗口（基岩版挥拳时盾是放下的）。 */
@Mixin(Player.class)
public abstract class PlayerAttackMixin {
    @Inject(method = "attack", at = @At("HEAD"))
    private void touchController$markShieldCooldown(Entity target, CallbackInfo ci) {
        Player self = (Player) (Object) this;
        ShieldCooldown.markAttack(self.getId(), self.tickCount);
    }
}
