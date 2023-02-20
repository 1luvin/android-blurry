package com.luvin.blurry.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.*
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.slider.Slider
import com.luvin.blurry.R
import com.luvin.blurry.extension.dp
import com.luvin.blurry.extension.setEnabledWithChildren
import com.luvin.blurry.extension.textSizeDp
import com.luvin.blurry.util.ColorUtil
import com.luvin.blurry.util.Layout
import com.luvin.blurry.util.Resource
import com.luvin.blurry.util.Theme

@SuppressLint("ViewConstructor")
class BottomActionBar(
    context: Context,
    private val onClose: () -> Unit,
    private val onBlurChanged: (Int) -> Unit,
    private val onDimChanged: (Float) -> Unit,
    private val onSave: () -> Unit
) : FrameLayout(context) {

    private lateinit var itemsLayout: LinearLayout
    private lateinit var blurSliderView: SliderView
    private lateinit var dimSliderView: SliderView

    private var navigateAnimator: ValueAnimator? = null

    init {
        createItemsLayout()
        addView(
            itemsLayout, Layout.ezFrame(
                Layout.MATCH_PARENT, Layout.MATCH_PARENT
            )
        )

        createBlurSliderView()
        addView(
            blurSliderView, Layout.ezFrame(
                Layout.MATCH_PARENT, Layout.MATCH_PARENT
            )
        )

        createDimSliderView()
        addView(
            dimSliderView, Layout.ezFrame(
                Layout.MATCH_PARENT, Layout.MATCH_PARENT
            )
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(70.dp, MeasureSpec.EXACTLY)
        )
    }

    private fun createItemsLayout() {
        val closeItem = ItemView(
            icon = Resource.drawable(R.drawable.close),
            name = Resource.string(R.string.Close),
            color = Theme.red,
            onClick = onClose
        )

        val blurItem = ItemView(
            icon = Resource.drawable(R.drawable.blur),
            name = Resource.string(R.string.Blur),
            color = Theme.white,
            onClick = {
                navigate(itemsLayout, blurSliderView)
            }
        )

        val dimItem = ItemView(
            icon = Resource.drawable(R.drawable.dim),
            name = Resource.string(R.string.Dim),
            color = Theme.white,
            onClick = {
                navigate(itemsLayout, dimSliderView)
            }
        )

        val saveItem = ItemView(
            icon = Resource.drawable(R.drawable.done),
            name = Resource.string(R.string.Save),
            color = Theme.blue,
            onClick = onSave
        )

        val params = Layout.linear(
            Layout.MATCH_PARENT, Layout.MATCH_PARENT,
            weight = 1f
        )
        itemsLayout = LinearLayout(context).apply {
            addView(
                closeItem, params
            )
            addView(
                blurItem, params
            )
            addView(
                dimItem, params
            )
            addView(
                saveItem, params
            )
        }
    }

    private fun createBlurSliderView() {
        val slider = Slider(context).apply {
            valueFrom = 0f
            valueTo = 30f
            value = 0f

            addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
                @SuppressLint("RestrictedApi")
                override fun onStartTrackingTouch(slider: Slider) {

                }

                @SuppressLint("RestrictedApi")
                override fun onStopTrackingTouch(slider: Slider) {
                    onBlurChanged(slider.value.toInt())
                }
            })
        }

        blurSliderView = SliderView(
            slider = slider,
            onDone = {
                navigate(blurSliderView, itemsLayout)
            }
        ).apply {
            visibility = View.GONE
        }
    }

    private fun createDimSliderView() {
        val slider = Slider(context).apply {
            valueFrom = 0f
            valueTo = 50f
            value = 0f

            addOnChangeListener { _, value, _ ->
                onDimChanged(value / 100)
            }
        }

        dimSliderView = SliderView(
            slider = slider,
            onDone = {
                navigate(dimSliderView, itemsLayout)
            }
        ).apply {
            visibility = View.GONE
        }
    }


    private fun navigate(from: View, to: View) {
        if (navigateAnimator != null) return

        val lowerScale = 0.9f
        navigateAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 150

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    to.apply {
                        setEnabledWithChildren(false)
                        alpha = 0f
                        visibility = View.VISIBLE
                        scaleX = lowerScale
                        scaleY = scaleY
                    }
                    from.setEnabledWithChildren(false)
                }

                override fun onAnimationEnd(animation: Animator?) {
                    to.setEnabledWithChildren(true)
                    from.visibility = View.GONE
                    navigateAnimator = null
                }
            })

            addUpdateListener {
                val v = it.animatedValue as Float

                from.apply {
                    alpha = 1f - v
                    scaleX = lowerScale + (1 - lowerScale) * (1 - v)
                    scaleY = scaleX
                }

                to.apply {
                    alpha = v
                    scaleX = lowerScale + (1 - lowerScale) * v
                    scaleY = scaleX
                }
            }

            start()
        }
    }


    private inner class ItemView(
        icon: Drawable,
        name: String,
        color: Int,
        onClick: () -> Unit
    ) : FrameLayout(context) {

        private val textView: TextView
        private val imageView: ImageView

        private val indentDp: Int get() = 10

        init {
            setOnTouchListener(InstantPress())
            setOnClickListener { onClick() }

            imageView = ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER
                setImageDrawable(icon.apply {
                    setTint(color)
                })
            }
            addView(
                imageView, Layout.ezFrame(
                    Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
                    Gravity.TOP or Gravity.CENTER_HORIZONTAL,
                    0, indentDp, 0, 0
                )
            )

            textView = TextView(context).apply {
                setTextColor(color)
                textSizeDp = 17f
                isSingleLine = true
                ellipsize = TextUtils.TruncateAt.END
                gravity = Gravity.CENTER_HORIZONTAL

                text = name
            }
            addView(
                textView, Layout.ezFrame(
                    Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
                    Gravity.BOTTOM,
                    indentDp, 0, indentDp, indentDp
                )
            )
        }
    }

    private inner class SliderView(
        slider: Slider,
        onDone: () -> Unit
    ) : LinearLayout(context) {

        private val doneTextView: TextView

        private val indentDp: Int get() = 20

        init {
            slider.apply {
                trackHeight = 3.dp
                trackTintList = ColorStateList.valueOf(Theme.gray)
                trackActiveTintList = ColorStateList.valueOf(Theme.white)

                thumbRadius = 9.dp
                thumbTintList = ColorStateList.valueOf(Theme.white)
                haloTintList = ColorStateList.valueOf(ColorUtil.alpha(Theme.white, 0.3f))

                labelBehavior = LabelFormatter.LABEL_GONE
            }
            addView(
                slider, Layout.ezLinear(
                    0, Layout.MATCH_PARENT,
                    weight = 1f,
                    indentDp, 0, 0, 0
                )
            )

            doneTextView = TextView(context).apply {
                setTextColor(Theme.blue)
                textSizeDp = 18f
                isAllCaps = true
                gravity = Gravity.CENTER_VERTICAL

                text = Resource.string(R.string.Done)

                setOnClickListener {
                    onDone()
                }
            }
            addView(
                doneTextView, Layout.ezLinear(
                    Layout.WRAP_CONTENT, Layout.MATCH_PARENT,
                    indentDp, 0, indentDp, 0
                )
            )
        }
    }
}