package com.denizd.textbasic.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.denizd.textbasic.BuildConfig
import com.denizd.textbasic.R
import com.denizd.textbasic.databinding.FragmentSettingsNewBinding
import com.denizd.textbasic.db.QuoteStorage
import com.denizd.textbasic.util.SettingsPreference
import com.denizd.textbasic.util.viewBinding
import com.google.android.material.button.MaterialButton

class SettingsNewFragment : BaseFragment(R.layout.fragment_settings_new) {

    private lateinit var storage: QuoteStorage

    private val binding: FragmentSettingsNewBinding by viewBinding(FragmentSettingsNewBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storage = QuoteStorage.getInstance(context)

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

        // --- text transparency --- //
        binding.textTransparency.text = storage.getInt(SettingsPreference.TEXT_TRANSPARENCY, 100).toString()

        binding.buttonTextTransparencyDecreaseFast.setOnClickAction(
            0,
            100,
            isPositive = false,
            isFast = true,
            binding.textTransparency,
            SettingsPreference.TEXT_TRANSPARENCY,
        )
        binding.buttonTextTransparencyDecrease.setOnClickAction(
            0,
            100,
            isPositive = false,
            isFast = false,
            binding.textTransparency,
            SettingsPreference.TEXT_TRANSPARENCY,
        )
        binding.buttonTextTransparencyIncrease.setOnClickAction(
            0,
            100,
            isPositive = true,
            isFast = false,
            binding.textTransparency,
            SettingsPreference.TEXT_TRANSPARENCY,
        )
        binding.buttonTextTransparencyIncreaseFast.setOnClickAction(
            0,
            100,
            isPositive = true,
            isFast = true,
            binding.textTransparency,
            SettingsPreference.TEXT_TRANSPARENCY,
        )

        // --- highlight style --- //
        binding.toggleGroupHighlightStyle.apply {
            check(when (storage.getBackgroundType()) {
                0 -> R.id.toggle_button_highlight_background
                1 -> R.id.toggle_button_highlight_stroke
                2 -> R.id.toggle_button_highlight_shadow
                else -> R.id.toggle_button_highlight_none // 3
            })

            addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (isChecked) {
                    storage.setBackgroundType(when (checkedId) {
                        R.id.toggle_button_highlight_background -> 0
                        R.id.toggle_button_highlight_stroke -> 1
                        R.id.toggle_button_highlight_shadow -> 2
                        else -> 3 // none
                    })
                }
            }
        }

        // --- text highlight transparency --- //
        binding.textHighlightTransparency.text = storage.getInt(SettingsPreference.HIGHLIGHT_TRANSPARENCY, 100).toString()

        binding.buttonHighlightTransparencyDecreaseFast.setOnClickAction(
            0,
            100,
            isPositive = false,
            isFast = true,
            binding.textHighlightTransparency,
            SettingsPreference.HIGHLIGHT_TRANSPARENCY,
        )
        binding.buttonHighlightTransparencyDecrease.setOnClickAction(
            0,
            100,
            isPositive = false,
            isFast = false,
            binding.textHighlightTransparency,
            SettingsPreference.HIGHLIGHT_TRANSPARENCY,
        )
        binding.buttonHighlightTransparencyIncrease.setOnClickAction(
            0,
            100,
            isPositive = true,
            isFast = false,
            binding.textHighlightTransparency,
            SettingsPreference.HIGHLIGHT_TRANSPARENCY,
        )
        binding.buttonHighlightTransparencyIncreaseFast.setOnClickAction(
            0,
            100,
            isPositive = true,
            isFast = true,
            binding.textHighlightTransparency,
            SettingsPreference.HIGHLIGHT_TRANSPARENCY,
        )

        // --- randomise order entries --- //
        binding.switchOrderRandom.apply {
            isChecked = storage.isOrderRandom()

            setOnCheckedChangeListener { _, isChecked ->
                storage.setIsOrderRandom(isChecked)
            }
        }

        /*
         * Display the app's version as set in the build.gradle. Also display if the app is a
         * development version, and if so, display build timestamp.
         */
        @SuppressLint("SetTextI18n")
        binding.appVersionText.text =
            "${BuildConfig.VERSION_NAME}-${if (BuildConfig.DEBUG) "dev" else "release"}"
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
        }
    }
}