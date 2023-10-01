package com.networklogger

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView

class LogsListAdapter(val onCompanySelected: (position: Int) -> Unit) :
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
        holder.companyItem.text = listdata[position].requestMethod
        holder.subTitle.text = listdata[position].url
        holder.timeStamp.text = listdata[position].request_time
        holder.itemView.setOnClickListener {
            onCompanySelected.invoke(position)
        }
        if (listdata[position].statusCode != "200") {
            holder.companyItem.setTextColor(
                ResourcesCompat.getColor(
                    holder.companyItem.resources,
                    R.color.red,
                    null
                )
            )
            holder.subTitle.setTextColor(
                ResourcesCompat.getColor(
                    holder.companyItem.resources,
                    R.color.red,
                    null
                )
            )
            holder.timeStamp.setTextColor(
                ResourcesCompat.getColor(
                    holder.companyItem.resources,
                    R.color.red,
                    null
                )
            )
        } else {
            holder.companyItem.setTextColor(
                ResourcesCompat.getColor(
                    holder.companyItem.resources,
                    R.color.green,
                    null
                )
            )
            holder.subTitle.setTextColor(
                ResourcesCompat.getColor(
                    holder.companyItem.resources,
                    R.color.green,
                    null
                )
            )
            holder.timeStamp.setTextColor(
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
        var subTitle: TextView
        var timeStamp: TextView

        init {
            companyItem = itemView.findViewById(R.id.logsItem)
            subTitle = itemView.findViewById(R.id.logsItemSubtitlw)
            timeStamp = itemView.findViewById(R.id.timeStamp)
        }
    }
}