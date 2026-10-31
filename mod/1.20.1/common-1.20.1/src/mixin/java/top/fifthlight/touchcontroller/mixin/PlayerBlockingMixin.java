package top.fifthlight.touchcontroller.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ShieldItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.fifthlight.touchcontroller.common_1_20_1.refactor.BedrockShieldFlag;

/**
 * 基岩版举盾：潜行 + 手上有盾 = 举盾。
 *
 * <p>原版 Java 举盾是 `isUsingItem() &amp;&amp; useItem == shield`，一旦成立就把「使用」通道整条占掉，
 * 而 `Minecraft.startAttack()` / `continueAttack()` 都带 `!player.isUsingItem()` 前置，
 * 于是举盾期间挖掘、放置、使用物品、攻击全被禁掉——这是 Java 版的引擎行为。</p>
 *
 * <p>基岩版不是这样：举盾只是实体上的一个状态（`query.blocking`），不占用任何操作通道，
 * 举着盾照样能挖、能放、能攻击。所以这里直接改 `isBlocking()` 的返回值：
 * 原版判定为真时不动，否则在潜行且持有盾牌时补一个 true。
 * 渲染姿态、减伤判定、举盾减速都读这个值，会一起跟随。</p>
 */
@Mixin(LivingEntity.class)
public abstract class PlayerBlockingMixin {
    @Inject(method = "isBlocking()Z", at = @At("RETURN"), cancellable = true)
    private void touchController$sneakToBlock(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            return;
        }
        if (!BedrockShieldFlag.sneakToBlock) {
            return;
        }
        // isBlocking() 定义在 LivingEntity（1.20.1），Player 没有自己的 override，
        // 所以注入点必须在 LivingEntity 上，再筛出玩家。
        if (!((Object) this instanceof Player self)) {
            return;
        }
        if (!self.isShiftKeyDown()) {
            return;
        }
        if (!(self.getMainHandItem().getItem() instanceof ShieldItem)
                && !(self.getOffhandItem().getItem() instanceof ShieldItem)) {
            return;
        }
        cir.setReturnValue(true);
    }
}
