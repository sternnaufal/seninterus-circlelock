package com.seninterus.circlelock.util

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import androidx.core.content.FileProvider
import com.seninterus.circlelock.model.GameMode
import java.io.File
import java.io.FileOutputStream

object ShareManager {

    fun shareWinResult(
        context: Context,
        level: Int,
        mode: GameMode,
        totalCleared: Int
    ) {
        val bitmap = createResultCard(context, level, mode, totalCleared, isWin = true)
        val uri = saveBitmapAndGetSize(context, bitmap)
        bitmap.recycle()

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "I just cleared Level $level in ${mode.getDisplayName()} mode on Circle Lock! Can you beat that?")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share result"))
    }

    fun shareGameResult(
        context: Context,
        level: Int,
        mode: GameMode,
        totalCleared: Int
    ) {
        val bitmap = createResultCard(context, level, mode, totalCleared, isWin = false)
        val uri = saveBitmapAndGetSize(context, bitmap)
        bitmap.recycle()

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "Just played Circle Lock - Level $level in ${mode.getDisplayName()} mode! Give it a try!")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share result"))
    }

    fun shareStreakResult(
        context: Context,
        streak: Int,
        reward: Int
    ) {
        val bitmap = createStreakCard(context, streak, reward)
        val uri = saveBitmapAndGetSize(context, bitmap)
        bitmap.recycle()

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "I'm on a $streak-day streak on Circle Lock! Just claimed $reward locks!")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share streak"))
    }

    private fun createResultCard(
        context: Context,
        level: Int,
        mode: GameMode,
        totalCleared: Int,
        isWin: Boolean
    ): Bitmap {
        val width = 1080
        val height = 1080
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val bgPaint = Paint().apply {
            color = Color.parseColor("#0A0A0A")
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FFD700")
            style = Paint.Style.STROKE
            strokeWidth = 8f
        }
        canvas.drawCircle(width / 2f, height / 2f - 80f, 200f, circlePaint)

        val innerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = if (isWin) Color.parseColor("#4CAF50") else Color.parseColor("#CF6679")
            style = Paint.Style.FILL
        }
        canvas.drawCircle(width / 2f, height / 2f - 80f, 150f, innerPaint)

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 72f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
        }
        val text = if (isWin) "SOLVED!" else "LOCKED"
        canvas.drawText(text, width / 2f, height / 2f - 60f, textPaint)

        val detailPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FFD700")
            textSize = 48f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("LEVEL $level", width / 2f, height / 2f + 120f, detailPaint)

        val modePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#9E9E9E")
            textSize = 36f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(mode.getDisplayName(), width / 2f, height / 2f + 180f, modePaint)

        val statsPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#616161")
            textSize = 32f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Total locks opened: $totalCleared", width / 2f, height - 200f, statsPaint)

        val brandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#4FC3F7")
            textSize = 28f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("CIRCLE LOCK - SENIN TERUS STUDIO", width / 2f, height - 120f, brandPaint)

        return bitmap
    }

    private fun createStreakCard(
        context: Context,
        streak: Int,
        reward: Int
    ): Bitmap {
        val width = 1080
        val height = 1080
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val bgPaint = Paint().apply {
            color = Color.parseColor("#0A0A0A")
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        val firePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FFD700")
            textSize = 160f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("\uD83D\uDD25", width / 2f, height / 2f - 100f, firePaint)

        val streakPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FFD700")
            textSize = 96f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
        }
        canvas.drawText("$streak", width / 2f, height / 2f + 60f, streakPaint)

        val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 48f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("DAY STREAK", width / 2f, height / 2f + 130f, labelPaint)

        val rewardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#4CAF50")
            textSize = 42f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("+$reward locks earned!", width / 2f, height / 2f + 200f, rewardPaint)

        val brandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#4FC3F7")
            textSize = 28f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("CIRCLE LOCK - SENIN TERUS STUDIO", width / 2f, height - 120f, brandPaint)

        return bitmap
    }

    private fun saveBitmapAndGetSize(context: Context, bitmap: Bitmap): Uri {
        val cacheDir = File(context.cacheDir, "shared_images")
        cacheDir.mkdirs()
        val file = File(cacheDir, "result_${System.currentTimeMillis()}.png")

        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
}
