package com.luvin.blurry

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.luvin.blurry.utils.Layout
import com.luvin.blurry.utils.Theme
import com.luvin.blurry.utils.Utils
import com.luvin.blurry.viewmodels.BlurViewModel
import com.luvin.blurry.views.BlurBottomBar
import jp.wasabeef.glide.transformations.BlurTransformation
import java.io.File

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
            .transition( DrawableTransitionOptions.withCrossFade(100) )
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(photoView)
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

    private fun updatePhotoDim()
    {
        photoView.apply {
            drawable?.colorFilter = PorterDuffColorFilter(Theme.alphaColor(Theme.BLACK, currentDim), PorterDuff.Mode.SRC_ATOP)
        }
    }

    private fun updatePhotoBlur()
    {
        Glide.with(photoView)
            .load(uri)
            .transform( BlurTransformation(currentBlur) )
            .transition( DrawableTransitionOptions.withCrossFade(100) )
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    //
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {

                    resource?.colorFilter = PorterDuffColorFilter(Theme.alphaColor(Theme.BLACK, currentDim), PorterDuff.Mode.SRC_ATOP)

                    return false
                }

            })
            .into(photoView)
    }

    private fun savePhoto()
    {
        val bitmap = photoView.drawable.toBitmap()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            contentResolver?.also { resolver ->
                val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, viewModel.generatePhotoContentValues())
                val fos = imageUri?.let { resolver.openOutputStream(it) }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos?.close()
            }
        }
        else
        {
            val root = Environment.getExternalStorageDirectory()
            val file = File("${root.absolutePath}/Pictures/Blurry/${viewModel.generatePhotoFileName()}").apply {
                createNewFile()
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, file.outputStream())
        }
    }
}
































//