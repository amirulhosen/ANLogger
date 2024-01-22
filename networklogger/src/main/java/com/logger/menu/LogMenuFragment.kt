package com.logger.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.networklogger.R
import com.networklogger.databinding.LoggerMenuLayoutBinding

class LogMenuFragment : Fragment() {
    private lateinit var binding: LoggerMenuLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = LoggerMenuLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        binding.apiLoggerButton.setOnClickListener {
            findNavController().navigate(R.id.actionApiLogger)
        }
        binding.timberLoggerButton.setOnClickListener {
            findNavController().navigate(R.id.actionTimberLogger)
        }
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().finish()
        }
    }
}