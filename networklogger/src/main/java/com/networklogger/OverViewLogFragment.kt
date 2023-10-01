package com.networklogger

import android.graphics.Typeface
import android.os.Bundle
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.networklogger.databinding.CustomLogDetailsOverviewBinding
import com.networklogger.databinding.CustomLogDetailsReqResBinding
import org.json.JSONObject

class OverViewLogFragment : Fragment() {
    private var _binding: CustomLogDetailsOverviewBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<LogNavigationViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CustomLogDetailsOverviewBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            urlTextView.text = Html.fromHtml("<b>URL:</b> "+viewModel.log.value?.url)
            requestMethodTextView.text = Html.fromHtml("<b>Request method:</b> "+viewModel.log.value?.requestMethod)
            sslTextView.text = Html.fromHtml("<b>SSL:</b> "+viewModel.log.value?.is_ssl)
            statusTextView.text = Html.fromHtml("<b>Status code:</b> "+viewModel.log.value?.statusCode)
            requestTimeTextView.text = Html.fromHtml("<b>Request time:</b> "+viewModel.log.value?.request_time)
            responseTimeTextView.text = Html.fromHtml("<b>Response time:</b> "+viewModel.log.value?.response_time)
            tlsTextView.text = Html.fromHtml("<b>TLS version:</b> "+viewModel.log.value?.tls_version)
            requestsizeTextView.text = Html.fromHtml("<b>Request size:</b> "+viewModel.log.value?.resquest_size)
            responseSizeTextView.text = Html.fromHtml("<b>response size:</b> "+viewModel.log.value?.response_size)
        }
    }
}