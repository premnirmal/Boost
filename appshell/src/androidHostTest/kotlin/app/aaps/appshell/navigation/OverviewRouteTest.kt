package app.aaps.appshell.navigation

import com.google.common.truth.Truth.assertThat
import app.aaps.core.interfaces.protection.ProtectionCheck
import app.aaps.core.ui.compose.ScreenMode
import org.junit.jupiter.api.Test

class OverviewRouteTest {

    @Test
    fun `Trio takes priority over the Boost start setting`() {
        assertThat(overviewRoute(trioMode = true, useBoostOverview = true)).isEqualTo(AppRoute.Main.route)
        assertThat(overviewRoute(trioMode = true, useBoostOverview = false)).isEqualTo(AppRoute.Main.route)
    }

    @Test
    fun `turning Trio off keeps the existing Boost start setting`() {
        assertThat(overviewRoute(trioMode = false, useBoostOverview = true)).isEqualTo(AppRoute.BoostV2Overview.route)
        assertThat(overviewRoute(trioMode = false, useBoostOverview = false)).isEqualTo(AppRoute.Main.route)
    }

    @Test
    fun `bolus authorization cannot edit management presets`() {
        assertThat(managementScreenMode(ProtectionCheck.Protection.BOLUS)).isEqualTo(ScreenMode.PLAY)
        assertThat(managementScreenMode(ProtectionCheck.Protection.APPLICATION)).isEqualTo(ScreenMode.PLAY)
    }

    @Test
    fun `preferences authorization can edit management presets`() {
        assertThat(managementScreenMode(ProtectionCheck.Protection.PREFERENCES)).isEqualTo(ScreenMode.EDIT)
        assertThat(managementScreenMode(ProtectionCheck.Protection.MASTER)).isEqualTo(ScreenMode.EDIT)
    }

    @Test
    fun `the Adjustments route keeps its granted mode across a restart`() {
        assertThat(AppRoute.TrioTreatments.createRoute(ScreenMode.PLAY)).isEqualTo("trio_treatments?mode=PLAY")
        assertThat(AppRoute.TrioTreatments.createRoute(ScreenMode.EDIT)).isEqualTo("trio_treatments?mode=EDIT")
    }
}
