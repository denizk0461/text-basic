package com.denizd.textbasic.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.transition.TransitionManager
import com.denizd.textbasic.BuildConfig
import com.denizd.textbasic.R
import com.denizd.textbasic.adapter.DropdownAdapter
import com.denizd.textbasic.databinding.FragmentSettingsNewBinding
import com.denizd.textbasic.db.QuoteStorage
import com.denizd.textbasic.sheet.TextSheet
import com.denizd.textbasic.util.getConstrast
import com.denizd.textbasic.util.showSnackBar
import com.denizd.textbasic.util.viewBinding
import com.denizd.textbasic.widget.CanvasText
import com.google.gson.Gson
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class SettingsNewFragment : BaseFragment(R.layout.fragment_settings_new) {

    private lateinit var storage: QuoteStorage

    private val binding: FragmentSettingsNewBinding by viewBinding(FragmentSettingsNewBinding::bind)

    private lateinit var resultIntent: ActivityResultLauncher<Intent>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storage = QuoteStorage.getInstance(context)

        // --- text font --- //
        val fontAdapter = DropdownAdapter(
            context,
            context.resources.getStringArray(R.array.text_fonts).toList(),
        )

        binding.autoCompleteFont.setAdapter(fontAdapter)
        binding.autoCompleteFont.setText(
            fontAdapter.getItem(storage.getTypefaceIndex()).toString(),
            false,
        )

        binding.autoCompleteFont.setOnItemClickListener { _, _, position, _ ->
            storage.setTypefaceIndex(position)
            binding.autoCompleteFont.setText(fontAdapter.getItem(position), false)
            updatePreview()
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
        binding.textSize.text = storage.getTextSize().toString()

        binding.buttonSizeDecreaseFast.setOnClickListener {
            storage.setTextSize(getValueInBoundary(
                binding.textSize.text.toString().toInt(),
                4,
                256,
                -10,
            ))
            binding.textSize.text = storage.getTextSize().toString()
            updatePreview()
        }
        binding.buttonSizeDecrease.setOnClickListener {
            storage.setTextSize(getValueInBoundary(
                binding.textSize.text.toString().toInt(),
                4,
                256,
                -1,
            ))
            binding.textSize.text = storage.getTextSize().toString()
            updatePreview()
        }
        binding.buttonSizeIncrease.setOnClickListener {
            storage.setTextSize(getValueInBoundary(
                binding.textSize.text.toString().toInt(),
                4,
                256,
                1,
            ))
            binding.textSize.text = storage.getTextSize().toString()
            updatePreview()
        }
        binding.buttonSizeIncreaseFast.setOnClickListener {
            storage.setTextSize(getValueInBoundary(
                binding.textSize.text.toString().toInt(),
                4,
                256,
                10,
            ))
            binding.textSize.text = storage.getTextSize().toString()
            updatePreview()
        }

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
        binding.textIntensity.text = storage.getHighlightIntensity().toString()

        binding.buttonIntensityDecreaseFast.setOnClickListener {
            storage.setHighlightIntensity(getValueInBoundary(
                binding.textIntensity.text.toString().toInt(),
                0,
                100,
                -10,
            ))
            binding.textIntensity.text = storage.getHighlightIntensity().toString()
            updatePreview()
        }
        binding.buttonIntensityDecrease.setOnClickListener {
            storage.setHighlightIntensity(getValueInBoundary(
                binding.textIntensity.text.toString().toInt(),
                0,
                100,
                -1,
            ))
            binding.textIntensity.text = storage.getHighlightIntensity().toString()
            updatePreview()
        }
        binding.buttonIntensityIncrease.setOnClickListener {
            storage.setHighlightIntensity(getValueInBoundary(
                binding.textIntensity.text.toString().toInt(),
                0,
                100,
                1,
            ))
            binding.textIntensity.text = storage.getHighlightIntensity().toString()
            updatePreview()
        }
        binding.buttonIntensityIncreaseFast.setOnClickListener {
            storage.setHighlightIntensity(getValueInBoundary(
                binding.textIntensity.text.toString().toInt(),
                0,
                100,
                10,
            ))
            binding.textIntensity.text = storage.getHighlightIntensity().toString()
            updatePreview()
        }

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

        binding.buttonImport.setOnClickListener {
            importFile()
        }

        resultIntent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            try {
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    data?.let {
                        it.data?.let { u ->
//                            val s = BufferedReader(FileReader(File(u.path ?: throw IllegalArgumentException()))).readLines().joinToString()
                            val a = Gson().fromJson(FileReader(getFile(getApplicationContext(), u)), Array<String>::class.java)
                            Log.d("TAGSARELOST", a[0])
                        }
                    }
                }
            } catch (e: IllegalArgumentException) {

            }
        }

        binding.buttonExport.setOnClickListener {
            val date = SimpleDateFormat("yyyyMMddHHmmss", Locale.ROOT).format(Date(System.currentTimeMillis())).run {
                this.substring(0, 8) + "T" + this.substring(8)
            }
            val fileName = getString(R.string.export_file_name, date)
            Log.d("TAGSARELOST", fileName)
            createFile(fileName, "hello test")
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

    @Throws(IOException::class)
    fun getFile(context: Context, uri: Uri): File {
        val destinationFilename =
            File(context.filesDir.path + File.separatorChar + queryName(context, uri))
        try {
            context.contentResolver.openInputStream(uri).use { ins ->
                createFileFromStream(
                    ins,
                    destinationFilename
                )
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return destinationFilename
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

    /**
     * Adds a step to a numeric value while staying within bounds as defined by min and max.
     *
     * @param value value to change
     * @param min   minimum boundary
     * @param max   maximum boundary
     * @param step  step to add to the given value; may be positive or negative
     * @return      new value with step added, or boundary if it would have been exceeded otherwise
     */
    private fun getValueInBoundary(value: Int, min: Int, max: Int, step: Int): Int = when {
        // If the new value exceeds the maximum value, return max
        (value + step) > max -> max
        // If the new value is below the minimum value, return min
        (value + step) < min -> min
        // Return the value with the step added
        else -> value + step
    }

    private fun setTextColour(colour: String) {
        binding.textTextColourCode.text = "#$colour"

        val newColour = Color.parseColor("#$colour")
        val contrast = newColour.getConstrast()

        binding.buttonTextColourDisplay.backgroundTintList = ColorStateList.valueOf(
            newColour
        )

        binding.buttonTextColourDisplay.setTextColor(contrast)
        binding.buttonTextColourDisplay.iconTint = ColorStateList.valueOf(contrast)
    }

    private fun setHighlightColour(colour: String) {
        binding.textHighlightColourCode.text = "#$colour"

        val newColour = Color.parseColor("#$colour")
        val contrast = newColour.getConstrast()

        binding.buttonHighlightColourDisplay.backgroundTintList = ColorStateList.valueOf(newColour)

        binding.buttonHighlightColourDisplay.setTextColor(contrast)
        binding.buttonHighlightColourDisplay.iconTint = ColorStateList.valueOf(contrast)
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

    private fun importFile() {
        var intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/json"
        intent = Intent.createChooser(intent, "Choose file")
        resultIntent.launch(intent)
    }

    private fun createFile(fileName: String, content: String) {
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        try {
            val writer = BufferedWriter(FileWriter(File(path, fileName)))
            writer.write(content)
            writer.close()
            context.theme.showSnackBar(
                binding.coordinatorLayout,
                getString(R.string.settings_fragment_export_successful, fileName),
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun updatePreview() {
        val bitmap = CanvasText.drawText(context, true)
        binding.textPreview.setImageBitmap(bitmap)
    }
}