package com.denizd.textbasic.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.ColorInt
import com.denizd.textbasic.ColorTransparentUtils
import com.denizd.textbasic.QuoteStorage
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

        storage = QuoteStorage(context)

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

        binding.transparencyPicker.apply {
            minValue = 0
            maxValue = 20
            val formatter = NumberPicker.Formatter { value ->
                val temp = value * 5
                "" + temp
            }
            setFormatter(formatter)
            descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
            wrapSelectorWheel = false
            value = storage.getTransparency()

            // Do not touch - fix for value on spinner not showing up until touched
            val f: Field = NumberPicker::class.java.getDeclaredField("mInputText")
            f.isAccessible = true
            val inputText: EditText = f.get(binding.transparencyPicker) as EditText
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

//        binding.switchHighContrast.apply {
//            isChecked = storage.isHighContrastEnabled()
//
//            setOnCheckedChangeListener { _, _ ->
//                updatePreview()
//            }
//        }

//        binding.typefaceMenuTextview.apply {
//            typefaceIndex = storage.getTypefaceIndex()
//            val typefaces = context.resources.getStringArray(R.array.typefaces)
//            val typefaceAdapter = ArrayAdapter(
//                context,
//                android.R.layout.simple_dropdown_item_1line,
//                typefaces
//            )
//            setAdapter(typefaceAdapter)
//            setText(typefaces[typefaceIndex], false)
//
//            setOnItemClickListener { adapterView, view, index, l ->
//                typefaceIndex = index
//                updatePreview()
//            }
//        }

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

//        val isBackgroundDark = if (storage.isInvertedEnabled()) { // bg white
//            (storage.getTransparency() * 5) <= 5
//        } else { // bg black
//            (storage.getTransparency() * 5) > 5
//        }

//        Log.d("ASDF", isBackgroundDark.toString())

//        binding.textPreviewBackground.setBackgroundColor(
//            Color.parseColor(ColorTransparentUtils.transparentColor(context.getColor(if (isBackgroundDark) {
//            R.color.md_theme_light_background
//        } else {
//            R.color.md_theme_dark_background
//        }), storage.getTransparency() * 5)))

//        binding.textPreviewBackground.setBackgroundColor(
//            storage.getColours(
//                !storage.isInvertedEnabled(),
//                abs(storage.getTransparency() - 100),
//                context = context
//            ).second
//        )

        binding.textPreviewBackgroundDark.setBackgroundColor(
            Color.parseColor(ColorTransparentUtils.transparentColor(
                context.getColor(R.color.md_theme_dark_background),
                if (storage.isInvertedEnabled()) {
                    abs((storage.getTransparency() * 5))
                } else {
                    abs((storage.getTransparency() * 5) - 100)
                }
            ))
        )

        val bitmap = CanvasText.drawText(context, true)

        binding.textPreview.setImageBitmap(bitmap)

//        val colours = storage.getColours(
//            binding.switchInvert.isChecked,
////            binding.switchHighContrast.isChecked,
//            binding.transparencyPicker.value,
//            context
//        )
////        binding.exampleText.textSize = binding.textSizePicker.value.toFloat()
//        binding.exampleText.setTextColor(colours.first)
//        binding.background.setBackgroundColor(colours.second)

//        Log.d("VALVALUE", "${(binding.transparencyPicker.value * 5).toString()} /// ${colours.second}")
    }

    private fun save() {
        storage.saveSettings(
            binding.textSizePicker.value,
            binding.switchInvert.isChecked,
//            binding.switchHighContrast.isChecked,
            binding.transparencyPicker.value,
            binding.switchRandom.isChecked,
            widgetGravity,
            backgroundIndex,
            typefaceIndex,
            typefaceStyleIndex,
            binding.outlinePicker.value.toFloat()
        )
    }

    override fun onPause() {
        save()
        super.onPause()
    }
}