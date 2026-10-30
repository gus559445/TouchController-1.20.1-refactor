package top.fifthlight.touchcontroller.common_1_20_1.refactor

import net.minecraft.client.Minecraft
import net.minecraft.world.InteractionHand
import top.fifthlight.combine.platform_1_20_1.toCombine
import top.fifthlight.touchcontroller.common.config.ItemList

/** 原版饮食上限：饱食度到 20 就吃不下了。 */
private const val MAX_FOOD_LEVEL = 20

/**
 * 手上物品**此刻**能不能真的用出去。
 *
 * <p>上游只判断 `hasItemsOnHand(usableItems)`——只要物品在「可长按使用」清单里就算可用，
 * 于是满饥饿值拿着食物长按方块时也会被判成「用物品」：锁 USE、吃东西（其实吃不下），
 * 方块一点都挖不动。这里按手补一条「此刻是否真能用」：</p>
 *
 * <ul>
 *   <li>不在清单里 → 这手不算（和上游一致，交给下面的挖方块判定）；</li>
 *   <li>不是食物（弓、弩、盾…）→ 能用；</li>
 *   <li>食物但 `canAlwaysEat()`（金苹果 / 附魔金苹果）→ 满饥饿也能吃，算能用；</li>
 *   <li>普通食物 → 只有饱食度 &lt; 20 才算能用，吃饱了就改判挖方块。</li>
 * </ul>
 *
 * <p>按手逐个判断（主手、副手各算一次），所以「主手吃不下、副手拿着弓」仍算可用，
 * 不会因为主手是食物就把副手的物品一起废掉。</p>
 */
internal fun isHeldItemUsableNow(list: ItemList): Boolean {
    val minecraftPlayer = Minecraft.getInstance().player ?: return true
    return InteractionHand.entries.any { hand ->
        val stack = minecraftPlayer.getItemInHand(hand)
        if (stack.isEmpty) return@any false
        if (stack.toCombine().item !in list) return@any false
        if (!stack.isEdible) return@any true
        val food = stack.item.getFoodProperties() ?: return@any true
        if (food.canAlwaysEat()) return@any true
        minecraftPlayer.foodData.foodLevel < MAX_FOOD_LEVEL
    }
}
