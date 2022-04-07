package com.luvin.blurry

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.TransitionOptions
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.luvin.blurry.utils.Layout
import com.luvin.blurry.utils.Locale
import com.luvin.blurry.utils.Theme
import com.luvin.blurry.utils.Utils
import com.luvin.blurry.viewmodels.BlurViewModel
import com.luvin.blurry.views.BlurBottomBar
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.ColorFilterTransformation
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class BlurActivity : AppCompatActivity()
{
    private val viewModel: BlurViewModel by viewModels()

    private lateinit var rootLayout: FrameLayout
    private lateinit var photoView: ImageView
    private lateinit var bottomBar: BlurBottomBar

    private lateinit var uri: Uri

    private var currentBlur: Int = 20
    private var currentDim: Float = 0F

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        uri = intent.data!!

        createUI()
    }

    private fun createUI()
    {
        createRootLayout()
        setContentView(rootLayout, Layout.frame(
            Layout.MATCH_PARENT, Layout.MATCH_PARENT
        ))

        createPhotoView()
        rootLayout.addView(photoView, Layout.frame(
            Layout.MATCH_PARENT, Layout.MATCH_PARENT,
            Gravity.START or Gravity.TOP,
            0, 0, 0, Utils.dp(80)
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
            setBackgroundColor( Theme.BLACK )
        }
    }

    private fun createPhotoView()
    {
        photoView = ImageView(this).apply {
            scaleType = ImageView.ScaleType.FIT_CENTER
        }

        Glide.with(this)
            .load(uri)
            .transform( BlurTransformation(currentBlur) )
            .into(photoView)
    }

    private fun createBottomBar()
    {
        bottomBar = BlurBottomBar(this).apply {
            onBlurChanged {
                currentBlur = it
                updatePhoto()
            }
            onDimChanged {
                currentDim = it
                updatePhoto()
            }
            onSave {
                savePhoto()
            }
        }
    }

    private fun updatePhoto()
    {
        val color = Theme.alphaColor( Theme.BLACK, currentDim )
        Glide.with(photoView)
            .load(uri)
            .transform( BlurTransformation(currentBlur), ColorFilterTransformation(color) )
            .transition( DrawableTransitionOptions.withCrossFade(100) )
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(photoView)
    }

    private fun savePhoto()
    {
        val bitmap = photoView.drawable.toBitmap()

        val dateString = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val photoFileName = "IMG_${dateString}.png"
        val blurryPath = "${Environment.DIRECTORY_PICTURES}/${Locale.string(R.string.app_name)}"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, photoFileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, blurryPath)
            }

            contentResolver?.also { resolver ->
                val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                val fos = imageUri?.let { resolver.openOutputStream(it) }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            }
        }
    }
}
































//