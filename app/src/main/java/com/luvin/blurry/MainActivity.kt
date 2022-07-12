package com.luvin.blurry

import android.Manifest
import android.animation.ValueAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Typeface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.*
import coil.request.ImageRequest
import com.luvin.blurry.util.*
import com.luvin.blurry.view.InstantPress

class MainActivity : AppCompatActivity()
{
    private lateinit var rootLayout: FrameLayout
    private lateinit var imageView: ImageView
    private lateinit var blurryTextView: TextView
    private lateinit var choosePhotoButton: TextView

    private val imageLoader = App.imageLoader
    private val readWritePermissionsRequestCode: Int = 1337


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        setupWindow()
        createView()
    }

    // View

    private fun setupWindow()
    {
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, windowInsets ->
            val insets = windowInsets.getInsets( WindowInsetsCompat.Type.navigationBars() )

            if (this::choosePhotoButton.isInitialized)
            {
                choosePhotoButton.updateLayoutParams<FrameLayout.LayoutParams> {
                    setMargins(
                        AndroidUtil.dp(50),
                        0,
                        AndroidUtil.dp(50),
                        AndroidUtil.dp(50) + insets.bottom
                    )
                }
            }

            WindowInsetsCompat.CONSUMED
        }
    }

    private fun createView()
    {
        createRootLayout()
        setContentView(rootLayout, Layout.frame(
            Layout.MATCH_PARENT, Layout.MATCH_PARENT
        ))

        createImageView()
        rootLayout.addView(imageView, Layout.frame(
            Layout.MATCH_PARENT, Layout.MATCH_PARENT
        ))

        createBlurryTextView()
        rootLayout.addView(blurryTextView, Layout.frame(
            Layout.MATCH_PARENT, Layout.MATCH_PARENT,
            Gravity.CENTER,
            0, 0, 0, AndroidUtil.dp(200)
        ))

        createChoosePhotoButton()
        rootLayout.addView(choosePhotoButton, Layout.frame(
            Layout.MATCH_PARENT, AndroidUtil.dp(60),
            Gravity.BOTTOM
        ))
    }

    private fun createRootLayout()
    {
        rootLayout = FrameLayout(this).apply {
            setBackgroundColor( Theme.black )
        }
    }

    private fun createImageView()
    {
        imageView = ImageView(this).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            alpha = 0f
        }

        val url = PhotoUtil.randomBlurredPhotoUrl(200, 400, 10)
        val request = ImageRequest.Builder(this)
            .data(url)
            .target {
                it.apply {
                    colorFilter = PorterDuffColorFilter(
                        Theme.alphaColor(Theme.black, 0.5f),
                        PorterDuff.Mode.SRC_ATOP
                    )
                }
                imageView.setImageDrawable(it)

                ValueAnimator.ofFloat(0f, 1f).apply {
                    duration = 200

                    addUpdateListener {
                        imageView.alpha = this.animatedValue as Float
                    }

                    start()
                }
            }
            .build()
        imageLoader.enqueue(request)
    }

    private fun createBlurryTextView()
    {
        blurryTextView = TextView(this).apply {
            setTextColor(Theme.white)
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 70f)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER

            text = Locale.string(R.string.app_name)
        }
    }

    private fun createChoosePhotoButton()
    {
        choosePhotoButton = Button(this).apply {
            setOnTouchListener( InstantPress() )

            background = Theme.rect(
                Theme.white,
                radii = FloatArray(4) { 10f }
            )

            setTextColor( Theme.black )
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19F)
            typeface = Typeface.DEFAULT_BOLD
            isAllCaps = false

            text = Locale.string( R.string.choose_photo )

            setOnClickListener {
                checkPermissions()
            }
        }
    }

    // Permission

    private fun checkPermissions()
    {
        val perms = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        val perm: String? = perms.find {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_DENIED
        }

        if (perm != null) {
            requestPermissions(perms, readWritePermissionsRequestCode)
        } else {
            choosePhoto()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode)
        {
            readWritePermissionsRequestCode ->
            {
                val found: Int? = grantResults.find {
                    it == PackageManager.PERMISSION_DENIED
                }

                if (found == null) {
                    choosePhoto()
                } else {
                    finish()
                }
            }
        }
    }

    // Photo

    private val choosePhoto = registerForActivityResult( ActivityResultContracts.GetContent() ) { uri: Uri? ->
        uri?.let {
            startBlurActivity(uri)
        }
    }
    private fun choosePhoto() = choosePhoto.launch("image/*")

    private fun startBlurActivity(data: Uri)
    {
        startActivity(
            Intent(this, BlurActivity::class.java).apply {
                setData(data)
            }
        )
    }
}