package app.aaps.ui.compose.overview.graphs

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import app.aaps.core.interfaces.overview.graph.GraphConfig
import app.aaps.core.interfaces.overview.graph.GraphDataPoint
import app.aaps.core.interfaces.overview.graph.HeartRateGraphData
import app.aaps.core.interfaces.utils.DateUtil
import app.aaps.core.interfaces.utils.DecimalFormatter
import app.aaps.core.ui.UiMode
import app.aaps.core.ui.compose.AapsTheme
import app.aaps.core.ui.compose.LocalDateUtil
import app.aaps.core.ui.compose.LocalDecimalFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35])
class HeartRateGraphTest {

    @get:Rule
    val compose = createComposeRule()

    private val now = 1_700_000_000_000L
    private val timeRange = now - 3_600_000L to now
    private val viewModel: GraphViewModel = mock()
    private val data = MutableStateFlow(HeartRateGraphData(emptyList()))

    @Test
    fun disabledGraphDoesNotReadDataOrTakeSpace() {
        setContent(enabled = false)

        compose.onNodeWithTag("heart-rate").assertDoesNotExist()
        verifyNoInteractions(viewModel)
    }

    @Test
    fun enabledGraphShowsAnEmptyStateUntilReadingsArrive() {
        whenever(viewModel.heartRateGraphFlow).thenReturn(data)
        setContent(enabled = true)

        compose.onNodeWithText("Heart Rate").assertDoesNotExist()
        compose.onNodeWithText("No data").assertIsDisplayed()
        compose.onNodeWithTag("heart-rate").assertHeightIsAtLeast(GraphConfig.DEFAULT_GRAPH_HEIGHT_DP.dp)
    }

    @Test
    fun enabledGraphDrawsNewReadingsWithoutRestarting() {
        whenever(viewModel.heartRateGraphFlow).thenReturn(data)
        setContent(enabled = true)
        compose.onNodeWithText("No data").assertIsDisplayed()

        compose.runOnIdle {
            data.value = HeartRateGraphData(
                listOf(listOf(GraphDataPoint(now - 300_000L, 70.0), GraphDataPoint(now, 85.0)))
            )
        }

        compose.onNodeWithTag("heart-rate").assertIsDisplayed()
        compose.onNodeWithText("Heart Rate").assertDoesNotExist()
        compose.onNodeWithText("No data").assertDoesNotExist()
    }

    @Test
    fun readingsOutsideTheGlucoseWindowDoNotAppearInTheGraph() {
        data.value = HeartRateGraphData(
            listOf(listOf(GraphDataPoint(now - 7_200_000L, 70.0), GraphDataPoint(now - 6_000_000L, 85.0)))
        )
        whenever(viewModel.heartRateGraphFlow).thenReturn(data)
        setContent(enabled = true)

        compose.onNodeWithText("No data").assertIsDisplayed()
    }

    @Test
    fun changingTheEnableStateShowsAndHidesTheGraph() {
        whenever(viewModel.heartRateGraphFlow).thenReturn(data)
        val enabled = mutableStateOf(false)
        compose.setContent {
            AapsTheme(uiMode = UiMode.LIGHT) {
                HeartRateGraph(viewModel, enabled.value, timeRange, now, Modifier.testTag("heart-rate"))
            }
        }
        compose.onNodeWithTag("heart-rate").assertDoesNotExist()

        compose.runOnIdle { enabled.value = true }
        compose.onNodeWithTag("heart-rate").assertIsDisplayed()
        compose.onNodeWithText("Heart Rate").assertDoesNotExist()

        compose.runOnIdle { enabled.value = false }
        compose.onNodeWithTag("heart-rate").assertDoesNotExist()
    }

    @Test
    fun heartRateReadingsAreDrawnWithTheSharedRedLine() {
        data.value = HeartRateGraphData(
            listOf(listOf(GraphDataPoint(now - 300_000L, 70.0), GraphDataPoint(now, 85.0)))
        )
        whenever(viewModel.heartRateGraphFlow).thenReturn(data)
        setContent(enabled = true)
        compose.waitUntil(timeoutMillis = 5000L) {
            val pixels = compose.onNodeWithTag("heart-rate").captureToImage().toPixelMap()
            (0 until pixels.height).any { y ->
                (0 until pixels.width).any { x ->
                    val color = pixels[x, y]
                    color.red > 0.35f && color.red > color.green * 2f && color.red > color.blue * 1.25f
                }
            }
        }
    }

    private fun setContent(enabled: Boolean) {
        val dateUtil: DateUtil = mock()
        val decimalFormatter: DecimalFormatter = mock()
        whenever(viewModel.bgReadingsFlow).thenReturn(MutableStateFlow(emptyList()))
        whenever(dateUtil.now()).thenReturn(now)
        whenever(decimalFormatter.to0Decimal(any())).thenAnswer { it.getArgument<Double>(0).toInt().toString() }
        compose.setContent {
            CompositionLocalProvider(
                LocalDateUtil provides dateUtil,
                LocalDecimalFormatter provides decimalFormatter
            ) {
                AapsTheme(uiMode = UiMode.LIGHT) {
                    HeartRateGraph(viewModel, enabled, timeRange, now, Modifier.testTag("heart-rate"))
                }
            }
        }
    }
}
