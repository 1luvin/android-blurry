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
import coil.load
import coil.request.ImageRequest
import com.luvin.blurry.util.*
import com.luvin.blurry.view.InstantPress

class MainActivity : AppCompatActivity()
{
    private val imageLoader = App.imageLoader

    private lateinit var rootLayout: FrameLayout
    private lateinit var imageView: ImageView
    private lateinit var blurryTextView: TextView
    private lateinit var choosePhotoButton: TextView

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        setupWindow()
        createUI()
    }

    /*
        UI
     */

    private fun setupWindow()
    {
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, windowInsets ->
            val insets = windowInsets.getInsets( WindowInsetsCompat.Type.navigationBars() )

            if (this::choosePhotoButton.isInitialized)
            {
                choosePhotoButton.updateLayoutParams<FrameLayout.LayoutParams> {
                    setMargins(
                        Utils.dp(50),
                        0,
                        Utils.dp(50),
                        Utils.dp(50) + insets.bottom
                    )
                }
            }

            WindowInsetsCompat.CONSUMED
        }
    }

    private fun createUI()
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
            0, 0, 0, Utils.dp(200)
        ))

        createChoosePhotoButton()
        rootLayout.addView(choosePhotoButton, Layout.frame(
            Layout.MATCH_PARENT, Utils.dp(60),
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

            alpha = 0F
        }

        val request = ImageRequest.Builder(this)
            .data( Utils.randomPhotoUrl())
            .target {
                it.apply {
                    colorFilter = PorterDuffColorFilter( Theme.alphaColor(Theme.black, 0.5F), PorterDuff.Mode.SRC_ATOP )
                }
                imageView.setImageDrawable(it)

                ValueAnimator.ofFloat(0F, 1F).apply {
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
            text = Locale.string(R.string.app_name)
            gravity = Gravity.CENTER
            setTextColor( Theme.white )
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 70F)
            typeface = Typeface.DEFAULT_BOLD
        }
    }

    private fun createChoosePhotoButton()
    {
        choosePhotoButton = Button(this).apply {
            setOnTouchListener( InstantPress() )

            background = Theme.rect( Theme.white, radii = FloatArray(4).apply {
                fill( Utils.dp(10F) )
            } )

            isAllCaps = false

            text = Locale.string( R.string.choose_photo )
            setTextColor( Theme.black )
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19F)
            typeface = Typeface.DEFAULT_BOLD

            setOnClickListener {
                checkPermissions()
            }
        }
    }

    /*
        PERMISSIONS
     */

    private fun checkPermissions()
    {
        val perms = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        val perm: String? = perms.find {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_DENIED
        }

        if (perm != null)
        {
            requestPermissions(perms, 1337)
        }
        else
        {
            choosePhoto()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode)
        {
            1337 ->
            {
                val found: Int? = grantResults.find {
                    it == PackageManager.PERMISSION_DENIED
                }

                if (found == null)
                {
                    choosePhoto()
                }
                else
                {
                    finish()
                }
            }
        }
    }

    /*
       PHOTO
     */

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































//