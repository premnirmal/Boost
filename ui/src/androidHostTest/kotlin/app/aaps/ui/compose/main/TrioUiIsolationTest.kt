package app.aaps.ui.compose.main

import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.interfaces.AppPlatform
import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import org.junit.jupiter.api.Test
import java.io.File

class TrioUiIsolationTest {

    @Test
    fun `Boost and Trio share the activity graph renderer`() {
        val boost = File(root, "ui/src/androidMain/kotlin/app/aaps/ui/compose/overview/boost/BoostChartContent.kt").readText()
        val shared = File(root, "ui/src/commonMain/kotlin/app/aaps/ui/compose/overview/graphs/HeartRateGraph.kt").readText()
        assertThat(boost).contains("ActivityGraph(")
        assertThat(boost).doesNotContain("private fun V2ActivityGraph")
        assertThat(shared.substringBefore("@Immutable")).contains("ActivityGraph(")
        assertThat(shared).doesNotContain("MaterialTheme.colorScheme.tertiary")
    }

    private val root = generateSequence(File(checkNotNull(System.getProperty("user.dir")))) { it.parentFile }
        .first { File(it, "settings.gradle").isFile }

    @Test
    fun `shared layouts do not own the Android Trio UI`() {
        val trioName = Regex("""\b(?:[Tt]rio\w*|isTrio)\b""")
        listOf(
            "ui/src/commonMain/kotlin/app/aaps/ui/compose/main/MainScreen.kt",
            "ui/src/commonMain/kotlin/app/aaps/ui/compose/main/OverviewScreen.kt",
            "ui/src/commonMain/kotlin/app/aaps/ui/compose/overview/OverviewScreen.kt",
            "ui/src/commonMain/kotlin/app/aaps/ui/compose/overview/chips/ChipsViewModel.kt"
        ).forEach { path ->
            assertWithMessage("Trio code in $path")
                .that(trioName.findAll(File(root, path).readText()).map { it.value }.toList()).isEmpty()
        }
    }

    @Test
    fun `the toggle is local Android UI state and remains available in simple mode`() {
        val key = BooleanKey.GeneralTrioMode
        assertThat(key.key).isEqualTo("trio_mode")
        assertThat(key.defaultValue).isFalse()
        assertThat(key.platforms).isEqualTo(AppPlatform.ANDROID_ONLY)
        assertThat(key.defaultedBySM).isFalse()
        assertThat(key.sync).isNull()
        assertThat(key.showInNsClientMode).isFalse()
        assertThat(key.showInPumpControlMode).isFalse()
    }

    @Test
    fun `both settings paths include the Appearance toggle`() {
        val searchables = File(root, "ui/src/commonMain/kotlin/app/aaps/ui/search/BuiltInSearchables.kt").readText()
        assertThat(searchables.substringAfter("val appearance =").substringBefore("val protection ="))
            .contains("BooleanKey.GeneralTrioMode")
        val graph = File(root, "appshell/src/commonMain/kotlin/app/aaps/appshell/navigation/TrioNavGraph.kt").readText()
        assertThat(graph.substringAfter("composable(AppRoute.TrioSettings.route)")).contains("AllPreferencesScreen(")
        val preferences = File(root, "ui/src/commonMain/kotlin/app/aaps/ui/compose/preferences/AllPreferencesScreen.kt").readText()
        assertThat(preferences).contains("addPreferenceContent(appearancePreferences")
    }

    @Test
    fun `a UI switch starts with no old navigation state`() {
        val activity = File(root, "app/src/main/kotlin/app/aaps/ComposeMainActivity.kt").readText()
        val observers = activity.substringAfter("private fun observePreferences()").substringBefore("private fun setupWakeLock()")
        assertThat(observers.substringAfter("preferences.observe(BooleanKey.GeneralTrioMode)"))
            .contains("intent.putExtra(RESET_UI_NAVIGATION, true)")
        assertThat(activity).contains("super.onCreate(if (resetNavigation) null else savedInstanceState)")
    }

    @Test
    fun `the heart rate graph follows the Trio glucose window and existing enable settings`() {
        val overview = File(root, "app/src/main/kotlin/app/aaps/trio/ui/compose/overview/TrioOverviewScreen.kt").readText()
        assertThat(overview).contains("preferences.observe(BooleanKey.ApsBoostHrIntegrationEnabled)")
        assertThat(overview).contains("preferences.observe(BooleanKey.ApsBoostHealthConnectHrEnabled)")
        val chart = overview.substringAfter("graphContent = { chartHeight ->").substringBefore("if (showNotificationSheet")
        assertThat(chart).contains("belowChart = { timeRange ->")
        assertThat(chart).contains("HeartRateGraph(")
        assertThat(chart).contains("enabled = heartRateIntegrationEnabled || healthConnectHrEnabled")
        assertThat(chart).contains("timeRange = timeRange")
        val glucose = File(root, "app/src/main/kotlin/app/aaps/trio/ui/compose/overview/GlucoseChart.kt").readText()
        assertThat(glucose).contains("belowChart(viewportEndMillis - viewportDurationMillis to viewportEndMillis)")
    }

    @Test
    fun `launcher switching runs at startup and before the UI is recreated`() {
        val activity = File(root, "app/src/main/kotlin/app/aaps/ComposeMainActivity.kt").readText()
        assertThat(activity.substringAfter("super.onCreate(if (resetNavigation) null else savedInstanceState)"))
            .contains("updateLauncherComponents()")
        val observer = activity.substringAfter("preferences.observe(BooleanKey.GeneralTrioMode)")
            .substringBefore("// Wake lock:")
        assertThat(observer.indexOf("updateLauncherComponents()")).isAtLeast(0)
        assertThat(observer.indexOf("updateLauncherComponents()")).isLessThan(observer.indexOf("recreate()"))
        val launcher = activity.substringAfter("private fun updateLauncherComponents()")
            .substringBefore("private fun observePreferences()")
        assertThat(launcher).contains("if (!config.APS) return")
        assertThat(launcher).contains("\"app.aaps.TrioLauncher\"")
        assertThat(launcher).contains("\"app.aaps.AndroidApsLauncher\"")
        assertThat(launcher).contains("trioMode = config.TRIO")
    }

    @Test
    fun `widget taps use the selected overview shell`() {
        val activity = File(root, "app/src/main/kotlin/app/aaps/ComposeMainActivity.kt").readText()
        val widgetNavigation = activity.substringAfter("val openBoostOverview by").substringBefore("// Track last navigated route")
        assertThat(widgetNavigation).contains("overviewRoute(trioMode = config.TRIO, useBoostOverview = true)")
        assertThat(widgetNavigation).contains("popBackStack(route, inclusive = false)")
        assertThat(widgetNavigation).contains("launchSingleTop = true")
    }

    @Test
    fun `Adjustments cannot restore a higher preset editing grant`() {
        val activity = File(root, "app/src/main/kotlin/app/aaps/ComposeMainActivity.kt").readText()
        val tabs = activity.substringAfter("private fun navigateToTrioTab(").substringBefore("private fun openCgmApp(")
        assertThat(tabs).contains("requestAuthorization(ElementType.TEMP_TARGET_MANAGEMENT.protection)")
        assertThat(tabs).contains("TrioTreatments.createRoute(managementScreenMode(granted))")
        assertThat(tabs).contains("restoreState = tab != TrioNavTab.Adjustments")
        val graph = File(root, "appshell/src/commonMain/kotlin/app/aaps/appshell/navigation/TrioNavGraph.kt").readText()
        assertThat(graph).contains("defaultValue = ScreenMode.PLAY.name")
        assertThat(graph).contains("initialMode = mode")
    }
}
