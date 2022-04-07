package com.luvin.blurry.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.slider.Slider
import com.luvin.blurry.R
import com.luvin.blurry.utils.Layout
import com.luvin.blurry.utils.Locale
import com.luvin.blurry.utils.Theme
import com.luvin.blurry.utils.Utils

class BlurBottomBar(context: Context) : FrameLayout(context)
{
    private lateinit var itemsLayout: LinearLayout
    private lateinit var blurSliderView: SliderView
    private lateinit var dimSliderView: SliderView

    init
    {
        createItemsLayout()
        addView(itemsLayout, Layout.frame(
            Layout.MATCH_PARENT, Layout.MATCH_PARENT
        ))

        createBlurSliderView()
        addView(blurSliderView, Layout.frame(
            Layout.MATCH_PARENT, Layout.MATCH_PARENT
        ))

        createDimSliderView()
        addView(dimSliderView, Layout.frame(
            Layout.MATCH_PARENT, Layout.MATCH_PARENT
        ))
    }

    private fun navigate(from: View, to: View)
    {
        ValueAnimator.ofFloat(0F, 1F).apply {
            duration = 200

            addUpdateListener {
                val a = it.animatedValue as Float

                from.apply {
                    alpha = 1F - a
                }

                to.apply {
                    alpha = a
                }
            }

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?)
                {
                    super.onAnimationStart(animation)

                    to.apply {
                        alpha = 0F
                        visibility = View.VISIBLE
                    }
                }

                override fun onAnimationEnd(animation: Animator?)
                {
                    super.onAnimationEnd(animation)

                    from.apply {
                        visibility = View.GONE
                    }
                }
            })

            start()
        }
    }

    private fun createItemsLayout()
    {
        val blurItem = ItemView().apply {
            itemName = Locale.string(R.string.blur)
            setItemIcon( Theme.drawable(R.drawable.blur) )

            setOnClickListener {
                navigate(itemsLayout, blurSliderView)
            }
        }

        val dimItem = ItemView().apply {
            itemName = Locale.string(R.string.dim)
            setItemIcon( Theme.drawable(R.drawable.dim) )

            setOnClickListener {
                navigate(itemsLayout, dimSliderView)
            }
        }

        itemsLayout = LinearLayout(context).apply {
            addView(blurItem, Layout.linear(
                Layout.MATCH_PARENT, Layout.MATCH_PARENT,
                1F
            ))
            addView(dimItem, Layout.linear(
                Layout.MATCH_PARENT, Layout.MATCH_PARENT,
                1F
            ))
        }
    }

    private fun createBlurSliderView()
    {
        blurSliderView = SliderView().apply {
            visibility = View.GONE
            onDone {
                navigate(this, itemsLayout)
            }
        }
    }

    private fun createDimSliderView()
    {
        dimSliderView = SliderView().apply {
            visibility = View.GONE
            onDone {
                navigate(this, itemsLayout)
            }
        }
    }

    inner class ItemView : FrameLayout(context)
    {
        private var textView: TextView
        private var imageView: ImageView

        private val textS: Float = 17F
        private val imageS: Int = Utils.dp(24)

        private val topM: Int = Utils.dp(15)
        private val bottomM: Int = Utils.dp(15)

        private val textImageIndent: Int = Utils.dp(20)

        var itemName: String = ""
            set(value) {
                field = value

                textView.text = itemName
            }

        fun setItemIcon(drawable: Drawable)
        {
            drawable.setTint( Theme.WHITE )
            imageView.setImageDrawable(drawable)
        }

        init
        {
            isClickable = true

            imageView = ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER
            }
            addView(imageView, Layout.frame(
                imageS, imageS,
                Gravity.TOP or Gravity.CENTER_HORIZONTAL,
                0, topM, 0, 0
            ))

            textView = TextView(context).apply {
                setTextColor( Theme.WHITE )
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, textS)

                setLines(1)
                maxLines = 1
                isSingleLine = true
                ellipsize = TextUtils.TruncateAt.END
            }
            addView(textView, Layout.frame(
                Layout.MATCH_PARENT, Layout.MATCH_PARENT,
                Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,
                Utils.dp(10), 0, Utils.dp(10), bottomM
            ))
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
        {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)

            textView.measure(
                MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.AT_MOST),
                0
            )

            setMeasuredDimension(
                MeasureSpec.getSize(widthMeasureSpec),
                Utils.dp(80)
            )
        }

    }

    inner class SliderView() : FrameLayout(context)
    {
        private var slider: Slider
        private var doneButton: ImageView

        private var doneListener: (() -> Unit)? = null
        fun onDone(l: () -> Unit)
        {
            doneListener = l
        }

        init
        {
            slider = Slider(context).apply {
                valueFrom = 0F
                valueTo = 1F

                trackTintList = ColorStateList.valueOf( Theme.WHITE )
                thumbTintList = ColorStateList.valueOf( Theme.color(R.color.main) )
                haloTintList = ColorStateList.valueOf( Theme.color(R.color.main_under) )

                labelBehavior = LabelFormatter.LABEL_GONE
            }
            addView(slider, Layout.frame(
                Layout.MATCH_PARENT, Layout.MATCH_PARENT,
                Gravity.START or Gravity.CENTER_VERTICAL,
                Utils.dp(20), 0, Utils.dp(20 + 24 + 20), 0
            ))

            doneButton = ImageView(context).apply {
                isClickable = true
                scaleType = ImageView.ScaleType.CENTER

                setImageDrawable( Theme.drawable(R.drawable.done) )

                setOnClickListener {
                    doneListener?.invoke()
                }
            }
            addView(doneButton, Layout.frame(
                Utils.dp(24), Utils.dp(24),
                Gravity.END or Gravity.CENTER_VERTICAL,
                0, 0, Utils.dp(20), 0
            ))
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
        {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)

            setMeasuredDimension(
                MeasureSpec.getSize(widthMeasureSpec),
                Utils.dp(80)
            )
        }

    }
}































//