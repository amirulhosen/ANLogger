package com.logger.networklogger.ui.details

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.logger.LogNavigationViewModel
import com.logger.observers.EventObserver
import com.networklogger.R
import com.networklogger.databinding.CustomLogDetailViewDialogBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class LogDetailViewFragment : Fragment() {
    private lateinit var binding: CustomLogDetailViewDialogBinding
    private val viewModel by activityViewModels<LogNavigationViewModel>()
    private val detailsViewModel: LogDetailsViewmodel by viewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = CustomLogDetailViewDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        val adapter = ViewPagerAdapter(parentFragmentManager, lifecycle)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabsArray[position]
        }.attach()

        detailsViewModel.logDetailsResponse.observe(viewLifecycleOwner, EventObserver {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_STREAM,
                FileProvider.getUriForFile(
                    requireContext(),
                    "${context?.packageName}.provider",
                    it
                ))
            startActivity(Intent.createChooser(shareIntent, "Share using"))
        })

        binding.toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.share_menu) {
                detailsViewModel.logDetailsRequest.postValue(viewModel.logPosition.value ?: 0)
            }
            return@setOnMenuItemClickListener false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.details_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    val tabsArray = arrayOf(
        "Overview",
        "Request",
        "Response"
    )
}