package com.networklogger

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.networklogger.databinding.CustomLogDetailViewDialogBinding


class LogDetailViewFragment : Fragment() {
    private lateinit var binding: CustomLogDetailViewDialogBinding
    private val viewModel by activityViewModels<LogNavigationViewModel>()


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
            tab.text = animalsArray[position]
        }.attach()

        binding.toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.share_menu) {
                val data = Gson().toJson(viewModel.log.value)
                val shareIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, data)
                    type = "text/plain"
                }
                context?.startActivity(Intent.createChooser(shareIntent, "Share"))
            }
            return@setOnMenuItemClickListener false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.details_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    val animalsArray = arrayOf(
        "Overview",
        "Request",
        "Response"
    )
}