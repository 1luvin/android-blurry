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
import com.luvin.blurry.util.Utils

class MessageCell(context: Context) : FrameLayout(context)
{
    private var imageView: ImageView
    private var textView: TextView

    init
    {
        setPadding(Utils.dp(20), 0, Utils.dp(20), 0)
        background = Theme.rect(
            Theme.blue,
            FloatArray(4).apply {
                fill( Utils.dp(7F) )
            }
        )

        imageView = ImageView(context).apply {
            scaleType = ImageView.ScaleType.CENTER

            setImageDrawable( Theme.drawable(R.drawable.done_bold).apply {
                setTint( Theme.white )
            } )
        }
        addView(imageView, Layout.frame(
            Utils.dp(24), Utils.dp(24),
            Gravity.START or Gravity.CENTER_VERTICAL
        ))

        textView = TextView(context).apply {
            setTextColor( Theme.white )
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17F)
            typeface = Typeface.DEFAULT_BOLD

            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END

            text = Locale.string(R.string.saved)
        }
        addView(textView, Layout.frame(
            Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
            Gravity.START or Gravity.CENTER_VERTICAL,
            Utils.dp(24 + 15), 0, 0, 0
        ))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec( MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY ),
            MeasureSpec.makeMeasureSpec( Utils.dp(56), MeasureSpec.EXACTLY )
        )

        imageView.measure(0, 0)

        val availableWidth = measuredWidth - (paddingLeft + imageView.measuredWidth + Utils.dp(15) + paddingRight)

        textView.measure(
            MeasureSpec.makeMeasureSpec( availableWidth, MeasureSpec.AT_MOST ),
            0
        )

    }

}



































//