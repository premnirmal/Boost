package app.aaps.ui.compose.overview.graphs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.aaps.core.data.configuration.Constants
import app.aaps.core.graph.vico.Square
import app.aaps.core.interfaces.overview.graph.GraphConfig
import app.aaps.core.interfaces.overview.graph.GraphDataPoint
import app.aaps.core.ui.CoreUiStrings
import app.aaps.core.ui.compose.AapsSpacing
import app.aaps.core.ui.compose.LocalAapsIsDark
import app.aaps.core.ui.compose.LocalDateUtil
import app.aaps.core.ui.compose.stringResource
import com.patrykandpatrick.vico.compose.cartesian.Scroll
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.VicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.VicoZoomState
import com.patrykandpatrick.vico.compose.cartesian.Zoom
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.cartesian.axis.Axis
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.compose.cartesian.data.lineModel
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.component.LineComponent
import com.patrykandpatrick.vico.compose.common.component.TextComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.data.ExtraStore
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

@Composable
fun HeartRateGraph(
    viewModel: GraphViewModel,
    enabled: Boolean,
    timeRange: Pair<Long, Long>,
    nowTimestamp: Long,
    modifier: Modifier = Modifier
) {
    if (!enabled) return

    val data by viewModel.heartRateGraphFlow.collectAsStateWithLifecycle()
    val height = GraphConfig.DEFAULT_GRAPH_HEIGHT_DP.dp
    val hasVisibleReadings = data.segments.any { segment ->
        segment.any { it.timestamp in timeRange.first..timeRange.second }
    }

    Column(
        modifier = modifier.fillMaxWidth().padding(top = AapsSpacing.medium)
    ) {
        if (hasVisibleReadings) {
            val scrollState = rememberVicoScrollState(scrollEnabled = false, initialScroll = Scroll.Absolute.Start)
            val zoomState = rememberVicoZoomState(zoomEnabled = false, initialZoom = Zoom.Content)
            LaunchedEffect(timeRange) {
                zoomState.zoom(Zoom.Content)
                scrollState.scroll(Scroll.Absolute.Start)
            }
            ActivityGraph(
                heartRateSegments = data.segments,
                steps = emptyList(),
                scrollState = scrollState,
                zoomState = zoomState,
                derivedTimeRange = timeRange,
                nowTimestamp = nowTimestamp,
                modifier = Modifier.fillMaxWidth().height(height)
            )
        } else {
            Box(
                modifier = Modifier.fillMaxWidth().height(height),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(CoreUiStrings.no_data),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Immutable
data class ActivityGraphColors(
    val heartRate: Color,
    val steps: Color,
    val stepsFill: Color,
    val label: Color,
    val grid: Color,
    val nowLine: Color
)

internal fun activityHeartRateColor(isDark: Boolean): Color =
    if (isDark) Color(0xFFFF6B81) else Color(0xFF880E4F)

@Composable
fun rememberActivityGraphColors(): ActivityGraphColors {
    val isDark = LocalAapsIsDark.current
    val scheme = MaterialTheme.colorScheme
    return remember(isDark, scheme) {
        val steps = if (isDark) Color(0xFFD3F166) else Color(0xFF1B5E20)
        ActivityGraphColors(
            heartRate = activityHeartRateColor(isDark),
            steps = steps,
            stepsFill = steps.copy(alpha = 0x4D / 255f),
            label = scheme.onSurfaceVariant,
            grid = scheme.outlineVariant,
            nowLine = scheme.onSurface.copy(alpha = 0x1A / 255f)
        )
    }
}

private val ActivityAxisScaleKey = ExtraStore.Key<Pair<CartesianLayerRangeProvider, CartesianLayerRangeProvider>>()

/** The Boost activity renderer, shared with Trio. Steps and heart rate keep separate axes and HR gaps. */
@Composable
fun ActivityGraph(
    heartRateSegments: List<List<GraphDataPoint>>,
    steps: List<GraphDataPoint>,
    scrollState: VicoScrollState,
    zoomState: VicoZoomState,
    derivedTimeRange: Pair<Long, Long>?,
    nowTimestamp: Long,
    modifier: Modifier = Modifier,
    visibleRange: Pair<Double, Double>? = null,
    inspect: GraphInspectState = remember { GraphInspectState() },
    colors: ActivityGraphColors = rememberActivityGraphColors()
) {
    val (minTimestamp, maxTimestamp) = derivedTimeRange ?: run {
        val now = LocalDateUtil.current.now()
        (now - Constants.GRAPH_TIME_RANGE_HOURS * 60 * 60 * 1000L) to now
    }
    val maxX = remember(minTimestamp, maxTimestamp) { timestampToX(maxTimestamp, minTimestamp) }
    val stableTimeRange = remember(minTimestamp / 60000, maxTimestamp / 60000) { minTimestamp to maxTimestamp }
    val hrSegments = remember(heartRateSegments, stableTimeRange) {
        projectHeartRateSegments(heartRateSegments, stableTimeRange)
    }
    val hrPoints = remember(hrSegments) { hrSegments.flatten() }
    val stepsPoints = remember(steps, stableTimeRange) { projectActivityPoints(steps, stableTimeRange) }
    val hasHr = hrPoints.size >= 2
    val hasSteps = stepsPoints.size >= 2
    val leftPoints = if (hasSteps) stepsPoints else if (hasHr) hrPoints else emptyList()
    val rightPoints = if (hasSteps && hasHr) hrPoints else emptyList()
    val leftData = if (hasSteps) listOf(stepsPoints) else if (hasHr) hrSegments else emptyList()
    val rightData = if (hasSteps && hasHr) hrSegments else emptyList()

    val modelProducer = remember { CartesianChartModelProducer() }
    val leftRange = rememberActivityGraphRange(leftPoints, visibleRange, maxX, fromZero = hasSteps)
    val rightRange = rememberActivityGraphRange(rightPoints, visibleRange, maxX, fromZero = false)
    // Range changes need a transaction even when the readings did not change.
    LaunchedEffect(leftData, rightData, maxX, leftRange, rightRange) {
        withContext(NonCancellable) {
            modelProducer.runTransaction {
                listOf(leftData, rightData).forEach { segments ->
                    lineModel {
                        series(x = normalizerX(maxX), y = NORMALIZER_Y)
                        if (segments.isEmpty()) {
                            series(x = listOf(0.0, 1.0), y = listOf(0.0, 0.0))
                        } else segments.forEach { points ->
                            series(x = points.map { it.first }, y = points.map { it.second })
                        }
                    }
                }
                extras { it[ActivityAxisScaleKey] = leftRange to rightRange }
            }
        }
    }

    val hrLine = remember(colors) {
        LineCartesianLayer.Line(
            fill = LineCartesianLayer.LineFill.single(Fill(colors.heartRate)),
            stroke = LineCartesianLayer.LineStroke.Continuous(thickness = 1.5.dp),
            areaFill = null,
            interpolator = Square
        )
    }
    val stepsLine = remember(colors) {
        LineCartesianLayer.Line(
            fill = LineCartesianLayer.LineFill.single(Fill(colors.steps)),
            stroke = LineCartesianLayer.LineStroke.Continuous(thickness = 1.5.dp),
            areaFill = LineCartesianLayer.AreaFill.single(Fill(colors.stepsFill)),
            interpolator = Square
        )
    }
    val normalizerLine = remember { createNormalizerLine() }
    val leftLines = remember(leftData, hasSteps, hrLine, stepsLine, normalizerLine) {
        listOf(normalizerLine) + List(maxOf(leftData.size, 1)) { if (hasSteps) stepsLine else hrLine }
    }
    val rightLines = remember(rightData, hrLine, normalizerLine) {
        listOf(normalizerLine) + List(maxOf(rightData.size, 1)) { hrLine }
    }
    val hasRightAxis = rightPoints.isNotEmpty()
    val timeFormatter = rememberTimeFormatter(minTimestamp)
    val bottomAxisItemPlacer = rememberBottomAxisItemPlacer(minTimestamp)
    val nowLine = rememberNowLine(minTimestamp, nowTimestamp, colors.nowLine)
    val inspectLine = rememberInspectLine(inspect)

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider = LineCartesianLayer.LineProvider.series(leftLines),
                rangeProvider = leftRange,
                verticalAxisPosition = Axis.Position.Vertical.Start
            ),
            rememberLineCartesianLayer(
                lineProvider = LineCartesianLayer.LineProvider.series(rightLines),
                rangeProvider = rightRange,
                verticalAxisPosition = Axis.Position.Vertical.End
            ),
            startAxis = rememberActivityGraphStartAxis(colors.label, colors.grid),
            endAxis = VerticalAxis.rememberEnd(
                line = null,
                label = rememberTextComponent(style = TextStyle(color = colors.heartRate)),
                horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Inside,
                tick = null,
                guideline = null,
                itemPlacer = remember(hasRightAxis) { if (hasRightAxis) VerticalAxis.ItemPlacer.step() else NoLabelsAxisItemPlacer }
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = timeFormatter,
                itemPlacer = bottomAxisItemPlacer,
                label = rememberTextComponent(style = TextStyle(color = colors.label)),
                guideline = LineComponent(fill = Fill(colors.grid))
            ),
            decorations = remember(nowLine, inspectLine) { listOfNotNull(nowLine, inspectLine) },
            getXStep = { _, _, _ -> 1.0 },
            marker = SilentInspectMarker,
            markerVisibilityListener = inspect.visibilityListener,
            markerController = inspect.controller
        ),
        modelProducer = modelProducer,
        modifier = modifier,
        scrollState = scrollState,
        zoomState = zoomState
    )
}

internal fun projectActivityPoints(data: List<GraphDataPoint>, timeRange: Pair<Long, Long>): List<Pair<Double, Double>> {
    val maxX = timestampToX(timeRange.second, timeRange.first)
    return data.map { timestampToX(it.timestamp, timeRange.first) to it.value }
        .filter { it.first in 0.0..maxX }
        .sortedBy { it.first }
}

internal fun projectHeartRateSegments(
    segments: List<List<GraphDataPoint>>,
    timeRange: Pair<Long, Long>
): List<List<Pair<Double, Double>>> =
    segments.map { projectActivityPoints(it, timeRange) }.filter { it.isNotEmpty() }

internal fun List<Pair<Double, Double>>.inWindow(window: Pair<Double, Double>?): List<Pair<Double, Double>> =
    if (window == null) this else filter { it.first in window.first..window.second }

@Composable
internal fun rememberActivityGraphRange(
    points: List<Pair<Double, Double>>,
    visibleRange: Pair<Double, Double>?,
    maxX: Double,
    fromZero: Boolean = true
): CartesianLayerRangeProvider = remember(points, visibleRange, maxX, fromZero) {
    val values = points.inWindow(visibleRange).ifEmpty { points }.map { it.second }
    val low = values.minOrNull() ?: 0.0
    val high = values.maxOrNull() ?: 1.0
    val scale = if (fromZero) zeroFloorNiceRange(low, high.coerceAtLeast(0.0)) else niceScale(low, high)
    CartesianLayerRangeProvider.fixed(minX = 0.0, maxX = maxX, minY = scale.min, maxY = scale.max)
}

@Composable
internal fun rememberActivityGraphStartAxis(labelColor: Color, gridColor: Color) = VerticalAxis.rememberStart(
    label = rememberTextComponent(
        style = TextStyle(color = labelColor),
        minWidth = TextComponent.MinWidth.fixed(30.dp)
    ),
    guideline = LineComponent(fill = Fill(gridColor))
)
