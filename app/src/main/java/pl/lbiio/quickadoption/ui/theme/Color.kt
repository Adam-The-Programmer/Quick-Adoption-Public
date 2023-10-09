package pl.lbiio.quickadoption.ui.theme

import android.annotation.SuppressLint
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

val SalmonWhite = Color(0xfffedbd0)
val SimpleWhite = Color(0xFFFFFFFF)
val Red = Color(0xffc5032b)
val Salmon = Color(0xfffbb8ac)
val PurpleBrown = Color(0xFF503537)
val PurpleBrownLight = Color(0xFF684A4C)


@SuppressLint("ConflictingOnColor")
internal val LightColorPalette = lightColors(
    primary = PurpleBrown,
    primaryVariant = Salmon,
    secondary = PurpleBrown,
    background = PurpleBrown,
    surface = SimpleWhite,
    error = Red,
    onPrimary = SimpleWhite,
    onSecondary = PurpleBrown,
    onBackground = SimpleWhite,
    onSurface = PurpleBrown,
    onError = Red
)

internal val DarkColorPalette = lightColors(
    primary = SalmonWhite,
    primaryVariant = Salmon,
    secondary = SimpleWhite,
    background = SalmonWhite,
    surface = SimpleWhite,
    error = Red,
    onPrimary = PurpleBrown,
    onSecondary = PurpleBrown,
    onBackground = PurpleBrown,
    onSurface = PurpleBrown,
    onError = Red
)