package com.luvin.blurry.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
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
import com.luvin.blurry.util.Layout
import com.luvin.blurry.util.Locale
import com.luvin.blurry.util.Theme
import com.luvin.blurry.util.Utils

class BlurBottomBar(context: Context) : FrameLayout(context)
{
    private lateinit var itemsLayout: LinearLayout
    private lateinit var blurSliderView: SliderView
    private lateinit var dimSliderView: SliderView

    private var closeListener: (() -> Unit)? = null
    fun onClose(l: () -> Unit)
    {
        closeListener = l
    }

    private var blurListener: ((Int) -> Unit)? = null
    fun onBlurChanged(l: (Int) -> Unit)
    {
        blurListener = l
    }

    private var dimListener: ((Float) -> Unit)? = null
    fun onDimChanged(l: (Float) -> Unit)
    {
        dimListener = l
    }

    private var saveListener: (() -> Unit)? = null
    fun onSave(l: () -> Unit)
    {
        saveListener = l
    }

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

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(Utils.dp(70), MeasureSpec.EXACTLY)
        )
    }

    private fun navigate(from: View, to: View)
    {
        ValueAnimator.ofFloat(0F, 1F).apply {
            duration = 120

            addUpdateListener {
                val p = it.animatedValue as Float
                val to_scale = 0.9F + p * 0.1F
                val from_scale = 1.9F - to_scale

                from.apply {
                    alpha = 1F - p
                    scaleX = from_scale
                    scaleY = from_scale
                }

                to.apply {
                    alpha = p
                    scaleX = to_scale
                    scaleY = to_scale
                }
            }

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?)
                {
                    super.onAnimationStart(animation)

                    to.apply {
                        alpha = 0F
                        scaleX = 0F
                        scaleY = 0F
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
        val closeItem = ItemView().apply {
            itemName = Locale.string(R.string.close)
            setItemIcon( Theme.drawable(R.drawable.close) )

            color = Theme.color(R.color.red)

            setOnClickListener {
                closeListener?.invoke()
            }
        }

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

        val saveItem = ItemView().apply {
            itemName = Locale.string(R.string.save)
            setItemIcon( Theme.drawable(R.drawable.done) )

            color = Theme.color(R.color.main)

            setOnClickListener {
                saveListener?.invoke()
            }
        }

        itemsLayout = LinearLayout(context).apply {
            addView(closeItem, Layout.linear(
                Layout.MATCH_PARENT, Layout.MATCH_PARENT,
                1F
            ))
            addView(blurItem, Layout.linear(
                Layout.MATCH_PARENT, Layout.MATCH_PARENT,
                1F
            ))
            addView(dimItem, Layout.linear(
                Layout.MATCH_PARENT, Layout.MATCH_PARENT,
                1F
            ))
            addView(saveItem, Layout.linear(
                Layout.MATCH_PARENT, Layout.MATCH_PARENT,
                1F
            ))
        }
    }

    private fun createBlurSliderView()
    {
        val slider = Slider(context).apply {
            valueFrom = 5F
            valueTo = 30F

            value = 5F

            addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
                @SuppressLint("RestrictedApi")
                override fun onStartTrackingTouch(slider: Slider)
                {
                    //
                }
                @SuppressLint("RestrictedApi")
                override fun onStopTrackingTouch(slider: Slider)
                {
                    blurListener?.invoke( slider.value.toInt() )
                }
            })
        }

        blurSliderView = SliderView(slider).apply {
            visibility = View.GONE
            onDone {
                navigate(this, itemsLayout)
            }
        }
    }

    private fun createDimSliderView()
    {
        val slider = Slider(context).apply {
            valueFrom = 0F
            valueTo = 50F

            value = 0F

            addOnChangeListener { slider, value, fromUser ->
                dimListener?.invoke( value / 100 )
            }
        }

        dimSliderView = SliderView(slider).apply {
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

        var itemName: String = ""
            set(value) {
                field = value

                textView.text = itemName
            }

        var color: Int = Theme.white
            set(value) {
                field = value

                textView.setTextColor(color)
                imageView.drawable.setTint(color)
            }

        fun setItemIcon(drawable: Drawable)
        {
            drawable.setTint( color )
            imageView.setImageDrawable(drawable)
        }

        init
        {
            isClickable = true
            setOnTouchListener( InstantPress() )

            imageView = ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER
            }
            addView(imageView, Layout.frame(
                Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
                Gravity.TOP or Gravity.CENTER_HORIZONTAL,
                0, Utils.dp(10), 0, 0
            ))

            textView = TextView(context).apply {
                setTextColor( Theme.white )
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17F)

                setLines(1)
                maxLines = 1
                isSingleLine = true
                ellipsize = TextUtils.TruncateAt.END
            }
            addView(textView, Layout.frame(
                Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
                Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,
                Utils.dp(10), 0, Utils.dp(10), Utils.dp(12)
            ))
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
        {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)

            textView.measure(
                MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.AT_MOST),
                0
            )
        }
    }

    inner class SliderView(private val slider: Slider) : FrameLayout(context)
    {
        private var doneTextView: TextView

        private var doneListener: (() -> Unit)? = null
        fun onDone(l: () -> Unit)
        {
            doneListener = l
        }

        init
        {
            slider.apply {
                trackHeight = Utils.dp(3)
                trackTintList = ColorStateList.valueOf( Theme.color(R.color.gray) )
                trackActiveTintList = ColorStateList.valueOf( Theme.color(R.color.white) )

                thumbRadius = Utils.dp(9)
                thumbTintList = ColorStateList.valueOf( Theme.color(R.color.white) )
                haloTintList = ColorStateList.valueOf( Theme.alphaColor(Theme.color(R.color.white), 0.24F) )

                labelBehavior = LabelFormatter.LABEL_GONE
            }
            addView(slider, Layout.frame(
                Layout.WRAP_CONTENT, Layout.MATCH_PARENT,
                Gravity.START or Gravity.CENTER_VERTICAL,
                Utils.dp(20), 0, 0, 0
            ))

            doneTextView = TextView(context).apply {
                setPadding(0, 0, Utils.dp(20), 0)
                gravity = Gravity.CENTER_VERTICAL

                setTextColor( Theme.color(R.color.main) )
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18F)

                isAllCaps = true

                text = Locale.string(R.string.done)

                setOnClickListener {
                    doneListener?.invoke()
                }
            }
            addView(doneTextView, Layout.frame(
                Layout.MATCH_PARENT, Layout.MATCH_PARENT,
                Gravity.END
            ))
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
        {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)

            doneTextView.measure(
                0,
                MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY)
            )

            val availableWidth = measuredWidth - (Utils.dp(20 * 2) + doneTextView.measuredWidth)

            slider.measure(
                MeasureSpec.makeMeasureSpec(availableWidth, MeasureSpec.EXACTLY),
                0
            )
        }
    }
}































//