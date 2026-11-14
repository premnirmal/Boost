package app.aaps.ui.compose.overview.graphs

import androidx.compose.ui.graphics.Color
import app.aaps.core.interfaces.overview.graph.GraphDataPoint
import kotlin.test.Test
import kotlin.test.assertEquals

class ActivityGraphDataTest {

    private val timeRange = 0L to 600_000L

    @Test
    fun heartRateSegmentsKeepGapsBetweenReadings() {
        val segments = listOf(
            listOf(GraphDataPoint(60_000L, 70.0), GraphDataPoint(120_000L, 75.0)),
            listOf(GraphDataPoint(480_000L, 80.0), GraphDataPoint(540_000L, 85.0))
        )

        assertEquals(
            listOf(listOf(1.0 to 70.0, 2.0 to 75.0), listOf(8.0 to 80.0, 9.0 to 85.0)),
            projectHeartRateSegments(segments, timeRange)
        )
    }

    @Test
    fun activityPointsAreSortedAndLimitedToTheGraphWindow() {
        val points = listOf(
            GraphDataPoint(660_000L, 90.0), GraphDataPoint(120_000L, 75.0),
            GraphDataPoint(-60_000L, 60.0), GraphDataPoint(60_000L, 70.0)
        )

        assertEquals(listOf(1.0 to 70.0, 2.0 to 75.0), projectActivityPoints(points, timeRange))
    }

    @Test
    fun segmentsOutsideTheWindowDoNotCreateAnEmptyDataSeries() {
        val segments = listOf(
            listOf(GraphDataPoint(-60_000L, 60.0)),
            listOf(GraphDataPoint(60_000L, 70.0), GraphDataPoint(120_000L, 75.0))
        )

        assertEquals(listOf(listOf(1.0 to 70.0, 2.0 to 75.0)), projectHeartRateSegments(segments, timeRange))
    }

    @Test
    fun heartRateUsesTheBoostRedShadesInBothThemes() {
        assertEquals(Color(0xFF880E4F), activityHeartRateColor(isDark = false))
        assertEquals(Color(0xFFFF6B81), activityHeartRateColor(isDark = true))
    }
}
