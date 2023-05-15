package com.denizd.textbasic.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.ColorInt
import com.denizd.textbasic.util.ColorTransparentUtils
import com.denizd.textbasic.db.QuoteStorage
import com.denizd.textbasic.R
import com.denizd.textbasic.databinding.FragmentSettingsBinding
import com.denizd.textbasic.util.viewBinding
import com.denizd.textbasic.widget.CanvasText
import com.google.android.material.transition.MaterialSharedAxis
import java.lang.reflect.Field
import kotlin.math.abs


class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    private lateinit var storage: QuoteStorage
    private lateinit var gravityButtons: Array<ImageButton>

    private var widgetGravity = 4
    private var backgroundIndex = 0
    private var typefaceIndex = 0
    private var typefaceStyleIndex = 0

    private val binding: FragmentSettingsBinding by viewBinding(FragmentSettingsBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.animationLl.animateLayoutChanges(true)

        // TODO a lot of the buttons don't at all do what they're supposed to do!

        storage = QuoteStorage.getInstance(context)

        gravityButtons = arrayOf(
            binding.buttonTopLeft, binding.buttonTopMiddle, binding.buttonTopRight,
            binding.buttonMiddleLeft, binding.buttonMiddleMiddle, binding.buttonMiddleRight,
            binding.buttonBottomLeft, binding.buttonBottomMiddle, binding.buttonBottomRight
        )

        widgetGravity = storage.getWidgetGravity()

        binding.textSizePicker.apply {
            minValue = 1
            maxValue = 168
            descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
            wrapSelectorWheel = false
            value = storage.getTextSize()

            setOnValueChangedListener { _, _, _ ->
                updatePreview()
            }
        }

        binding.textTransparencyPicker.apply {
            minValue = 0
            maxValue = 20
            val formatter = NumberPicker.Formatter { value ->
                val temp = value * 5
                "" + temp
            }
            setFormatter(formatter)
            descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
            wrapSelectorWheel = false
            value = storage.getBackgroundTransparency()

            // Do not touch - fix for value on spinner not showing up until touched
            val f: Field = NumberPicker::class.java.getDeclaredField("mInputText")
            f.isAccessible = true
            val inputText: EditText = f.get(binding.textTransparencyPicker) as EditText
            inputText.filters = arrayOfNulls(0)

            setOnValueChangedListener { _, _, _ ->
                updatePreview()
            }
        }

        binding.bgTransparencyPicker.apply {
            minValue = 0
            maxValue = 20
            val formatter = NumberPicker.Formatter { value ->
                val temp = value * 5
                "" + temp
            }
            setFormatter(formatter)
            descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
            wrapSelectorWheel = false
            value = storage.getBackgroundTransparency()

            // Do not touch - fix for value on spinner not showing up until touched
            val f: Field = NumberPicker::class.java.getDeclaredField("mInputText")
            f.isAccessible = true
            val inputText: EditText = f.get(binding.bgTransparencyPicker) as EditText
            inputText.filters = arrayOfNulls(0)

            setOnValueChangedListener { _, _, _ ->
                updatePreview()
            }
        }

        binding.outlinePicker.apply {
            minValue = 0
            maxValue = 64
            descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
            wrapSelectorWheel = false
            value = storage.getOutlineSize().toInt()

            setOnValueChangedListener { _, _, _ ->
                updatePreview()
            }
        }

        ////////////////////////////////////////////

        binding.switchInvert.apply {
            isChecked = storage.isInvertedEnabled()

            setOnCheckedChangeListener { _, _ ->
                updatePreview()
            }
        }

        binding.switchRandom.apply {
            isChecked = storage.isOrderRandom()

            setOnCheckedChangeListener { _, _ ->
                updatePreview()
            }
        }

        binding.typefaceToggleButton.apply {
            typefaceIndex = storage.getTypefaceIndex()

            check(when (typefaceStyleIndex) {
                2 -> R.id.button3_monospace
                1 -> R.id.button2_serif
                else -> R.id.button1_sansserif
            })

            addOnButtonCheckedListener { group, checkedId, isChecked ->
                typefaceIndex = when (checkedId) {
                    R.id.button3_monospace -> 2
                    R.id.button2_serif -> 1
                    else -> 0
                }
                updatePreview()
            }
        }

        binding.typefaceStyleToggleButton.apply {
            typefaceStyleIndex = storage.getTypefaceStyle()

            check(when (typefaceStyleIndex) {
                3 -> R.id.button4_bold_italics
                2 -> R.id.button3_italics
                1 -> R.id.button2_bold
                else -> R.id.button1_normal
            })

            addOnButtonCheckedListener { group, checkedId, isChecked ->
                typefaceStyleIndex = when (checkedId) {
                    R.id.button4_bold_italics -> 3
                    R.id.button3_italics -> 2
                    R.id.button2_bold -> 1
                    else -> 0
                }
                updatePreview()
            }
        }

        binding.backgroundToggleButton.apply {
            backgroundIndex = storage.getBackgroundType()

            check(when (backgroundIndex) {
                2 -> R.id.button3_shadow
                1 -> R.id.button2_outline
                else -> R.id.button1_background
            })

            addOnButtonCheckedListener { group, checkedId, isChecked ->
                backgroundIndex = when (checkedId) {
                    R.id.button3_shadow -> 2
                    R.id.button2_outline -> 1
                    else -> 0
                }
                updatePreview()
            }
        }

        gravityButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                widgetGravity = index
                setGravityButtons()
                updatePreview()
            }
        }

        setGravityButtons()
        updatePreview()
    }

    private fun setGravityButtons() {
        val transparent = context.getColor(android.R.color.transparent)

        gravityButtons.forEach { button ->
            button.setBackgroundColor(transparent)
        }

        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.colorSecContainer, typedValue, true)
        @ColorInt val colorTertiary = typedValue.data

        gravityButtons[widgetGravity].setBackgroundColor(colorTertiary)
    }

    private fun updatePreview() {

        save()

        if (backgroundIndex == 0) {
            binding.outlineTextview.visibility = View.GONE
            binding.outlinePicker.visibility = View.GONE
        } else {
            binding.outlineTextview.visibility = View.VISIBLE
            binding.outlinePicker.visibility = View.VISIBLE
            if (backgroundIndex == 1) {
                binding.outlineTextview.text = getString(R.string.thickness)
            } else {
                binding.outlineTextview.text = getString(R.string.intensity)
            }
        }

        binding.textPreviewBackgroundDark.setBackgroundColor(
            Color.parseColor(
                ColorTransparentUtils.transparentColor(
                context.getColor(R.color.md_theme_dark_background),
                if (storage.isInvertedEnabled()) {
                    abs((storage.getBackgroundTransparency() * 5))
                } else {
                    abs((storage.getBackgroundTransparency() * 5) - 100)
                }
            ))
        )

        val bitmap = CanvasText.drawText(context, true)

        binding.textPreview.setImageBitmap(bitmap)
    }

    private fun save() {
        storage.saveSettings(
            textSize = binding.textSizePicker.value,
            inverted = binding.switchInvert.isChecked,
            textTransparency = binding.textTransparencyPicker.value,
            bgTransparency = binding.bgTransparencyPicker.value,
            random = binding.switchRandom.isChecked,
            widgetPosition = widgetGravity,
            backgroundType = backgroundIndex,
            typefaceIndex = typefaceIndex,
            typefaceStyleIndex = typefaceStyleIndex,
            outlineSize = binding.outlinePicker.value.toFloat()
        )
    }

    override fun onPause() {
        save()
        super.onPause()
    }
}