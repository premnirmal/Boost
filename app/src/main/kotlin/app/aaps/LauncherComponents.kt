package app.aaps

import android.content.ComponentName
import android.content.pm.PackageManager

internal fun switchLauncherComponents(
    packageManager: PackageManager,
    trioLauncher: ComponentName,
    aapsLauncher: ComponentName,
    trioMode: Boolean
) {
    val enabledLauncher = if (trioMode) trioLauncher else aapsLauncher
    val disabledLauncher = if (trioMode) aapsLauncher else trioLauncher

    // Keep a launcher available without stopping the pump process.
    for ((component, state) in listOf(
        enabledLauncher to PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
        disabledLauncher to PackageManager.COMPONENT_ENABLED_STATE_DISABLED
    )) {
        if (packageManager.getComponentEnabledSetting(component) != state) {
            packageManager.setComponentEnabledSetting(component, state, PackageManager.DONT_KILL_APP)
        }
    }
}
