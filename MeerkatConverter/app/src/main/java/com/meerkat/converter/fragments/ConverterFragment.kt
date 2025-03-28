package com.meerkat.converter.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.meerkat.converter.databinding.FragmentConverterBinding

class ConverterFragment : Fragment() {
    private var _binding: FragmentConverterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConverterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private lateinit var viewModel: ConverterViewModel
    private lateinit var locationHelper: LocationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ConverterViewModel::class.java]
        locationHelper = LocationHelper(requireContext())
    }

    private fun setupUI() {
        binding.apply {
            // Setup format spinners
            ArrayAdapter.createFromResource(
                requireContext(),
                R.array.coordinate_formats,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerFromFormat.adapter = adapter
                spinnerToFormat.adapter = adapter
            }

            // Setup buttons
            btnConvert.setOnClickListener { convertCoordinates() }
            btnGpsLocation.setOnClickListener { getGpsLocation() }
            btnCopyAll.setOnClickListener { copyAllResults() }
            btnShareAll.setOnClickListener { shareAllResults() }
        }

        observeViewModel()
    }

    private fun convertCoordinates() {
        val input = binding.editInput.text.toString()
        val fromFormat = binding.spinnerFromFormat.selectedItemPosition
        val toFormat = binding.spinnerToFormat.selectedItemPosition

        if (input.isBlank()) {
            showError("Please enter coordinates to convert")
            return
        }

        try {
            val result = CoordinateConverter.convert(
                input,
                CoordinateConverter.Format.values()[fromFormat],
                CoordinateConverter.Format.values()[toFormat]
            )
            binding.textOutput.text = result
            saveToHistory(input, result, fromFormat, toFormat)
        } catch (e: Exception) {
            showError("Invalid input format: ${e.message}")
        }
    }

    private fun getGpsLocation() {
        lifecycleScope.launch {
            val location = locationHelper.getCurrentLocation()
            if (location != null) {
                val latLng = "${location.latitude}, ${location.longitude}"
                binding.editInput.setText(latLng)
                binding.spinnerFromFormat.setSelection(CoordinateConverter.Format.DD.ordinal)
            } else {
                showError("Could not get GPS location")
            }
        }
    }

    private fun saveToHistory(input: String, output: String, from: Int, to: Int) {
        viewModel.saveToHistory(
            CoordinateConverter.createHistoryEntry(
                input,
                output,
                CoordinateConverter.Format.values()[from],
                CoordinateConverter.Format.values()[to]
            )
        )
    }

    private fun copyAllResults() {
        val textToCopy = buildString {
            append("Input: ${binding.editInput.text}\n")
            append("From: ${binding.spinnerFromFormat.selectedItem}\n")
            append("To: ${binding.spinnerToFormat.selectedItem}\n")
            append("Result: ${binding.textOutput.text}")
        }

        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Conversion Result", textToCopy)
        clipboard.setPrimaryClip(clip)
        
        Snackbar.make(binding.root, "Copied to clipboard", Snackbar.LENGTH_SHORT).show()
    }

    private fun shareAllResults() {
        val shareText = buildString {
            append("Coordinate Conversion Result\n\n")
            append("Input: ${binding.editInput.text}\n")
            append("From: ${binding.spinnerFromFormat.selectedItem}\n")
            append("To: ${binding.spinnerToFormat.selectedItem}\n")
            append("Result: ${binding.textOutput.text}")
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun observeViewModel() {
        // TODO: Observe any ViewModel data if needed
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}