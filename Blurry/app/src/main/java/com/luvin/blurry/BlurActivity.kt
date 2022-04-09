package com.luvin.blurry

import android.animation.AnimatorListenerAdapter
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
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
import com.luvin.blurry.utils.*
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
    private lateinit var loadingText: TextView

    private lateinit var photoUri: Uri
    private lateinit var photoBitmap: Bitmap

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

    private fun createPhotoView()
    {
        photoView = ImageView(this).apply {
            scaleType = ImageView.ScaleType.FIT_CENTER
        }

        Glide.with(this)
            .load(photoUri)
            .transform( BlurTransformation(currentBlur) )
            .transition( DrawableTransitionOptions.withCrossFade(100) )
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {

                    resource?.let {
                        photoBitmap = it.toBitmap()
                    }

                    return false
                }

            })
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
        photoView.apply {
            drawable?.colorFilter = PorterDuffColorFilter(Theme.alphaColor(Theme.BLACK, currentDim), PorterDuff.Mode.SRC_ATOP)
        }
    }

    private fun updatePhotoBlur()
    {
        ValueAnimator.ofFloat(1F, 0F).apply {
            duration = 1000L
            repeatCount = 1
            repeatMode = ValueAnimator.REVERSE

            addUpdateListener {
                val a = it.animatedValue as Float
                photoView.alpha = a

                println(it.animatedFraction)

                if (a == 0F)
                {
                    println("DUDE!")
                    val blurBitmap = fastblur(photoBitmap, currentBlur)
                    val blurDrawable = BitmapDrawable(resources, blurBitmap).apply {
                        colorFilter = PorterDuffColorFilter(Theme.alphaColor(Theme.BLACK, currentDim), PorterDuff.Mode.SRC_ATOP)
                    }
                    photoView.setImageDrawable(blurDrawable)
                }
            }

            start()
        }
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