package com.luvin.blurry.view

import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.luvin.blurry.R
import com.luvin.blurry.util.Layout
import com.luvin.blurry.util.Locale
import com.luvin.blurry.util.Theme
import com.luvin.blurry.util.AndroidUtil

class TextCell(
    context: Context,
    text: String,
) : FrameLayout(context)
{
    private var imageView: ImageView
    private var textView: TextView


    init
    {
        setPadding( AndroidUtil.dp(20), 0, AndroidUtil.dp(20), 0 )
        background = Theme.rect(
            Theme.blue,
            radii = FloatArray(4) { 7f }
        )

        imageView = ImageView(context).apply {
            scaleType = ImageView.ScaleType.CENTER

            setImageDrawable(
                Theme.drawable(R.drawable.done_bold).apply {
                    setTint(Theme.white)
                }
            )
        }
        addView(imageView, Layout.frame(
            AndroidUtil.dp(24), AndroidUtil.dp(24),
            Gravity.START or Gravity.CENTER_VERTICAL
        ))

        textView = TextView(context).apply {
            setTextColor( Theme.white )
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17f)
            typeface = Typeface.DEFAULT_BOLD
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END

            this.text = text
        }
        addView(textView, Layout.frame(
            Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
            Gravity.START or Gravity.CENTER_VERTICAL,
            AndroidUtil.dp(24 + 15), 0, 0, 0
        ))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec( MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY ),
            MeasureSpec.makeMeasureSpec( AndroidUtil.dp(56), MeasureSpec.EXACTLY )
        )

        imageView.measure(0, 0)

        val availableWidth = measuredWidth - (paddingLeft + imageView.measuredWidth + AndroidUtil.dp(15) + paddingRight)

        textView.measure(
            MeasureSpec.makeMeasureSpec(availableWidth, MeasureSpec.AT_MOST),
            0
        )
    }
}