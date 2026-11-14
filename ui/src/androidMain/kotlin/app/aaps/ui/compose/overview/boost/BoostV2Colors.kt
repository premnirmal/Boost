package app.aaps.ui.compose.overview.boost

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import app.aaps.core.interfaces.overview.ActivityMode
import app.aaps.core.interfaces.overview.BoostV5State
import app.aaps.core.ui.compose.LocalAapsIsDark
import app.aaps.core.ui.compose.StatusLevel
import app.aaps.ui.compose.overview.graphs.activityHeartRateColor

/** The original Boost dark palette, with matching roles for the light theme. */
@Immutable
internal data class BoostV2Colors(
    val isDark: Boolean = true,
    val background: Color = Color(0xFF0D0F14),
    val cardBackground: Color = Color(0xFF10121A),
    val stroke: Color = Color(0xFF1A1D28),
    val accent: Color = Color(0xFF00D4FF),
    val textPrimary: Color = Color(0xFFE8EAF0),
    val textSecondary: Color = Color(0xFFAAAAAA),
    val labelGrey: Color = Color(0xFF9AA0AD),
    val green: Color = Color(0xFF4CAF50),
    val amber: Color = Color(0xFFFFB300),
    val red: Color = Color(0xFFFF5252),
    val blue: Color = Color(0xFF60A5FA),
    val purple: Color = Color(0xFFC084FC),
    val orange: Color = Color(0xFFFB923C),
    val bgUrgent: Color = Color(0xFFFF4444),
    val bgHigh: Color = Color(0xFFFFFF00),
    val bgInRange: Color = Color(0xFF00FF00),
    val bgLow: Color = Color(0xFFFF0000),
    val chartBackground: Color = Color(0xFF0A0C10),
    val chartInRangeFill: Color = Color(0x0C00D4FF),
    val chartInRangeUpperBorder: Color = Color(0x3300D4FF),
    val chartInRangeLowerBorder: Color = Color(0x4DFF5252),
    val chartTargetLine: Color = Color(0x26FFFFFF),
    val chartNowLine: Color = Color(0x1AFFFFFF),
    val chartIobLine: Color = Color(0xFF60A5FA),
    val chartIobFill: Color = Color(0x6660A5FA),
    val chartPredIob: Color = Color(0xFF1E88E5),
    val chartPredCob: Color = Color(0xFFFB8C00),
    val chartPredACob: Color = Color(0x99FB8C00),
    val chartPredUam: Color = Color(0xFFC9BD60),
    val chartPredZt: Color = Color(0xFF00D2D2),
    val chartInsulinActivity: Color = Color(0xFFD3F166),
    val chartBolus: Color = Color(0xFF1EA3E5),
    val chartBasalLine: Color = Color(0xFF00FFFF),
    val chartBasalFill: Color = Color(0x59FB923C),
    val chartActivityLine: Color = Color(0xFFD3F166),
    val chartActivityFill: Color = Color(0x4DD3F166),
    val chartHeartRateLine: Color = activityHeartRateColor(isDark = true),
    val chartBgReading: Color = Color(0xFFE8EAF0)
) {
    val accentFill = accent.copy(alpha = 0x1A / 255f)
    val accentStroke = accent.copy(alpha = 0x26 / 255f)
    val pillIob = blue
    val pillDynIsf = purple
    val pillTdd = orange
    val pillProfile = accent
    val rangeActiveFill = accentFill
    val rangeInactiveFill = Color.Transparent
    val chartCarbs = amber
    val chartGrid = stroke
    val chartAxisLabel = textSecondary
}

private val DarkBoostColors = BoostV2Colors()

@Composable
internal fun rememberBoostV2Colors(): BoostV2Colors {
    val isDark = LocalAapsIsDark.current
    val scheme = MaterialTheme.colorScheme
    return remember(isDark, scheme) { boostV2Colors(isDark, scheme) }
}

internal fun boostV2Colors(isDark: Boolean, scheme: ColorScheme): BoostV2Colors {
    if (isDark) return DarkBoostColors

    // Darker shades keep the same glucose and alert meanings readable on light surfaces.
    val green = Color(0xFF1B5E20)
    val amber = Color(0xFF795500)
    val red = Color(0xFFB71C1C)
    val blue = Color(0xFF0D47A1)
    val purple = Color(0xFF6A1B9A)
    val orange = Color(0xFF8A3D00)
    val teal = Color(0xFF006064)
    return DarkBoostColors.copy(
        isDark = false,
        background = scheme.surface,
        cardBackground = scheme.surfaceContainerLow,
        stroke = scheme.outlineVariant,
        accent = scheme.primary,
        textPrimary = scheme.onSurface,
        textSecondary = scheme.onSurfaceVariant,
        labelGrey = scheme.onSurfaceVariant,
        green = green,
        amber = amber,
        red = red,
        blue = blue,
        purple = purple,
        orange = orange,
        bgUrgent = red,
        bgHigh = amber,
        bgInRange = green,
        bgLow = red,
        chartBackground = scheme.surface,
        chartInRangeFill = scheme.primary.copy(alpha = 0x0C / 255f),
        chartInRangeUpperBorder = scheme.primary.copy(alpha = 0x33 / 255f),
        chartInRangeLowerBorder = red.copy(alpha = 0x4D / 255f),
        chartTargetLine = scheme.onSurface.copy(alpha = 0x26 / 255f),
        chartNowLine = scheme.onSurface.copy(alpha = 0x1A / 255f),
        chartIobLine = blue,
        chartIobFill = blue.copy(alpha = 0x66 / 255f),
        chartPredIob = blue,
        chartPredCob = orange,
        chartPredACob = orange.copy(alpha = 0.85f),
        chartPredUam = purple,
        chartPredZt = teal,
        chartInsulinActivity = green,
        chartBolus = blue,
        chartBasalLine = teal,
        chartBasalFill = orange.copy(alpha = 0x59 / 255f),
        chartActivityLine = green,
        chartActivityFill = green.copy(alpha = 0x4D / 255f),
        chartHeartRateLine = activityHeartRateColor(isDark = false),
        chartBgReading = scheme.onSurface
    )
}

/** 5-zone BG ring colour by mg/dL. */
internal fun bgZoneColor(bgMgdl: Double, colors: BoostV2Colors): Color = when {
    bgMgdl > 250 -> colors.bgUrgent
    bgMgdl > 180 -> colors.bgHigh
    bgMgdl >= 70 -> colors.bgInRange
    bgMgdl >= 54 -> colors.bgLow
    bgMgdl > 0   -> colors.bgUrgent
    else         -> colors.labelGrey
}

/** Pump reservoir / battery / age warning level. */
internal fun statusLevelColorV2(level: StatusLevel, colors: BoostV2Colors): Color = when (level) {
    StatusLevel.NORMAL      -> colors.textPrimary
    StatusLevel.WARNING     -> colors.amber
    StatusLevel.CRITICAL    -> colors.red
    StatusLevel.UNSPECIFIED -> colors.textSecondary
}

/** AID (loop) status. */
internal fun aidStatusColorV2(status: AidStatus, colors: BoostV2Colors): Color = when (status) {
    AidStatus.ACTIVE    -> colors.green
    AidStatus.ATTENTION -> colors.amber
    AidStatus.ALERT     -> colors.red
    AidStatus.DISABLED  -> colors.labelGrey
}

/** Exercise / activity mode. */
internal fun activityModeColorV2(mode: ActivityMode, colors: BoostV2Colors): Color = when (mode) {
    ActivityMode.ACTIVE    -> colors.amber
    ActivityMode.INACTIVE  -> colors.blue
    ActivityMode.SLEEP_IN  -> colors.purple
    ActivityMode.BOOST_OFF -> colors.labelGrey
    ActivityMode.NORMAL    -> colors.textSecondary
}

internal fun v5StateColorV2(state: BoostV5State, colors: BoostV2Colors): Color =
    if (colors.isDark) Color(state.colorHex) else when (state) {
        BoostV5State.IDLE       -> colors.labelGrey
        BoostV5State.OBSERVING  -> colors.amber
        BoostV5State.CONFIRMED  -> colors.red
        BoostV5State.COMMITTED  -> colors.orange
        BoostV5State.RECOVERING -> colors.chartBasalLine
    }
