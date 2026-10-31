package top.fifthlight.touchcontroller.shield;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 「刚攻击完 / 刚用过物品」的盾牌收手窗口。
 *
 * <p>基岩版里攻击动作会把盾放下（手臂在挥），大约 0.3~0.5 秒才恢复举盾。
 * Java 版的 {@code isBlocking()} 只看「是否正在使用物品」，攻击不会改变它，
 * 于是会出现「一边挥拳一边格挡」。这里用一个按实体 id 记的 tick 冷却补上这段。</p>
 */
public final class ShieldCooldown {
    /** 攻击后多少 tick 内不格挡（8 tick ≈ 0.4 秒）。 */
    public static final int ATTACK_COOLDOWN_TICKS = 8;

    private static final Map<Integer, Integer> UNTIL = new ConcurrentHashMap<>();

    private ShieldCooldown() {
    }

    public static void markAttack(int entityId, int tickCount) {
        UNTIL.put(entityId, tickCount + ATTACK_COOLDOWN_TICKS);
    }

    public static boolean isCooling(int entityId, int tickCount) {
        Integer until = UNTIL.get(entityId);
        if (until == null) {
            return false;
        }
        if (tickCount >= until) {
            UNTIL.remove(entityId);
            return false;
        }
        return true;
    }
}
