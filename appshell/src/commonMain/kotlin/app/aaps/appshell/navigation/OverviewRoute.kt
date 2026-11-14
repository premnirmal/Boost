package app.aaps.appshell.navigation

fun overviewRoute(trioMode: Boolean, useBoostOverview: Boolean): String = when {
    trioMode -> AppRoute.Main.route
    useBoostOverview -> AppRoute.BoostV2Overview.route
    else -> AppRoute.Main.route
}
