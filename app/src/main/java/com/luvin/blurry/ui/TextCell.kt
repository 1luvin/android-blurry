package com.luvin.blurry.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.luvin.blurry.R
import com.luvin.blurry.extension.dp
import com.luvin.blurry.extension.textSizeDp
import com.luvin.blurry.util.DrawableUtil
import com.luvin.blurry.util.Layout
import com.luvin.blurry.util.Resource
import com.luvin.blurry.util.Theme

@SuppressLint("ViewConstructor")
class TextCell(
    context: Context,
    text: String,
) : FrameLayout(context) {
    private val imageView: ImageView
    private val textView: TextView

    private val imageSizeDp: Int get() = 24
    private val indentDp: Int get() = 20

    init {
        setPadding(indentDp.dp, 0, indentDp.dp, 0)
        background = DrawableUtil.rect(
            color = Theme.blue,
            corner = 7f.dp
        )

        imageView = ImageView(context).apply {
            scaleType = ImageView.ScaleType.CENTER

            val d = Resource.drawable(R.drawable.done_bold).apply {
                setTint(Theme.white)
            }
            setImageDrawable(d)
        }
        addView(
            imageView, Layout.ezFrame(
                imageSizeDp, imageSizeDp,
                Gravity.START or Gravity.CENTER_VERTICAL
            )
        )

        textView = TextView(context).apply {
            setTextColor(Theme.white)
            textSizeDp = 17f
            typeface = Typeface.DEFAULT_BOLD
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
            gravity = Gravity.CENTER_VERTICAL

            this.text = text
        }
        addView(
            textView, Layout.ezFrame(
                Layout.MATCH_PARENT, Layout.MATCH_PARENT,
                imageSizeDp + indentDp, 0, indentDp, 0
            )
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(56.dp, MeasureSpec.EXACTLY)
        )
    }
}