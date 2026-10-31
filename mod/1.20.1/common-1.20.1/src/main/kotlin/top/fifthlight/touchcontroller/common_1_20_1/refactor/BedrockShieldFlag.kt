package top.fifthlight.touchcontroller.common_1_20_1.refactor

/**
 * 基岩版潜行举盾的开关，供 mixin 读取。
 *
 * <p>为什么用静态字段而不是让 mixin 去读配置：mixin 注入的是 `Player.isBlocking()`，
 * 每帧会被渲染、伤害判定、移动减速各调很多次，走 Koin 取配置太重；
 * 这里由 [viewGestureRefactored] 每帧写一次，mixin 只读一个 boolean。</p>
 */
object BedrockShieldFlag {
    /** @JvmField：Java 侧（mixin）要直接读这个静态字段，@JvmStatic 只生成 getter/setter。 */
    @JvmField
    @Volatile
    var sneakToBlock: Boolean = true
}
