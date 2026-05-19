package com.citu.ukayearn.ui.util

import android.graphics.BitmapFactory
import android.widget.ImageView

object AssetImageLoader {
    fun load(imageView: ImageView, assetPath: String?) {
        if (assetPath.isNullOrBlank()) return

        runCatching {
            imageView.context.assets.open(assetPath).use { stream ->
                imageView.imageTintList = null
                imageView.setImageBitmap(BitmapFactory.decodeStream(stream))
            }
        }
    }
}
