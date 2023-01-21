package com.puntogris.neonmaze.utils

import android.graphics.Color
import android.graphics.Paint

object Utils {

    private val randomNumber = (0..8).random()

    private var colorList = arrayOf(
        "#ff0000", "#ff5000", "#ffb600", "#faff00",
        "#a5ff00", "#2aff00", "#00ffd8",
        "#0033ff", "#9d00ff", "#ff00c3", "#ff005d"
    )

    fun getRandomColor() = colorList[randomNumber]

    val wallPaint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
        strokeWidth = 7F
        setShadowLayer(8f, 0f, 0f, Color.parseColor("#FF00D6"))
        textSize = 60f
    }

    val playerPaint = Paint().apply {
        strokeWidth = 11f
        style = Paint.Style.STROKE
        isAntiAlias = true
        isDither = true
    }

    val exitPaint = Paint().apply {
        color = Color.WHITE
        strokeWidth = 11f
        style = Paint.Style.STROKE
        setShadowLayer(12f, 0f, 0f, Color.parseColor("#FF00D6"))
    }
}
