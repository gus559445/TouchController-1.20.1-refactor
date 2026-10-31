package top.fifthlight.touchcontroller.common_1_20_1.refactor

import net.minecraft.client.Minecraft
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.ProjectileWeaponItem
import top.fifthlight.combine.platform_1_20_1.toCombine
import top.fifthlight.touchcontroller.common.config.GlobalConfig

/** 原版饮食上限：饱食度到 20 就吃不下了。 */
private const val MAX_FOOD_LEVEL = 20

/**
 * 找出**此刻真的能用出去**的那只手（主手优先），没有就返回 null。
 *
 * <p>上游只判断 `hasItemsOnHand(usableItems)`：物品在清单里就算可用，于是</p>
 * <ul>
 *   <li>满饥饿拿普通食物 → 照样锁 USE → 其实吃不下 → 方块挖不动；</li>
 *   <li>拿甜浆果这类「可放置的食物」长按方块 → 锁 USE 后原版先走 useItemOn → 先把浆果放出去。</li>
 * </ul>
 * <p>这里把手上的实际情况算清楚：</p>
 * <ul>
 *   <li>不在清单里 → 这手不算；</li>
 *   <li>非食物（弓、弩、盾、药水）→ 能用；</li>
 *   <li>`canAlwaysEat()`（金苹果 / 附魔金苹果）→ 满饥饿也能吃，能用；</li>
 *   <li>普通食物 → 只有 `foodLevel &lt; 20` 才算能用。</li>
 * </ul>
 */
internal fun findUsableHand(config: GlobalConfig): InteractionHand? {
    val list = config.item.usableItems
    val minecraftPlayer = Minecraft.getInstance().player ?: return null
    // 创造/旁观：原版 Player.canEat 会走 abilities.invulnerable 分支，饿不着也能吃；
    // getProjectile 在 instabuild 时直接给箭。所以这里要放行，不能按生存模式的
    // 「饥饿值/弹药」去否掉创造模式本来就能用的东西。
    val creative = minecraftPlayer.abilities.instabuild
    return InteractionHand.entries.firstOrNull { hand ->
        val stack = minecraftPlayer.getItemInHand(hand)
        if (stack.isEmpty) return@firstOrNull false
        if (stack.toCombine().item !in list) return@firstOrNull false
        // 弓 / 弩：没有弹药就拉不动。上游会照样判「能用」→ 锁 USE → 长按方块既不射也不挖。
        // 这里按原版 getProjectile 的实际结果判断，没箭就当这手用不出去，长按改判挖方块。
        if (!creative && stack.item is ProjectileWeaponItem && minecraftPlayer.getProjectile(stack).isEmpty) {
            return@firstOrNull false
        }
        if (!stack.isEdible) return@firstOrNull true
        val food = stack.item.getFoodProperties() ?: return@firstOrNull true
        if (food.canAlwaysEat()) return@firstOrNull true
        if (creative) return@firstOrNull true
        minecraftPlayer.foodData.foodLevel < MAX_FOOD_LEVEL
    }
}

internal fun isHeldItemUsableNow(config: GlobalConfig): Boolean = findUsableHand(config) != null



/**
 * 主动**开始**使用手上物品（吃第一口 / 拉弓），不等原版从右键路径自己走到这一步。
 *
 * <p>为什么必须主动调：原版 `startUseItem` 的顺序是「先对方块 useItemOn，再 useItem」。
 * 甜浆果 / 发光浆果这种既是食物又是 BlockItem 的东西，对着方块按住右键时
 * 会先在方块上放一颗，根本轮不到吃。手动 `gameMode.useItem` 让玩家进入
 * 「正在使用物品」状态后，原版后续每一帧都会走 `continueUsingItem`，
 * 不再拿这一下右键去放方块。</p>
 */
internal fun startUsingHeldItem(hand: InteractionHand) {
    val minecraft = Minecraft.getInstance()
    val player = minecraft.player ?: return
    val gameMode = minecraft.gameMode ?: return
    // isUsingItem 之外再看使用剩余 tick：刚发包那一帧 isUsingItem 可能还没置位，
    // 不加这条会在两帧内连发两次 useItem，把进食进度重置。
    if (player.isUsingItem || player.useItemRemainingTicks > 0) return
    if (player.getItemInHand(hand).isEmpty) return
    gameMode.useItem(player, hand)
}
