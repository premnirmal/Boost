package app.aaps.core.ui.compose

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.v2.createComposeRule
import app.aaps.core.ui.UiMode
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35])
class AapsThemeTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    @Config(sdk = [31])
    fun trioLightThemeUsesDeviceMaterialColorsOnAndroid12() {
        checkColors(UiMode.LIGHT) {
            dynamicLightColorScheme(LocalContext.current)
        }
    }

    @Test
    fun trioDarkThemeUsesDeviceMaterialColors() {
        checkColors(UiMode.DARK) {
            dynamicDarkColorScheme(LocalContext.current)
        }
    }

    @Test
    fun regularUiUsesTheSameDeviceMaterialColorsAsTrioSource() {
        checkColors(UiMode.LIGHT, trioMode = false) {
            dynamicLightColorScheme(LocalContext.current)
        }
    }

    @Test
    @Config(qualifiers = "night")
    fun systemDarkThemeUsesDeviceDarkColors() {
        checkColors(UiMode.SYSTEM) {
            dynamicDarkColorScheme(LocalContext.current)
        }
    }

    @Test
    @Config(sdk = [30])
    fun olderAndroidKeepsTheTrioLightPalette() {
        checkColors(UiMode.LIGHT) {
            lightColorScheme(
                primary = Color(0xFF0F766E),
                secondary = Color(0xFF0D9488),
                background = Color(0xFFF8FAFC),
                surface = Color(0xFFF8FAFC)
            )
        }
    }

    @Test
    @Config(sdk = [30])
    fun olderAndroidKeepsTheTrioDarkPalette() {
        checkColors(UiMode.DARK) {
            darkColorScheme(
                secondary = Color(0xFF2DD4BF),
                secondaryContainer = Color(0xFF635F6A),
                surface = Color(0xFF0B1220)
            )
        }
    }

    private fun checkColors(
        uiMode: UiMode,
        trioMode: Boolean = true,
        expectedColors: @Composable () -> ColorScheme
    ) {
        lateinit var actual: ColorScheme
        lateinit var expected: ColorScheme
        compose.setContent {
            AapsTheme(trioMode = trioMode, uiMode = uiMode) {
                val colors = MaterialTheme.colorScheme
                val reference = expectedColors()
                SideEffect {
                    actual = colors
                    expected = reference
                }
            }
        }

        compose.runOnIdle {
            assertThat(actual.roles()).containsExactlyElementsIn(expected.roles()).inOrder()
        }
    }

    private fun ColorScheme.roles() = listOf(
        primary, onPrimary, primaryContainer, onPrimaryContainer, inversePrimary,
        secondary, onSecondary, secondaryContainer, onSecondaryContainer,
        tertiary, onTertiary, tertiaryContainer, onTertiaryContainer,
        background, onBackground, surface, onSurface, surfaceVariant, onSurfaceVariant,
        surfaceTint, inverseSurface, inverseOnSurface,
        error, onError, errorContainer, onErrorContainer, outline, outlineVariant, scrim,
        surfaceBright, surfaceDim, surfaceContainer, surfaceContainerHigh, surfaceContainerHighest,
        surfaceContainerLow, surfaceContainerLowest
    )
}
