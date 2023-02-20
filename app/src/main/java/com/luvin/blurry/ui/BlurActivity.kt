package com.luvin.blurry.ui

import android.content.ContentValues
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.core.view.*
import coil.request.ImageRequest
import com.google.android.material.snackbar.Snackbar
import com.luvin.blurry.App
import com.luvin.blurry.R
import com.luvin.blurry.extension.asBitmap
import com.luvin.blurry.util.*
import kotlinx.coroutines.*
import java.io.File
import java.lang.Exception

class BlurActivity : AppCompatActivity() {

    private lateinit var rootFrame: FrameLayout
    private lateinit var photoSwitcher: ImageSwitcher
    private lateinit var bottomActionBar: BottomActionBar

    private val imageLoader get() = App.imageLoader
    private lateinit var photoUri: Uri
    private lateinit var photoBitmap: Bitmap
    private lateinit var photoDrawable: Drawable

    private var currentBlur: Int = 0
    private var currentDim: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        photoUri = intent.data!!

        setContentView(createView())

        setupWindow()
    }

    /*
        View
     */

    private fun setupWindow() {
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            rootFrame.updatePadding(0, insets.top, 0, insets.bottom)

            WindowInsetsCompat.CONSUMED
        }
    }

    private fun createView(): View {
        createPhotoSwitcher()
        createBottomActionBar()

        rootFrame = FrameLayout(this).apply {
            setBackgroundColor(Theme.black)

            addView(
                photoSwitcher, Layout.ezFrame(
                    Layout.MATCH_PARENT, Layout.MATCH_PARENT,
                    0, 0, 0, 70
                )
            )

            addView(
                bottomActionBar, Layout.ezFrame(
                    Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
                    Gravity.BOTTOM
                )
            )
        }

        return rootFrame
    }

    private fun createPhotoSwitcher() {
        photoSwitcher = ImageSwitcher(this).apply {
            setFactory {
                ImageView(this@BlurActivity).apply {
                    scaleType = ImageView.ScaleType.FIT_CENTER
                    layoutParams = Layout.ezFrame(
                        Layout.MATCH_PARENT, Layout.MATCH_PARENT
                    )
                }
            }

            setInAnimation(this@BlurActivity, R.anim.`in`)
            setOutAnimation(this@BlurActivity, R.anim.out)
        }

        val request = ImageRequest.Builder(this)
            .data(photoUri)
            .target {
                val b = (it as BitmapDrawable).bitmap
                photoBitmap = b.copy(Bitmap.Config.ARGB_8888, false)
                photoDrawable = it
                photoSwitcher.setImageDrawable(photoDrawable)
            }
            .build()
        imageLoader.enqueue(request)
    }

    private fun createBottomActionBar() {
        bottomActionBar = BottomActionBar(
            context = this,
            onClose = {
                finish()
            },
            onBlurChanged = {
                updatePhotoBlur(it)
            },
            onDimChanged = {
                updatePhotoDim(it)
            },
            onSave = {
                savePhoto()
            }
        )
    }

    /*
        Action
     */

    private fun updatePhotoBlur(blur: Int) {
        if (blur == currentBlur) return

        if (blur < 1) {
            photoDrawable = BitmapDrawable(resources, photoBitmap)
            updatePhotoDim(currentDim)
            photoSwitcher.setImageDrawable(photoDrawable)
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                val blurBitmap = BlurUtil.fastblur(photoBitmap, blur)
                photoDrawable = BitmapDrawable(resources, blurBitmap)
                updatePhotoDim(currentDim)
                photoSwitcher.setImageDrawable(photoDrawable)
            }
        }

        currentBlur = blur
    }

    private fun updatePhotoDim(dim: Float) {
        photoDrawable.colorFilter = PorterDuffColorFilter(
            ColorUtil.alpha(Theme.black, dim),
            PorterDuff.Mode.SRC_ATOP
        )

        currentDim = dim
    }

    private fun savePhoto() {
        val bitmap = photoDrawable.asBitmap()
        val photoName = PhotoUtil.newPhotoFileName()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, photoName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, App.savePath)
                }
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                val fos = imageUri?.let { resolver.openOutputStream(it) }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos?.apply {
                    flush()
                    close()
                }
            }
        } else {
            val blurryDir = File(App.savePath)
            if (!blurryDir.exists()) blurryDir.mkdirs()

            val photoFile = File(blurryDir, photoName)
            if (photoFile.exists()) photoFile.delete()

            try {
                val out = photoFile.outputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.apply {
                    flush()
                    close()
                }
            } catch (e: Exception) {
                /* ignore */
            }

            MediaScannerConnection.scanFile(
                this,
                arrayOf(photoFile.toString()),
                null
            ) { _, _ -> }
        }

        showDownloadedMessage()
    }

    private fun showDownloadedMessage() {
        val snackbar = Snackbar.make(rootFrame, "", 1700).apply {
            view.setBackgroundColor(Color.TRANSPARENT)
        }
        (snackbar.view as Snackbar.SnackbarLayout).apply {
            addView(
                TextCell(
                    context = this@BlurActivity,
                    text = Resource.string(R.string.Saved)
                ),
                0
            )
        }
        snackbar.show()
    }
}