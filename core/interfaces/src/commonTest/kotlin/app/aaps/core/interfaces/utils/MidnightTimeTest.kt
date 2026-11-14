package app.aaps.core.interfaces.utils

import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

class MidnightTimeTest {

    @Test
    fun springClockChangeUsesTheOffsetAtMidnight() {
        assertMidnight("2026-03-08T15:00:00Z", "America/New_York", "2026-03-08T05:00:00Z")
    }

    @Test
    fun autumnClockChangeUsesTheOffsetAtMidnight() {
        assertMidnight("2026-11-01T15:00:00Z", "America/New_York", "2026-11-01T04:00:00Z")
    }

    @Test
    fun localDateCanBeAheadOfTheUtcDate() {
        assertMidnight("2026-01-01T15:00:00Z", "Pacific/Auckland", "2026-01-01T11:00:00Z")
    }

    @Test
    fun midnightIncludesTheStartOfTheCurrentDay() {
        assertMidnight("2026-01-01T00:00:00Z", "UTC", "2026-01-01T00:00:00Z")
    }

    private fun assertMidnight(timestamp: String, zone: String, expected: String) {
        assertEquals(
            Instant.parse(expected).toEpochMilliseconds(),
            MidnightTime.before(Instant.parse(timestamp).toEpochMilliseconds(), TimeZone.of(zone))
        )
    }
}
