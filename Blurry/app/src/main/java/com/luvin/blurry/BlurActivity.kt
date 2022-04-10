package com.luvin.blurry

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
import android.util.TypedValue
import android.view.Gravity
import android.widget.*
import androidx.activity.viewModels
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.*
import coil.ImageLoader
import coil.request.ImageRequest
import com.luvin.blurry.utils.*
import com.luvin.blurry.viewmodels.BlurViewModel
import com.luvin.blurry.views.BlurBottomBar
import java.io.File
import java.lang.Exception

class BlurActivity : AppCompatActivity()
{
    private val viewModel: BlurViewModel by viewModels()
    private val imageLoader = MainApplication.imageLoader

    private lateinit var rootLayout: FrameLayout
    private lateinit var photoSwitcher: ImageSwitcher
    private lateinit var bottomBar: BlurBottomBar
    private lateinit var loadingText: TextView

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
            0, 0, 0, Utils.dp(80)
        ))

        createBottomBar()
        rootLayout.addView(bottomBar, Layout.frame(
            Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
            Gravity.BOTTOM
        ))

        createLoadingText()
        rootLayout.addView(loadingText, Layout.frame(
            Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
            Gravity.CENTER,
            0, 0, 0, Utils.dp(40)
        ))
    }

    private fun createRootLayout()
    {
        rootLayout = FrameLayout(this).apply {
            setBackgroundColor( Theme.BLACK )
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
                finish()
            }
        }
    }

    private fun createLoadingText()
    {
        loadingText = TextView(this).apply {
            setTextColor( Theme.WHITE )
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20F)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)

            text = Locale.string(R.string.blurring)

            alpha = 0F
        }
    }

    private fun updatePhotoDim()
    {
        photoDrawable.colorFilter = PorterDuffColorFilter(Theme.alphaColor(Theme.BLACK, currentDim), PorterDuff.Mode.SRC_ATOP)
    }

    private fun updatePhotoBlur()
    {
        val blurBitmap = fastblur(photoBitmap, currentBlur)
        photoDrawable = BitmapDrawable(resources, blurBitmap).apply {
            colorFilter = PorterDuffColorFilter(Theme.alphaColor(Theme.BLACK, currentDim), PorterDuff.Mode.SRC_ATOP)
        }
        photoSwitcher.setImageDrawable(photoDrawable)
    }

    private fun savePhoto()
    {
        val bitmap = photoDrawable.asBitmap()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            contentResolver?.also { resolver ->
                val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, viewModel.generatePhotoContentValues())
                val fos = imageUri?.let { resolver.openOutputStream(it) }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos?.apply {
                    flush()
                    close()
                }
            }
        }
        else
        {
            val root = Environment.getExternalStorageDirectory()
            val blurryPath = "${root.absolutePath}/Pictures/Blurry/"
            val blurryDir = File(blurryPath)
            if ( ! blurryDir.exists() ) blurryDir.mkdirs()

            val photoName = viewModel.generatePhotoFileName()
            val photoFile = File(blurryDir, photoName)
            if ( photoFile.exists() ) photoFile.delete()

            try {
                val out = photoFile.outputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.apply {
                    flush()
                    close()
                }

                MediaScannerConnection.scanFile(this, arrayOf(photoFile.toString()), null) { _, _ ->  }
            } catch (e: Exception) {
                //
            }
        }
    }
}
































//