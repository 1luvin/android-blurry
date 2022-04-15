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
import com.luvin.blurry.util.Theme
import com.luvin.blurry.util.Utils

class MessageCell(context: Context) : FrameLayout(context)
{
    private var doneView: ImageView
    private var messageView: TextView

    init
    {
        setPadding(Utils.dp(20), 0, Utils.dp(20), 0)
        background = Theme.rect(
            R.color.main,
            FloatArray(4).apply {
                fill( Utils.dp(7F) )
            }
        )

        doneView = ImageView(context).apply {
            scaleType = ImageView.ScaleType.CENTER

            setImageDrawable( Theme.drawable(R.drawable.done_bold).apply {
                setTint( Theme.white )
            } )
        }
        addView(doneView, Layout.frame(
            Utils.dp(24), Utils.dp(24),
            Gravity.START or Gravity.CENTER_VERTICAL
        ))

        messageView = TextView(context).apply {
            setTextColor( Theme.white )
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16.5F)
            typeface = Typeface.DEFAULT_BOLD

            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END

            text = "Saved"
        }
        addView(messageView, Layout.frame(
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

        doneView.measure(0, 0)

        val availableWidth = measuredWidth - (paddingLeft + doneView.measuredWidth + Utils.dp(15) + paddingRight)

        messageView.measure(
            MeasureSpec.makeMeasureSpec( availableWidth, MeasureSpec.AT_MOST ),
            0
        )

    }
}



































//