package top.fifthlight.touchcontroller.common.ui.tab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.layout.Arrangement
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.drawing.background
import top.fifthlight.combine.modifier.placement.fillMaxSize
import top.fifthlight.combine.modifier.placement.padding
import top.fifthlight.combine.modifier.scroll.verticalScroll
import top.fifthlight.combine.widget.base.layout.Column
import top.fifthlight.touchcontroller.assets.BackgroundTextures
import top.fifthlight.touchcontroller.assets.Texts
import top.fifthlight.touchcontroller.common.ui.component.SwitchPreferenceItem
import top.fifthlight.touchcontroller.common.ui.model.LocalConfigScreenModel

/**
 * 物品分栏下的「盾牌」页。
 *
 * <p>基岩版的举盾只有一条通路：按住 USE。这里把这条通路做成可关的开关——</p>
 * <ul>
 *   <li>开（默认）：拿着盾牌按住潜行键 = 举盾（基岩版做法，Java 版是按使用键）；</li>
 *   <li>关：潜行只蹲下，不举盾。</li>
 * </ul>
 */
object ShieldTab : Tab() {
    override val options = TabOptions(
        titleId = Texts.SCREEN_CONFIG_ITEM_SHIELD_TITLE,
        group = TabGroup.ItemGroup,
        index = 3,
        onReset = { copy(item = item.copy(shieldSneakToBlock = true)) },
    )

    @Composable
    override fun Content() {
        val screenModel = LocalConfigScreenModel.current
        Column(
            modifier = Modifier
                .padding(8)
                .verticalScroll(background = BackgroundTextures.BRICK_BACKGROUND)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8),
        ) {
            val uiState by screenModel.uiState.collectAsState()
            SwitchPreferenceItem(
                title = Text.translatable(Texts.SCREEN_CONFIG_ITEM_SHIELD_SNEAK_TO_BLOCK_TITLE),
                description = Text.translatable(Texts.SCREEN_CONFIG_ITEM_SHIELD_SNEAK_TO_BLOCK_DESCRIPTION),
                value = uiState.config.item.shieldSneakToBlock,
                onValueChanged = { value ->
                    screenModel.updateConfig { copy(item = item.copy(shieldSneakToBlock = value)) }
                },
            )
        }
    }
}
