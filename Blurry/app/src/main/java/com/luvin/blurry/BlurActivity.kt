package com.luvin.blurry

import android.content.ContentValues
import android.content.Intent
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
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.*
import coil.request.ImageRequest
import com.google.android.material.snackbar.Snackbar
import com.luvin.blurry.util.*
import com.luvin.blurry.view.BlurBottomBar
import com.luvin.blurry.view.MessageCell
import kotlinx.coroutines.*
import java.io.File
import java.lang.Exception

class BlurActivity : AppCompatActivity()
{
    private val imageLoader = App.imageLoader

    private lateinit var rootLayout: FrameLayout
    private lateinit var photoSwitcher: ImageSwitcher
    private lateinit var bottomBar: BlurBottomBar

    private lateinit var photoUri: Uri
    private lateinit var photoBitmap: Bitmap
    private lateinit var photoDrawable: Drawable

    private var currentBlur: Int = 5
    private var currentDim: Float = 0F

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        photoUri = intent.data!!

        setupWindow()
        createUI()
    }

    private fun setupWindow()
    {
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, windowInsets ->
            val insets = windowInsets.getInsets( WindowInsetsCompat.Type.systemBars() )

            rootLayout.updatePadding(0, insets.top, 0, insets.bottom)

            WindowInsetsCompat.CONSUMED
        }
    }

    private fun createUI()
    {
        createRootLayout()
        setContentView(rootLayout, Layout.frame(
            Layout.MATCH_PARENT, Layout.MATCH_PARENT
        ))

        createPhotoSwitcher()
        rootLayout.addView(photoSwitcher, Layout.frame(
            Layout.MATCH_PARENT, Layout.MATCH_PARENT,
            Gravity.START or Gravity.TOP,
            0, 0, 0, Utils.dp(70)
        ))

        createBottomBar()
        rootLayout.addView(bottomBar, Layout.frame(
            Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
            Gravity.BOTTOM
        ))
    }

    private fun createRootLayout()
    {
        rootLayout = FrameLayout(this).apply {
            setBackgroundColor( Theme.black )
        }
    }

    private fun createPhotoSwitcher()
    {
        photoSwitcher = ImageSwitcher(this).apply {
            setFactory {
                ImageView(this@BlurActivity).apply {
                    scaleType = ImageView.ScaleType.FIT_CENTER
                    layoutParams = Layout.frame(
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
                photoBitmap = it.toBitmap().copy(Bitmap.Config.ARGB_8888, true)
                photoDrawable = BitmapDrawable(resources, fastblur(photoBitmap, currentBlur))
                photoSwitcher.setImageDrawable(photoDrawable)
            }
            .build()
        imageLoader.enqueue(request)
    }

    private fun createBottomBar()
    {
        bottomBar = BlurBottomBar(this).apply {
            onClose {
                finish()
            }
            onBlurChanged {
                currentBlur = it
                updatePhotoBlur()
            }
            onDimChanged {
                currentDim = it
                updatePhotoDim()
            }
            onSave {
                savePhoto()
            }
        }
    }

    private fun updatePhotoDim()
    {
        photoDrawable.colorFilter = PorterDuffColorFilter(Theme.alphaColor(Theme.black, currentDim), PorterDuff.Mode.SRC_ATOP)
    }

    private fun updatePhotoBlur()
    {
        CoroutineScope(Dispatchers.IO).launch {
            val blurBitmap = fastblur(photoBitmap, currentBlur)
            photoDrawable = BitmapDrawable( resources, blurBitmap )
            updatePhotoDim()

            withContext(Dispatchers.Main) {
                photoSwitcher.setImageDrawable(photoDrawable)
            }
        }
    }

    private fun savePhoto()
    {
        val bitmap = photoDrawable.asBitmap()
        val photoName = Utils.newPhotoFileName()
        val photoFile: File

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
//        {
//            contentResolver?.also { resolver ->
//                val contentValues = ContentValues().apply {
//                    put(MediaStore.MediaColumns.DISPLAY_NAME, photoName)
//                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
//                    put(MediaStore.MediaColumns.RELATIVE_PATH, App.appDirPath())
//                }
//                val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
//                val fos = imageUri?.let { resolver.openOutputStream(it) }
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
//                fos?.apply {
//                    flush()
//                    close()
//                }
//            }
//
//            photoFile = File( App.appDirPath(), photoName )
//        }
//        else
//        {
//            val root = Environment.getExternalStorageDirectory()
//            val blurryPath = "${root.absolutePath}/Pictures/Blurry/"
//            val blurryDir = File(blurryPath)
//            if ( ! blurryDir.exists() ) blurryDir.mkdirs()
//
//            photoFile = File(blurryDir, photoName)
//            if ( photoFile.exists() ) photoFile.delete()
//
//            try {
//                val out = photoFile.outputStream()
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
//                out.apply {
//                    flush()
//                    close()
//                }
//            } catch (e: Exception) {
//                //
//            }
//
//            MediaScannerConnection.scanFile(this, arrayOf(photoFile.toString()), null) { _, _ ->  }
//        }

        showDownloadedMessage()
    }

    private fun showDownloadedMessage()
    {
        val snackbar = Snackbar.make(rootLayout, "", 1700).apply {
            view.setBackgroundColor( Color.TRANSPARENT )
        }
        val snackbarLayout = (snackbar.view as Snackbar.SnackbarLayout)
        snackbarLayout.addView( MessageCell(this), 0 )
        snackbar.show()
    }
}
































//