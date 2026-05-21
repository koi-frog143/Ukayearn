package com.citu.ukayearn.ui.util

import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView

object AssetImageLoader {
    fun load(imageView: ImageView, assetPath: String?) {
        if (assetPath.isNullOrBlank()) return

        runCatching {
            val stream = when {
                assetPath.startsWith("content://") || assetPath.startsWith("file://") ->
                    imageView.context.contentResolver.openInputStream(Uri.parse(assetPath))
                else ->
                    imageView.context.assets.open(assetPath)
            }
            stream?.use { input ->
                imageView.imageTintList = null
                imageView.setImageBitmap(BitmapFactory.decodeStream(input))
            }
        }
    }
}
