package app.aaps.core.ui.compose.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val IcInsulinVial: ImageVector by lazy {
    ImageVector.Builder(
        name = "IcInsulinVial",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Black),
            pathFillType = PathFillType.EvenOdd
        ) {
            moveTo(8f, 2f)
            horizontalLineTo(16f)
            verticalLineTo(5f)
            horizontalLineTo(8f)
            close()

            moveTo(9f, 6f)
            horizontalLineTo(15f)
            verticalLineTo(7f)
            lineTo(18f, 10f)
            verticalLineTo(20f)
            curveTo(18f, 21.1f, 17.1f, 22f, 16f, 22f)
            horizontalLineTo(8f)
            curveTo(6.9f, 22f, 6f, 21.1f, 6f, 20f)
            verticalLineTo(10f)
            lineTo(9f, 7f)
            close()

            moveTo(11f, 12f)
            horizontalLineTo(13f)
            verticalLineTo(14f)
            horizontalLineTo(15f)
            verticalLineTo(16f)
            horizontalLineTo(13f)
            verticalLineTo(18f)
            horizontalLineTo(11f)
            verticalLineTo(16f)
            horizontalLineTo(9f)
            verticalLineTo(14f)
            horizontalLineTo(11f)
            close()
        }
    }.build()
}
