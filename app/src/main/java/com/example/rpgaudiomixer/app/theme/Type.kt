package com.example.rpgaudiomixer.app.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.example.rpgaudiomixer.R

// Google Font Provider
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

// Font Families
val Newsreader = FontFamily(
    Font(googleFont = GoogleFont("Newsreader"), fontProvider = provider)
)

val Manrope = FontFamily(
    Font(googleFont = GoogleFont("Manrope"), fontProvider = provider)
)

// Set of Material typography styles to start with
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = Newsreader,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        letterSpacing = 0.sp,
        color = Gold
    ),
    displayMedium = TextStyle(
        fontFamily = Newsreader,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        color = Gold
    ),
    displaySmall = TextStyle(
        fontFamily = Newsreader,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        color = Gold
    ),
    headlineLarge = TextStyle(
        fontFamily = Newsreader,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        color = Gold
    ),
    titleLarge = TextStyle(
        fontFamily = Newsreader,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        color = Gold
    ),
    bodyLarge = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)