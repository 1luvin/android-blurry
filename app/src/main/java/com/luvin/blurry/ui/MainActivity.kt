package com.luvin.blurry.ui

import android.Manifest
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Typeface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.*
import coil.request.ImageRequest
import com.luvin.blurry.App
import com.luvin.blurry.R
import com.luvin.blurry.extension.dp
import com.luvin.blurry.extension.textSizeDp
import com.luvin.blurry.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var rootFrame: FrameLayout
    private lateinit var imageView: ImageView
    private lateinit var blurryTextView: TextView
    private lateinit var choosePhotoButton: TextView

    private val imageLoader get() = App.imageLoader
    private val readWritePermissionsRequestCode: Int = 1337

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(createView())

        setupWindow()
    }

    /*
        View
     */

    private fun setupWindow() {
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        val m = 50.dp
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())

//            if (this::blurryTextView.isInitialized) {
//                blurryTextView.updateLayoutParams<FrameLayout.LayoutParams> {
//                    setMargins(30.dp, 30.dp + insets.top, 30.dp, 0)
//                }
//            }

            if (this::choosePhotoButton.isInitialized) {
                choosePhotoButton.updateLayoutParams<FrameLayout.LayoutParams> {
                    setMargins(m, 0, m, m + insets.bottom)
                }
            }

            WindowInsetsCompat.CONSUMED
        }
    }

    private fun createView(): View {
        createImageView()
        createBlurryTextView()
        createChoosePhotoButton()

        rootFrame = FrameLayout(this).apply {
            setBackgroundColor(Theme.black)

            addView(
                imageView, Layout.ezFrame(
                    Layout.MATCH_PARENT, Layout.MATCH_PARENT
                )
            )

            addView(
                blurryTextView, Layout.ezFrame(
                    Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
                    Gravity.CENTER_VERTICAL
                )
            )

            addView(
                choosePhotoButton, Layout.ezFrame(
                    Layout.MATCH_PARENT, 60,
                    Gravity.BOTTOM
                )
            )
        }

        return rootFrame
    }

    private fun createImageView() {
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
                        ColorUtil.alpha(Theme.black, 0.5f),
                        PorterDuff.Mode.SRC_ATOP
                    )
                }
                imageView.setImageDrawable(it)

                ObjectAnimator.ofFloat(imageView, View.ALPHA, 0f, 1f)
                    .setDuration(200)
                    .start()
            }
            .build()
        imageLoader.enqueue(request)
    }

    private fun createBlurryTextView() {
        blurryTextView = TextView(this).apply {
            setTextColor(Theme.white)
            textSizeDp = 70f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER

            text = Resource.string(R.string.app_name)
        }
    }

    private fun createChoosePhotoButton() {
        choosePhotoButton = Button(this).apply {
            setOnTouchListener(InstantPress())

            background = DrawableUtil.rect(
                color = Theme.white,
                corner = 10f.dp
            )

            setTextColor(Theme.black)
            textSizeDp = 19f
            typeface = Typeface.DEFAULT_BOLD
            isAllCaps = false

            text = Resource.string(R.string.ChoosePhoto)

            setOnClickListener {
                checkPermissions()
            }
        }
    }

    /*
        Action
     */

    private fun checkPermissions() {
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            readWritePermissionsRequestCode -> {
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

    private val choosePhoto = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it == null || it == Uri.EMPTY) return@registerForActivityResult

        startActivity(
            Intent(this, BlurActivity::class.java).apply {
                data = it
            }
        )
    }

    private fun choosePhoto() = choosePhoto.launch("image/*")
}