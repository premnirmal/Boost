package app.aaps

import android.content.ComponentName
import android.content.pm.PackageManager
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class LauncherComponentsTest {

    private val packageManager: PackageManager = mock()
    private val trioLauncher: ComponentName = mock()
    private val aapsLauncher: ComponentName = mock()

    @Test
    fun `turning Trio on enables its launcher before disabling the original`() {
        whenever(packageManager.getComponentEnabledSetting(trioLauncher))
            .thenReturn(PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
        whenever(packageManager.getComponentEnabledSetting(aapsLauncher))
            .thenReturn(PackageManager.COMPONENT_ENABLED_STATE_ENABLED)

        switchLauncherComponents(packageManager, trioLauncher, aapsLauncher, trioMode = true)

        val order = inOrder(packageManager)
        order.verify(packageManager).setComponentEnabledSetting(
            trioLauncher, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
        )
        order.verify(packageManager).setComponentEnabledSetting(
            aapsLauncher, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
        )
    }

    @Test
    fun `turning Trio off restores the original launcher before disabling Trio`() {
        whenever(packageManager.getComponentEnabledSetting(trioLauncher))
            .thenReturn(PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
        whenever(packageManager.getComponentEnabledSetting(aapsLauncher))
            .thenReturn(PackageManager.COMPONENT_ENABLED_STATE_DISABLED)

        switchLauncherComponents(packageManager, trioLauncher, aapsLauncher, trioMode = false)

        val order = inOrder(packageManager)
        order.verify(packageManager).setComponentEnabledSetting(
            aapsLauncher, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
        )
        order.verify(packageManager).setComponentEnabledSetting(
            trioLauncher, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
        )
    }

    @Test
    fun `startup applies saved Trio mode to default component states`() {
        whenever(packageManager.getComponentEnabledSetting(trioLauncher))
            .thenReturn(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT)
        whenever(packageManager.getComponentEnabledSetting(aapsLauncher))
            .thenReturn(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT)

        switchLauncherComponents(packageManager, trioLauncher, aapsLauncher, trioMode = true)

        verify(packageManager).setComponentEnabledSetting(
            trioLauncher, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
        )
        verify(packageManager).setComponentEnabledSetting(
            aapsLauncher, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
        )
    }

    @Test
    fun `unchanged launcher states are not written again`() {
        whenever(packageManager.getComponentEnabledSetting(trioLauncher))
            .thenReturn(PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
        whenever(packageManager.getComponentEnabledSetting(aapsLauncher))
            .thenReturn(PackageManager.COMPONENT_ENABLED_STATE_DISABLED)

        switchLauncherComponents(packageManager, trioLauncher, aapsLauncher, trioMode = true)

        verify(packageManager, never()).setComponentEnabledSetting(any(), any(), any())
    }
}
