package com.luvin.blurry

import android.Manifest
import android.graphics.Typeface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.luvin.blurry.utils.Layout
import com.luvin.blurry.utils.Locale
import com.luvin.blurry.utils.Theme
import com.luvin.blurry.utils.Utils
import com.luvin.blurry.viewmodels.MainViewModel
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks
{
    private val viewModel: MainViewModel by viewModels()

    private lateinit var rootLayout: FrameLayout
    private lateinit var blurryTextView: TextView
    private lateinit var choosePhotoButton: TextView

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        createUI()
    }

    private fun createUI()
    {
        createRootLayout()
        setContentView(rootLayout, Layout.frame(
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
            Gravity.BOTTOM,
            Utils.dp(50), 0, Utils.dp(50), Utils.dp(50)
        ))
    }

    private fun createRootLayout()
    {
        rootLayout = FrameLayout(this).apply {
            setBackgroundColor( Theme.color(R.color.bg) )
        }
    }

    private fun createBlurryTextView()
    {
        blurryTextView = TextView(this).apply {
            text = Locale.string(R.string.app_name)
            gravity = Gravity.CENTER
            setTextColor( Theme.WHITE )
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 70F)
            typeface = Typeface.DEFAULT_BOLD
        }
    }

    private fun createChoosePhotoButton()
    {
        choosePhotoButton = Button(this).apply {
            background = Theme.rect( R.color.main, radii = FloatArray(4).apply {
                fill( Utils.dp(10F) )
            } )

            text = Locale.string( R.string.choose_photo )
            setTextColor( Theme.WHITE )
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17F)
            typeface = Typeface.DEFAULT_BOLD

            setOnClickListener {
                checkPermissions()
            }
        }
    }

    private fun checkPermissions()
    {
        val perms = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (EasyPermissions.hasPermissions(this, *perms))
        {
            selectPhoto()
        }
        else
        {
            EasyPermissions.requestPermissions(
                this,
                Locale.string(R.string.rationale_storage),
                1337,
                *perms)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>)
    {
        if (perms.size < 2) finish()

        selectPhoto()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>)
    {
        finish()
    }

    private val selectPhoto = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            println("GOT A PHOTO BITCH!")
        }
    }
    private fun selectPhoto() = selectPhoto.launch("image/*")
}































//