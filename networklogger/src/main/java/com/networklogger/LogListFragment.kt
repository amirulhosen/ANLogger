package com.networklogger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.networklogger.databinding.CustomLogListFragmentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.util.Calendar


class LogListFragment : Fragment() {
    private val TAG = "CustomLogViewDialog"
    private lateinit var jsonObject: LogDataModel
    private var logsListAdapter: LogsListAdapter? = null
    private var file: File? = null
    private val viewModel by activityViewModels<LogNavigationViewModel>()
    private lateinit var binding: CustomLogListFragmentBinding

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
            checkAndDelete()
            (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
            CoroutineScope(Dispatchers.IO).launch {

                jsonObject = loadDataFromFile()
                CoroutineScope(Dispatchers.Main).launch {
                    if (jsonObject.logs.isEmpty()) {
                        binding.emptyImageView.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                        return@launch
                    }
                    logsListAdapter = LogsListAdapter {
                        viewModel.log.value =
                            jsonObject.logs.sortedByDescending { it.request_time }[it]
                        viewModel.logPosition.value = it
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
                        jsonObject.logs.sortedByDescending { it.request_time }
                    logsListAdapter?.notifyDataSetChanged()
                    findViewById<RecyclerView>(R.id.recyclerView).apply {
                        layoutManager = LinearLayoutManager(context)
                        this.adapter = logsListAdapter
                    }
                }
            }
            binding.toolbar.setNavigationOnClickListener {
                requireActivity().finish()
            }

        }

        binding.toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.share_menu) {
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
            } else if (it.itemId == R.id.delete_menu) {
                val deleteDialog = android.app.AlertDialog.Builder(context).run {
                    setMessage("Are you sure want to delete?")
                    setTitle("Confirm")
                    setCancelable(true)
                    setPositiveButton(
                        android.R.string.ok
                    ) { dialog, which ->
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
                deleteDialog.show()
            } else if (it.itemId == R.id.settings_menu) {
                startActivity(Intent(requireActivity(),ConfigurationEditorActivity::class.java))
            }
            return@setOnMenuItemClickListener false
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

    private fun checkAndDelete(){
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireActivity())
        val file = File(
            (requireContext().getExternalFilesDir("")?.absolutePath
                ?: "") + "/network_logs"
        )

        val createdAt: Long = file.lastModified()
        val periodSeconds: Long = (Calendar.getInstance().timeInMillis - createdAt) / 1000
        val elapsedDays = periodSeconds / 60 / 60 / 24

        if(prefs.getString("clean_period_list","")?.equals("three_day") == true && elapsedDays >=3){
            file.delete()
        }else if(prefs.getString("clean_period_list","")?.equals("two_day") == true && elapsedDays >=2){
            file.delete()
        }else if(prefs.getString("clean_period_list","")?.equals("one_day") == true && elapsedDays >=1){
            file.delete()
        } else if (prefs.getString("clean_period_list", "")
                ?.equals("") == true
        ) {
            file.delete()
        }
        else{
            Log.i("","Ignore")
        }
    }

}