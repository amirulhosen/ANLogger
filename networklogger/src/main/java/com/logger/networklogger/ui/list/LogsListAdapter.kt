package com.logger.networklogger.ui.list

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.logger.networklogger.domain.model.LogDataModel
import com.networklogger.R
import java.text.DateFormat
import java.text.SimpleDateFormat

class LogsListAdapter(val onCompanySelected: (position: Long, index: Int) -> Unit) :
    RecyclerView.Adapter<LogsListAdapter.ViewHolder>() {
    internal var listdata = listOf<LogDataModel.Log>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val root = LayoutInflater.from(parent.context)
            .inflate(R.layout.logging_list_item, parent, false)
        return ViewHolder(root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.methodTextView.text = listdata[position].requestMethod
        holder.statusTextView.text = listdata[position].statusCode
        holder.companyItem.text = listdata[position].url

        try {
            val dateFormat: DateFormat = SimpleDateFormat("HH:mm:ss")
            holder.timeStamp.text = dateFormat.format(listdata[position].request_time)
        } catch (e: Exception) {
            Log.e("Invalid date", e.toString())
        }
        holder.itemView.setOnClickListener {
            onCompanySelected.invoke(listdata[position].uid, position)
        }
        if (listdata[position].statusCode != "200") {
            holder.leftLayout.setBackgroundColor(
                ResourcesCompat.getColor(
                    holder.leftLayout.resources,
                    R.color.red,
                    null
                )
            )
            holder.companyItem.setTextColor(
                ResourcesCompat.getColor(
                    holder.companyItem.resources,
                    R.color.red,
                    null
                )
            )
        } else {
            holder.leftLayout.setBackgroundColor(
                ResourcesCompat.getColor(
                    holder.leftLayout.resources,
                    R.color.green,
                    null
                )
            )
            holder.companyItem.setTextColor(
                ResourcesCompat.getColor(
                    holder.companyItem.resources,
                    R.color.green,
                    null
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return listdata.count()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var companyItem: TextView
        var timeStamp: TextView
        var leftLayout: LinearLayout
        var methodTextView: TextView
        var statusTextView: TextView

        init {
            companyItem = itemView.findViewById(R.id.logsItem)
            timeStamp = itemView.findViewById(R.id.timeStamp)
            leftLayout = itemView.findViewById(R.id.leftLayout)
            methodTextView = itemView.findViewById(R.id.methodTextView)
            statusTextView = itemView.findViewById(R.id.statusTextView)
        }
    }
}