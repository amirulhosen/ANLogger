package com.logger.networklogger.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.logger.LogNavigationViewModel
import com.networklogger.databinding.CustomLogDetailsReqResBinding
import org.json.JSONObject

class RequestLogFragment : Fragment() {
    private var _binding: CustomLogDetailsReqResBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<LogNavigationViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CustomLogDetailsReqResBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            val jsonObject = JSONObject(viewModel.log.value?.requestBody)

            if (jsonObject.toString(4).length > 2000) {
                val chuckenedValue = jsonObject.toString(4).chunked(2000)
                chuckenedValue.forEach {
                    binding.responseTextView.append(it)
                }
            } else
                binding.responseTextView.text = jsonObject.toString(4)
        } catch (e: Exception) {
            binding.responseTextView.text = viewModel.log.value?.requestBody
        }
    }
}