package com.denizd.textbasic.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.transition.TransitionManager
import com.denizd.textbasic.BuildConfig
import com.denizd.textbasic.R
import com.denizd.textbasic.databinding.FragmentSettingsNewBinding
import com.denizd.textbasic.db.QuoteStorage
import com.denizd.textbasic.sheet.TextSheet
import com.denizd.textbasic.util.SettingsPreference
import com.denizd.textbasic.util.viewBinding
import com.denizd.textbasic.widget.CanvasText
import com.google.android.material.button.MaterialButton
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import java.util.Calendar

class SettingsNewFragment : BaseFragment(R.layout.fragment_settings_new) {

    private lateinit var storage: QuoteStorage

    private val binding: FragmentSettingsNewBinding by viewBinding(FragmentSettingsNewBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storage = QuoteStorage.getInstance(context)

        // --- text font --- //
//        binding.autoCompleteFont.setOnClickListener {
//            // Create menu for selecting a new canteen
//            PopupMenu(binding.root.context, binding.textLayoutFont).apply {
//                setOnMenuItemClickListener { item ->
//                    item?.itemId?.let { id ->
//                        storage.setTypefaceStyle(id)
//                    }
//
//                    // Update the preview
//                    updatePreview()
//                    true
//                }
//                menu.add(0, 0, 0, getString(R.string.sansserif))
//                menu.add(1, 1, 1, getString(R.string.serif))
//                menu.add(2, 2, 2, getString(R.string.monospace))
//                show()
//            }
//        }

//        val fontAdapter = ArrayAdapter(
//            context,
//            R.layout.item_dropdown_menu,
//            resources.getStringArray(R.array.text_fonts),
//        )
//
//        binding.autoCompleteFont.setAdapter(fontAdapter)

        binding.autoCompleteFont
            .setText(context.resources.getStringArray(R.array.text_fonts)[storage.getTypefaceIndex()])

        binding.autoCompleteFont.setOnClickListener {
            PopupMenu(binding.root.context, binding.autoCompleteFont).apply {
                setOnMenuItemClickListener { item ->
                    item?.itemId?.let { id ->
                        val index = when (id) {
                            R.id.serif -> 1
                            R.id.monospace -> 2
                            else -> 0 // sans-serif
                        }
                        storage.setTypefaceIndex(index)

                        binding.autoCompleteFont
                            .setText(context.resources.getStringArray(R.array.text_fonts)[index])
                    }

                    // Refresh the canteen menu for the newly selected canteen
                    updatePreview()
                    true
                }
                inflate(R.menu.list_fonts)
                show()
            }
        }

        // --- font style --- //
        binding.toggleGroupFont.apply {
            check(when (storage.getTypefaceStyle()) {
                1 -> R.id.toggle_button_font_bold
                2 -> R.id.toggle_button_font_italic
                3 -> R.id.toggle_button_font_bold_italic
                else -> R.id.toggle_button_font_default
            })

            addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (isChecked) {
                    storage.setTypefaceStyle(when (checkedId) {
                        R.id.toggle_button_font_bold -> 1
                        R.id.toggle_button_font_italic -> 2
                        R.id.toggle_button_font_bold_italic -> 3
                        else -> 0
                    })

                    updatePreview()
                }
            }
        }

        // --- text size --- //
        binding.textSize.text = storage.getInt(SettingsPreference.TEXT_SIZE, 18).toString()

        binding.buttonSizeDecreaseFast.setOnClickAction(
            4,
            256,
            isPositive = false,
            isFast = true,
            binding.textSize,
            SettingsPreference.TEXT_SIZE,
        )
        binding.buttonSizeDecrease.setOnClickAction(
            4,
            256,
            isPositive = false,
            isFast = false,
            binding.textSize,
            SettingsPreference.TEXT_SIZE,
        )
        binding.buttonSizeIncrease.setOnClickAction(
            4,
            256,
            isPositive = true,
            isFast = false,
            binding.textSize,
            SettingsPreference.TEXT_SIZE,
        )
        binding.buttonSizeIncreaseFast.setOnClickAction(
            4,
            256,
            isPositive = true,
            isFast = true,
            binding.textSize,
            SettingsPreference.TEXT_SIZE,
        )

        // --- text colour --- //
        setTextColour(storage.getTextColour())

        binding.buttonTextColourDisplay.setOnClickListener {
            showColorPickerDialog(context, R.string.settings_text_colour_dialog_title, true)//, storage.getTextColour())
        }

        // --- highlight style --- //
        binding.toggleGroupHighlightStyle.apply {
            check(checkBackgroundIntensity(storage.getBackgroundType()))

            addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (isChecked) {
                    when (checkedId) {
                        R.id.toggle_button_highlight_background -> {
                            storage.setBackgroundType(0)
                            checkBackgroundIntensity(0)
                        }
                        R.id.toggle_button_highlight_stroke -> {
                            storage.setBackgroundType(1)
                            checkBackgroundIntensity(1)
                        }
                        R.id.toggle_button_highlight_shadow -> {
                            storage.setBackgroundType(2)
                            checkBackgroundIntensity(2)
                        }
                        else -> {
                            storage.setBackgroundType(3)
                            checkBackgroundIntensity(3)
                        } // none
                    }

                    updatePreview()
                }
            }
        }

        // --- highlight intensity --- //
        binding.textIntensity.text = storage.getInt(SettingsPreference.HIGHLIGHT_INTENSITY, 100).toString()

        binding.buttonIntensityDecreaseFast.setOnClickAction(
            0,
            100,
            isPositive = false,
            isFast = true,
            binding.textIntensity,
            SettingsPreference.HIGHLIGHT_INTENSITY,
        )
        binding.buttonIntensityDecrease.setOnClickAction(
            0,
            100,
            isPositive = false,
            isFast = false,
            binding.textIntensity,
            SettingsPreference.HIGHLIGHT_INTENSITY,
        )
        binding.buttonIntensityIncrease.setOnClickAction(
            0,
            100,
            isPositive = true,
            isFast = false,
            binding.textIntensity,
            SettingsPreference.HIGHLIGHT_INTENSITY,
        )
        binding.buttonIntensityIncreaseFast.setOnClickAction(
            0,
            100,
            isPositive = true,
            isFast = true,
            binding.textIntensity,
            SettingsPreference.HIGHLIGHT_INTENSITY,
        )

        // --- highlight colour --- //
        setHighlightColour(storage.getHighlightColour())

        binding.buttonHighlightColourDisplay.setOnClickListener {
            showColorPickerDialog(context, R.string.settings_highlight_colour_dialog_title, false)
        }

        // --- randomise order entries --- //
        binding.switchOrderRandom.apply {
            isChecked = storage.isOrderRandom()

            setOnCheckedChangeListener { _, isChecked ->
                storage.setIsOrderRandom(isChecked)
            }
        }

        // --- widget position (gravity) --- //
        val gravityButtons = listOf(
            binding.buttonTopLeft, binding.buttonTopMiddle, binding.buttonTopRight,
            binding.buttonMiddleLeft, binding.buttonMiddleMiddle, binding.buttonMiddleRight,
            binding.buttonBottomLeft, binding.buttonBottomMiddle, binding.buttonBottomRight
        )

        setGravityButtons(gravityButtons, storage.getWidgetGravity())

        gravityButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                storage.setWidgetGravity(index)
                setGravityButtons(gravityButtons, index)
                updatePreview()
            }
        }

        // Set click listener for showing licences dialogue
        binding.buttonLicences.setOnClickListener {
            openBottomSheet(
                TextSheet().also { sheet ->
                    val bundle = Bundle()
                    bundle.putString("header", getString(R.string.sheet_licences_header))
                    bundle.putString("content", getString(
                        R.string.sheet_licences_content,
                        Calendar.getInstance().get(Calendar.YEAR).toString(),
                    ))
                    sheet.arguments = bundle
                }
            )
        }

        /*
         * Display the app's version as set in the build.gradle. Also display if the app is a
         * development version, and if so, display build timestamp.
         */
        @SuppressLint("SetTextI18n")
        binding.appVersionText.text =
            "${BuildConfig.VERSION_NAME}-${if (BuildConfig.DEBUG) "dev" else "release"}"

        updatePreview()
    }

    private fun setGravityButtons(buttons: List<ImageButton>, gravity: Int) {
        val transparent = context.getColor(android.R.color.transparent)

        buttons.forEach { button ->
            button.setBackgroundColor(transparent)
        }

        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.colorSecContainer, typedValue, true)
        @ColorInt val colorTertiary = typedValue.data

        buttons[gravity].setBackgroundColor(colorTertiary)
    }

    /**
     * Retrieves which button to check for the highlight, and sets the visibility of the intensity
     * modifying layout accordingly.
     */
    private fun checkBackgroundIntensity(index: Int): Int {
        TransitionManager.beginDelayedTransition(binding.root.parent as ViewGroup)
        return when (index) {
            0 -> { // background
                binding.layoutIntensity.visibility = View.GONE
                R.id.toggle_button_highlight_background
            }

            1 -> { // stroke
                binding.layoutIntensity.visibility = View.VISIBLE
                R.id.toggle_button_highlight_stroke
            }

            2 -> { // shadow
                binding.layoutIntensity.visibility = View.VISIBLE
                R.id.toggle_button_highlight_shadow
            }

            else -> { // none
                binding.layoutIntensity.visibility = View.GONE
                R.id.toggle_button_highlight_none
            }
        }
    }

    // TODO horrible, horrible, horrible function. but it works. fix this in the future. please.
    private fun MaterialButton.setOnClickAction(
        min: Int,
        max: Int,
        isPositive: Boolean,
        isFast: Boolean,
        textField: TextView,
        pref: SettingsPreference,
    ) {
        setOnClickListener {
            var value = storage.getInt(pref)
            if (isFast) {
                if (isPositive) {
                    if ((value + 10) > max) {
                        value = max
                    } else {
                        value += 10
                    }
                } else {
                    if ((value - 10) < min) {
                        value = min
                    } else {
                        value -= 10
                    }
                }
            } else {
                if (isPositive && (value != max)) value += 1
                if (!isPositive && (value != min)) value -= 1
            }

            storage.setInt(pref, value)
            textField.text = value.toString()

            updatePreview()
        }
    }

    private fun setTextColour(colour: String) {
        binding.textTextColourCode.text = "#$colour"

        binding.buttonTextColourDisplay.backgroundTintList = ColorStateList.valueOf(
            Color.parseColor("#$colour")
        )
    }

    private fun setHighlightColour(colour: String) {
        binding.textHighlightColourCode.text = "#$colour"

        binding.buttonHighlightColourDisplay.backgroundTintList = ColorStateList.valueOf(
            Color.parseColor("#$colour")
        )
    }

    // TODO this should start with the current colour of the attribute that's to be changed!
    private fun showColorPickerDialog(
        context: Context,
        @StringRes title: Int,
        isText: Boolean,
//        tag: String,
//        colour: String,
    ) {
        ColorPickerDialog.Builder(context)
//        .setInitialColor(Color.parseColor("#$colour"))
//        .setPreferenceName(tag)
//        .setLifecycleOwner(this)
//        .build()
            .setTitle(getString(title))
            .setPositiveButton(getString(R.string.settings_colour_dialog_positive), object : ColorEnvelopeListener {
                override fun onColorSelected(envelope: ColorEnvelope, fromUser: Boolean) {
                    if (isText) {
                        storage.setTextColour(envelope.hexCode)
                        setTextColour(envelope.hexCode)
                    } else {
                        storage.setHighlightColour(envelope.hexCode)
                        setHighlightColour(envelope.hexCode)
                    }
                    updatePreview()
                }})
            .setNegativeButton(getString(R.string.settings_colour_dialog_negative)
            ) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun updatePreview() {

        val bitmap = CanvasText.drawText(context, true)

        binding.textPreview.setImageBitmap(bitmap)
    }
}