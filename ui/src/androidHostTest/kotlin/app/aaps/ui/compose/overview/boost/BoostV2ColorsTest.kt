package app.aaps.ui.compose.overview.boost

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import app.aaps.core.interfaces.overview.BoostV5State
import app.aaps.ui.compose.overview.graphs.activityHeartRateColor
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class BoostV2ColorsTest {

    private val scheme = lightColorScheme()
    private val light = boostV2Colors(isDark = false, scheme)
    private val dark = boostV2Colors(isDark = true, scheme)

    @Test
    fun `dark mode keeps the original palette regardless of Material colors`() {
        assertThat(dark).isEqualTo(BoostV2Colors())
        assertThat(dark.background).isEqualTo(Color(0xFF0D0F14))
        assertThat(dark.cardBackground).isEqualTo(Color(0xFF10121A))
        assertThat(dark.accent).isEqualTo(Color(0xFF00D4FF))
        assertThat(dark.bgHigh).isEqualTo(Color(0xFFFFFF00))
        assertThat(dark.bgInRange).isEqualTo(Color(0xFF00FF00))
        assertThat(dark.chartBackground).isEqualTo(Color(0xFF0A0C10))
        BoostV5State.entries.forEach { state ->
            assertThat(v5StateColorV2(state, dark)).isEqualTo(Color(state.colorHex))
        }
    }

    @Test
    fun `light mode follows Material surface and text roles`() {
        assertThat(light.isDark).isFalse()
        assertThat(light.background).isEqualTo(scheme.surface)
        assertThat(light.cardBackground).isEqualTo(scheme.surfaceContainerLow)
        assertThat(light.chartBackground).isEqualTo(scheme.surface)
        assertThat(light.accent).isEqualTo(scheme.primary)
        assertThat(light.textPrimary).isEqualTo(scheme.onSurface)
        assertThat(light.textSecondary).isEqualTo(scheme.onSurfaceVariant)
        assertThat(light.stroke).isEqualTo(scheme.outlineVariant)
    }

    @Test
    fun `Boost uses the shared red heart rate color in both themes`() {
        assertThat(light.chartHeartRateLine).isEqualTo(activityHeartRateColor(isDark = false))
        assertThat(dark.chartHeartRateLine).isEqualTo(activityHeartRateColor(isDark = true))
    }

    @Test
    fun `light mode uses updated Material colors rather than a fixed light surface`() {
        val updated = scheme.copy(surface = Color.White, surfaceContainerLow = Color(0xFFF0F0F0), primary = Color(0xFF005BBB))
        val colors = boostV2Colors(isDark = false, updated)

        assertThat(colors.background).isEqualTo(updated.surface)
        assertThat(colors.cardBackground).isEqualTo(updated.surfaceContainerLow)
        assertThat(colors.accent).isEqualTo(updated.primary)
    }

    @Test
    fun `light text and status colors have at least 4 point 5 contrast on cards`() {
        val textColors = listOf(
            light.textPrimary, light.textSecondary, light.labelGrey, light.accent,
            light.green, light.amber, light.red, light.blue, light.purple, light.orange
        ) + BoostV5State.entries.map { v5StateColorV2(it, light) }

        textColors.forEach { color ->
            assertThat(contrast(color, light.cardBackground)).isAtLeast(4.5f)
        }
        assertThat(contrast(light.accent, light.accentFill.compositeOver(light.cardBackground))).isAtLeast(4.5f)
    }

    @Test
    fun `glucose text remains readable on its tinted bobble background`() {
        listOf(light.bgUrgent, light.bgHigh, light.bgInRange, light.bgLow).forEach { color ->
            val disc = color.copy(alpha = 0.12f).compositeOver(light.background)
            assertThat(contrast(color, disc)).isAtLeast(4.5f)
        }
    }

    @Test
    fun `light chart data lines have at least 3 contrast on the chart surface`() {
        listOf(
            light.chartIobLine, light.chartPredIob, light.chartPredCob, light.chartPredACob,
            light.chartPredUam, light.chartPredZt, light.chartInsulinActivity, light.chartBolus,
            light.chartBasalLine, light.chartActivityLine, light.chartHeartRateLine,
            light.bgLow, light.bgInRange, light.bgHigh, light.chartBgReading
        ).forEach { color ->
            assertThat(contrast(color.compositeOver(light.chartBackground), light.chartBackground)).isAtLeast(3f)
        }
    }

    @Test
    fun `glucose thresholds and missing data keep their meanings in both themes`() {
        listOf(light, dark).forEach { colors ->
            assertThat(bgZoneColor(0.0, colors)).isEqualTo(colors.labelGrey)
            assertThat(bgZoneColor(53.0, colors)).isEqualTo(colors.bgUrgent)
            assertThat(bgZoneColor(54.0, colors)).isEqualTo(colors.bgLow)
            assertThat(bgZoneColor(69.0, colors)).isEqualTo(colors.bgLow)
            assertThat(bgZoneColor(70.0, colors)).isEqualTo(colors.bgInRange)
            assertThat(bgZoneColor(180.0, colors)).isEqualTo(colors.bgInRange)
            assertThat(bgZoneColor(181.0, colors)).isEqualTo(colors.bgHigh)
            assertThat(bgZoneColor(250.0, colors)).isEqualTo(colors.bgHigh)
            assertThat(bgZoneColor(251.0, colors)).isEqualTo(colors.bgUrgent)
        }
    }

    private fun contrast(first: Color, second: Color): Float {
        val firstLuminance = first.luminance()
        val secondLuminance = second.luminance()
        return (maxOf(firstLuminance, secondLuminance) + 0.05f) / (minOf(firstLuminance, secondLuminance) + 0.05f)
    }
}
