package com.chmouel.liseur.ui.theme

import android.content.res.AssetManager
import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * The bundled Literata, the same face the reader offers, so the chrome
 * around a book is set in the type of one. The platform serif it
 * replaces looked like a fallback because it was one.
 */
fun literataFamily(assets: AssetManager): FontFamily = FontFamily(
    Font("fonts/Literata.ttf", assets),
    Font("fonts/Literata-Italic.ttf", assets, style = FontStyle.Italic),
)

/**
 * The CJK serif — Noto Serif SC (OFL), subset to the GB2312 set plus the
 * strings in this build.
 *
 * A `FontFamily` resolves one font per weight and style, and a font with no
 * glyph for a character falls back to the *platform* default (a sans), not to
 * the next entry in the family. So the styles that carry Chinese have to name
 * this family outright — see [liseurTypography]. Adding this font to
 * [literataFamily] instead would look right and do nothing.
 */
fun notoSerifScFamily(assets: AssetManager): FontFamily = FontFamily(
    Font("fonts/NotoSerifSC-Regular.ttf", assets),
    Font("fonts/NotoSerifSC-SemiBold.ttf", assets, weight = FontWeight.SemiBold),
)

fun liseurTypography(serif: FontFamily, cjk: FontFamily = serif): Typography = Typography().run {
    copy(
        displayLarge = displayLarge.copy(fontFamily = cjk),
        displayMedium = displayMedium.copy(fontFamily = cjk),
        displaySmall = displaySmall.copy(fontFamily = cjk),
        headlineLarge = headlineLarge.copy(fontFamily = cjk),
        headlineMedium = headlineMedium.copy(fontFamily = cjk),
        headlineSmall = headlineSmall.copy(fontFamily = cjk),
        titleLarge = titleLarge.copy(
            fontFamily = cjk,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
        ),
        titleMedium = titleMedium.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 22.sp,
        ),
        titleSmall = titleSmall.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 18.sp,
        ),
    )
}

/** The platform-serif fallback, for previews and tests without assets. */
val LiseurTypography = liseurTypography(FontFamily.Serif)
