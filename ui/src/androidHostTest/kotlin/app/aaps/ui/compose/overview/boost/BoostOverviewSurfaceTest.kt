package app.aaps.ui.compose.overview.boost

import android.app.Activity
import android.view.View
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type
import androidx.core.view.WindowInsetsControllerCompat
import app.aaps.core.ui.UiMode
import app.aaps.core.ui.compose.AapsTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35])
class BoostOverviewSurfaceTest {

    @get:Rule
    val compose = createComposeRule()

    private lateinit var activity: Activity
    private lateinit var view: View
    private lateinit var colors: BoostV2Colors

    @Test
    fun lightThemeUsesLightBoostSurfacesAndDarkSystemIcons() {
        setContent()

        assertDarkIcons(true)
        assertEquals(false, colors.isDark)
    }

    @Test
    fun leavingBoostRestoresLightThemeIcons() {
        val showBoost = mutableStateOf(true)
        setContent(showBoost = showBoost)
        assertDarkIcons(true)

        compose.runOnIdle { showBoost.value = false }

        assertDarkIcons(true)
    }

    @Test
    fun leavingBoostKeepsDarkThemeIcons() {
        val showBoost = mutableStateOf(true)
        setContent(showBoost = showBoost, uiMode = mutableStateOf(UiMode.DARK))
        assertDarkIcons(false)

        compose.runOnIdle { showBoost.value = false }

        assertDarkIcons(false)
    }

    @Test
    fun themeChangesUpdateBoostSurfacesAndSystemIconsTogether() {
        val showBoost = mutableStateOf(true)
        val uiMode = mutableStateOf(UiMode.DARK)
        setContent(showBoost = showBoost, uiMode = uiMode)
        assertDarkIcons(false)
        assertEquals(true, colors.isDark)

        compose.runOnIdle { uiMode.value = UiMode.LIGHT }
        assertDarkIcons(true)
        assertEquals(false, colors.isDark)
        compose.runOnIdle { showBoost.value = false }

        assertDarkIcons(true)
    }

    @Test
    fun backgroundExtendsOutsideThePaddedOverview() {
        setContent {
            Box(Modifier.fillMaxSize().padding(32.dp).background(Color.Magenta))
        }

        val pixels = compose.onRoot().captureToImage().toPixelMap()
        assertEquals(colors.background, pixels[1, 1])
        assertEquals(Color.Magenta, pixels[pixels.width / 2, pixels.height / 2])
    }

    @Test
    fun systemBarBackgroundsCoverLightDrawerContent() {
        setContent {
            Box(Modifier.fillMaxSize().background(Color.White))
        }
        compose.runOnIdle {
            ViewCompat.dispatchApplyWindowInsets(
                view,
                WindowInsetsCompat.Builder()
                    .setInsets(Type.statusBars(), Insets.of(0, 40, 0, 0))
                    .setInsets(Type.navigationBars(), Insets.of(0, 0, 0, 40))
                    .setVisible(Type.systemBars(), true)
                    .build()
            )
        }

        val pixels = compose.onRoot().captureToImage().toPixelMap()
        assertEquals(colors.background, pixels[pixels.width / 2, 1])
        assertEquals(colors.background, pixels[pixels.width / 2, pixels.height - 2])
        assertEquals(Color.White, pixels[pixels.width / 2, pixels.height / 2])
    }

    @Test
    @Config(qualifiers = "+notnight")
    fun systemThemeUsesLightBoostColorsOnALightDevice() {
        setContent(uiMode = mutableStateOf(UiMode.SYSTEM))

        assertEquals(false, colors.isDark)
        assertDarkIcons(true)
    }

    @Test
    @Config(qualifiers = "+night")
    fun systemThemeUsesTheOriginalBoostPaletteOnADarkDevice() {
        setContent(uiMode = mutableStateOf(UiMode.SYSTEM))

        assertEquals(BoostV2Colors(), colors)
        assertDarkIcons(false)
    }

    @Test
    fun overviewChangesItsPaintedBackgroundWithTheTheme() {
        val uiMode = mutableStateOf(UiMode.LIGHT)
        setContent(uiMode = uiMode) {
            BoostV2Screen(
                uiState = BoostUiState(),
                pumpStatus = "",
                actions = BoostActionsState()
            )
        }
        var pixels = compose.onRoot().captureToImage().toPixelMap()
        assertEquals(colors.background, pixels[pixels.width - 2, pixels.height - 2])

        compose.runOnIdle { uiMode.value = UiMode.DARK }

        pixels = compose.onRoot().captureToImage().toPixelMap()
        assertEquals(BoostV2Colors().background, pixels[pixels.width - 2, pixels.height - 2])
        assertDarkIcons(false)
    }

    private fun setContent(
        showBoost: State<Boolean> = mutableStateOf(true),
        uiMode: State<UiMode> = mutableStateOf(UiMode.LIGHT),
        content: @Composable () -> Unit = {}
    ) {
        compose.setContent {
            activity = requireNotNull(LocalActivity.current)
            view = LocalView.current
            AapsTheme(uiMode = uiMode.value) {
                colors = rememberBoostV2Colors()
                Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
                    if (showBoost.value) BoostOverviewSurface(content)
                }
            }
        }
    }

    private fun assertDarkIcons(expected: Boolean) {
        compose.runOnIdle {
            val controller = WindowInsetsControllerCompat(activity.window, view)
            assertEquals(expected, controller.isAppearanceLightStatusBars)
            assertEquals(expected, controller.isAppearanceLightNavigationBars)
        }
    }
}
