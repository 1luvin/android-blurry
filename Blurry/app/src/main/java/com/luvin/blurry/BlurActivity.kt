package com.luvin.blurry

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.viewModels
import com.luvin.blurry.utils.Layout
import com.luvin.blurry.utils.Theme
import com.luvin.blurry.viewmodels.BlurViewModel
import com.luvin.blurry.views.BlurBottomBar

class BlurActivity : AppCompatActivity()
{
    private val viewModel: BlurViewModel by viewModels()

    private lateinit var rootLayout: FrameLayout
    private lateinit var photoView: ImageView
    private lateinit var bottomBar: BlurBottomBar

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

    }

    private fun createBottomBar()
    {
        bottomBar = BlurBottomBar(this)
    }
}
































//