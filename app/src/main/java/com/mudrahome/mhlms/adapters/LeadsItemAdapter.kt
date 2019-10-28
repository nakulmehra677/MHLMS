package com.mudrahome.mhlms.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.mudrahome.mhlms.R
import com.mudrahome.mhlms.fragments.LeadDetailsFragment
import com.mudrahome.mhlms.model.LeadDetails
import com.mudrahome.mhlms.model.OfferDetails
import com.mudrahome.mhlms.viewHolders.LeadListViewHolder
import com.mudrahome.mhlms.viewHolders.OfferViewHolder
import java.sql.Date
import java.util.*


class LeadsItemAdapter(
    private val items: MutableList<Any>,
    private val context: Context, /* Set<String>*/
    private val currentUserType: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private val OFFER = 0
    private val LEADS = 1

    private val exampleFilter = object : Filter() {

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterList = ArrayList<Any>()

            if (constraint == null || constraint.length == 0) {
                filterList.addAll(items)
            } else {
                val filterPattern = constraint.toString().toLowerCase().trim { it <= ' ' }
                for (item in items) {
                    val details = item as LeadDetails
                    if (details.name!!.toLowerCase().contains(filterPattern) ||
                        details.assignedTo!!.toLowerCase().contains(filterPattern) ||
                        details.assigner!!.toLowerCase().contains(filterPattern) ||
                        details.location!!.toLowerCase().contains(filterPattern) ||
                        details.status!!.toLowerCase().contains(filterPattern)
                    ) {
                        filterList.add(details)
                    }
                }
            }

            val results = FilterResults()
            results.values = filterList

            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            items.clear()
            if (results.values != null)
                items.addAll(listOf(results.values as List<*>))
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder
        val inflater = LayoutInflater.from(parent.context)

        viewHolder = when (viewType) {
            OFFER -> {
                val v2 = inflater.inflate(R.layout.offer_item, parent, false)
                OfferViewHolder(v2)
            }

            else -> {
                val v1 = inflater.inflate(R.layout.list_lead_item, parent, false)
                LeadListViewHolder(v1)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {

        when (holder.itemViewType) {
            OFFER -> {
                val vh1 = holder as OfferViewHolder
                val offerDetails = items[i] as OfferDetails

                vh1.offerTitle.text = offerDetails.title
                vh1.offerDescription.text = offerDetails.description
            }

            else -> {
                val vh2 = holder as LeadListViewHolder
                val model = items[i] as LeadDetails
                vh2.status.text = model.status

                if (model.status!!.matches("Closed".toRegex())) {
                    vh2.status.setTextColor(Color.RED)
                } else {
                    vh2.status.setTextColor(context.resources.getColor(R.color.colorPrimary))
                }

                vh2.name.text = model.name
                vh2.loanAmount.text = model.loanAmount
                vh2.loanType.text = model.loanType
                vh2.location.text = model.location

                vh2.date.text = Date(model.timeStamp).toString()

                if (currentUserType == context.getString(R.string.telecaller) || currentUserType == context.getString(
                        R.string.teleassigner
                    )
                )
                    vh2.assign.text = model.assignedTo
                else if (currentUserType == context.getString(R.string.salesman)) {
                    vh2.assign.text = model.assigner
                } else if (currentUserType == context.getString(R.string.business_associate)) {
                    vh2.assign.text = model.assigner
                } else
                    vh2.assign.visibility = View.GONE

                vh2.itemView.setOnClickListener {
                    val leadDetailsFragment = LeadDetailsFragment(model, currentUserType)
                    leadDetailsFragment.show(
                        (context as AppCompatActivity).supportFragmentManager,
                        "f"
                    )
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return this.items.size
    }

    override fun getFilter(): Filter {
        return exampleFilter
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position] is OfferDetails) {
            OFFER
        } else {
            LEADS
        }
    }
}