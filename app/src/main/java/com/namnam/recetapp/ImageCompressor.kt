package com.namnam.recetapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.min

object ImageCompressor {

    private const val MAX_WIDTH = 1080
    private const val MAX_HEIGHT = 1080
    private const val JPEG_QUALITY = 80

    /**
     * Comprime una imagen desde un URI y la guarda en el almacenamiento interno
     * @param context Contexto de la aplicación
     * @param uri URI de la imagen original
     * @return URI de la imagen comprimida o null si falla
     */
    fun compressImage(context: Context, uri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (originalBitmap == null) {
                return null
            }

            val rotatedBitmap = rotateImageIfRequired(context, originalBitmap, uri)

            val resizedBitmap = resizeBitmap(rotatedBitmap, MAX_WIDTH, MAX_HEIGHT)

            val compressedFile = saveCompressedImage(context, resizedBitmap)

            if (rotatedBitmap != originalBitmap) {
                rotatedBitmap.recycle()
            }
            originalBitmap.recycle()
            resizedBitmap.recycle()

            compressedFile?.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun rotateImageIfRequired(context: Context, bitmap: Bitmap, uri: Uri): Bitmap {
        return try {
            val input = context.contentResolver.openInputStream(uri)
            val exif = input?.let { ExifInterface(it) }
            input?.close()

            val orientation = exif?.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            ) ?: ExifInterface.ORIENTATION_NORMAL

            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
                else -> bitmap
            }
        } catch (e: Exception) {
            e.printStackTrace()
            bitmap
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxWidth && height <= maxHeight) {
            return bitmap
        }

        val scale = min(
            maxWidth.toFloat() / width.toFloat(),
            maxHeight.toFloat() / height.toFloat()
        )

        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    private fun saveCompressedImage(context: Context, bitmap: Bitmap): File? {
        return try {
            val imagesDir = File(context.filesDir, "recipe_images")
            if (!imagesDir.exists()) {
                imagesDir.mkdirs()
            }

            val fileName = "recipe_${System.currentTimeMillis()}.jpg"
            val imageFile = File(imagesDir, fileName)

            FileOutputStream(imageFile).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, outputStream)
                outputStream.flush()
            }

            imageFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deleteCompressedImage(imagePath: String?) {
        if (imagePath.isNullOrEmpty()) return

        try {
            val file = File(imagePath)
            if (file.exists() && file.parent?.contains("recipe_images") == true) {
                file.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getImageSize(imagePath: String?): Long {
        if (imagePath.isNullOrEmpty()) return 0

        return try {
            val file = File(imagePath)
            if (file.exists()) {
                file.length() / 1024 // Tamaño en KB
            } else {
                0
            }
        } catch (e: Exception) {
            0
        }
    }

    fun cleanOrphanImages(context: Context, referencedPaths: List<String>) {
        try {
            val imagesDir = File(context.filesDir, "recipe_images")
            if (!imagesDir.exists()) return

            imagesDir.listFiles()?.forEach { file ->
                if (!referencedPaths.contains(file.absolutePath)) {
                    file.delete()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}