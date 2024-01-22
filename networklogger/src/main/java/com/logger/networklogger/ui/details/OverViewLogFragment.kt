package com.logger.networklogger.ui.details

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.logger.LogNavigationViewModel
import com.networklogger.databinding.CustomLogDetailsOverviewBinding
import java.text.DateFormat
import java.text.SimpleDateFormat


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

            val dateFormat: DateFormat = SimpleDateFormat("HH:mm:ss")
            urlTextView.text = Html.fromHtml("<b>URL:</b> " + viewModel.log.value?.url)
            requestMethodTextView.text =
                Html.fromHtml("<b>Request method:</b> " + viewModel.log.value?.requestMethod)
            sslTextView.text = Html.fromHtml("<b>SSL:</b> " + viewModel.log.value?.is_ssl)
            statusTextView.text =
                Html.fromHtml("<b>Status code:</b> " + viewModel.log.value?.statusCode)
            try {
                requestTimeTextView.text =
                    Html.fromHtml("<b>Request time:</b> " + dateFormat.format(viewModel.log.value?.request_time))
            } catch (e: Exception) {
                Log.e("Invalid date", e.toString())
            }
            try {
                responseTimeTextView.text =
                    Html.fromHtml("<b>Response time:</b> " + dateFormat.format(viewModel.log.value?.response_time))
            } catch (e: Exception) {
                Log.e("Invalid date", e.toString())
            }
            tlsTextView.text =
                Html.fromHtml("<b>TLS version:</b> " + viewModel.log.value?.tls_version)
            requestsizeTextView.text =
                Html.fromHtml("<b>Request size:</b> " + viewModel.log.value?.resquest_size)
            requestsizeTextView.isVisible = !viewModel.log.value?.requestMethod.equals("get", true)
            responseSizeTextView.text =
                Html.fromHtml("<b>response size:</b> " + viewModel.log.value?.response_size)

            val duration = (viewModel.log.value?.response_time?.minus(
                viewModel.log.value?.request_time ?: 0
            ))?:0
        }
    }
}