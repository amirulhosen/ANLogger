package com.logger.networklogger.ui.list

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.logger.LogNavigationViewModel
import com.logger.networklogger.api.SuppressApi.isSqliteEnable
import com.logger.networklogger.domain.model.LogDataModel
import com.logger.networklogger.domain.model.ShareType
import com.logger.networklogger.ui.CustomProgressDialog
import com.logger.networklogger.ui.settings.ConfigurationEditorActivity
import com.logger.observers.EventObserver
import com.networklogger.R
import com.networklogger.databinding.CustomLogListFragmentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.BufferedReader
import java.io.File
import java.io.FileReader


class LogListFragment : Fragment() {
    private lateinit var jsonObject: LogDataModel
    private var logsListAdapter: LogsListAdapter? = null
    private var file: File? = null
    private val viewModel by activityViewModels<LogNavigationViewModel>()
    private lateinit var binding: CustomLogListFragmentBinding
    private val logListViewModel: LogListViewmodel by viewModel()
    private var customProgressDialog: CustomProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        binding = CustomLogListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            customProgressDialog = CustomProgressDialog(requireContext())
            (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
            binding.toolbar.setNavigationOnClickListener {
                requireActivity().finish()
            }
        }
        customProgressDialog?.show()
        logListViewModel.logListRequest.postValue(Unit)
        handleLiveData(view)
        manageToolbarActions()
    }

    private fun manageToolbarActions() {
        binding.toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.share_menu) {
                if (isSqliteEnable) {
                    customProgressDialog?.show()
                    val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                    builder
                        .setTitle("Share as")
                        .setItems(arrayOf("Json", "SQLite")) { dialog, which ->
                            if (which == 0)
                                logListViewModel.logGsonRequest.postValue(ShareType.JSON.type)
                            else
                                logListViewModel.logGsonRequest.postValue(ShareType.SQLITE.type)
                        }

                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                } else {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        if (file != null)
                            putExtra(
                                Intent.EXTRA_STREAM,
                                FileProvider.getUriForFile(
                                    requireContext(),
                                    requireContext().applicationContext.packageName + ".provider",
                                    file!!
                                )
                            )
                        type = "text/html"
                    }
                    requireContext().startActivity(Intent.createChooser(intent, "Share using"))
                }
            } else if (it.itemId == R.id.delete_menu) {
                val deleteDialog = AlertDialog.Builder(context).run {
                    setMessage("Are you sure want to delete?")
                    setTitle("Confirm")
                    setCancelable(true)
                    setPositiveButton(
                        android.R.string.ok
                    ) { dialog, which ->
                        if (isSqliteEnable) {
                            customProgressDialog?.show()
                            logListViewModel.deleteLogRequest.postValue(Unit)
                        } else {
                            val file = File(
                                (requireContext().getExternalFilesDir("")?.absolutePath
                                    ?: "") + "/network_logs"
                            )
                            val isDeleted = file.delete()
                            if (isDeleted) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    binding.emptyImageView.isVisible = true
                                    binding.recyclerView.isVisible = false
                                    logsListAdapter?.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                }
                deleteDialog.show()
            } else if (it.itemId == R.id.settings_menu) {
                startActivity(Intent(requireActivity(), ConfigurationEditorActivity::class.java))
            }
            return@setOnMenuItemClickListener false
        }
    }

    private fun handleLiveData(view: View) {
        logListViewModel.run {
            logJsonResponse.observe(viewLifecycleOwner, EventObserver {
                customProgressDialog?.dismiss()
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.type = "application/txt"
                shareIntent.putExtra(
                    Intent.EXTRA_STREAM,
                    FileProvider.getUriForFile(
                        requireContext(),
                        "${context?.packageName}.provider",
                        it
                    )
                )
                startActivity(Intent.createChooser(shareIntent, "Share using"))
            })
            deleteLogResponse.observe(viewLifecycleOwner, EventObserver {
                customProgressDialog?.dismiss()
                binding.emptyImageView.isVisible = true
                binding.recyclerView.isVisible = false
                logsListAdapter?.notifyDataSetChanged()
            })
            logListResponse.observe(viewLifecycleOwner) { logList ->
                customProgressDialog?.dismiss()
                jsonObject = logList
                if (logList.logs.isEmpty()) {
                    binding.emptyImageView.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                }
                logsListAdapter = LogsListAdapter { position, index ->
                    viewModel.log.value =
                        logList.logs.sortedByDescending { it.request_time }[index]
                    viewModel.logPosition.value = position
                    findNavController().navigate(R.id.actionContactInfo)
                }
                val itemDecorator = DividerItemDecorator(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.list_item_divider
                    )
                )
                binding.recyclerView.addItemDecoration(itemDecorator)
                logsListAdapter?.listdata =
                    logList.logs.sortedByDescending { it.request_time }
                logsListAdapter?.notifyDataSetChanged()
                view.findViewById<RecyclerView>(R.id.recyclerView).apply {
                    layoutManager = LinearLayoutManager(context)
                    this.adapter = logsListAdapter
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private suspend fun loadDataFromFile(): LogDataModel {
        try {
            val file = File(
                (requireContext().getExternalFilesDir("")?.absolutePath ?: "") + "/network_logs"
            )
            val fileReader = FileReader(file)
            val bufferedReader = BufferedReader(fileReader)
            val stringBuilder = StringBuilder()
            var line = bufferedReader.readLine()
            while (line != null) {
                stringBuilder.append(line).append("\n")
                line = bufferedReader.readLine()
            }
            bufferedReader.close()
            val jsonData = Gson().fromJson(stringBuilder.toString(), LogDataModel::class.java)
            return jsonData
        } catch (ex: Exception) {
            return LogDataModel(emptyList())
        }
    }
}