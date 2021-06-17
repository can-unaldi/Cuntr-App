package com.canunaldi.cuntrapp.discover

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.canunaldi.cuntrapp.R
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class DiscoverAdapter(private val itemList: ArrayList<DiscoverItemModel>,
                      private val listener: OnItemClickListener) : RecyclerView.Adapter<DiscoverAdapter.ItemHolder>(),Filterable   {
    var filteredItemList : ArrayList<DiscoverItemModel> = ArrayList()
    init {
        filteredItemList=itemList
    }
    inner class ItemHolder(view : View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        var itemType : TextView?=null
        var itemImg : ImageView?=null
        var itemTitle : TextView?=null

        init {
            itemType=view.findViewById(R.id.txtRowType)
            itemImg=view.findViewById(R.id.userFollowImg)
            itemTitle=view.findViewById(R.id.txtUserFollow)

            view.setOnClickListener(this)

            view.findViewById<ImageButton>(R.id.btnUserFollow).setOnClickListener(View.OnClickListener {
                val position=adapterPosition
                if (position!=RecyclerView.NO_POSITION){
                    listener.onFollowClicked(filteredItemList[position].itemId)
                }
            })
        }

        override fun onClick(v: View?) {
            val position=adapterPosition
            if (position!=RecyclerView.NO_POSITION){
                listener.onItemClicked(filteredItemList[position].itemId)
            }
        }
    }
    interface OnItemClickListener{
        fun onItemClicked(position: String)
        fun onFollowClicked(position: String)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        var inflater= LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.discover_recycler_row,parent,false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val currentItem=filteredItemList[position]
        holder.itemType?.text=currentItem.itemType
        if (currentItem.itemImgUrl.isEmpty()&&currentItem.itemType=="Açık Cuntr"){
            Picasso.get().load(R.drawable.ic_cuntr).into(holder.itemImg)

        }else{
            Picasso.get().load(currentItem.itemImgUrl).into(holder.itemImg)
        }
        holder.itemTitle?.text=currentItem.itemTitle
    }

    override fun getItemCount(): Int {
        return filteredItemList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    filteredItemList=itemList
                } else {
                    val resultList = ArrayList<DiscoverItemModel>()

                    for (row in itemList) {
                        if (row.itemTitle.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    filteredItemList = resultList
                }
                val filterResults = FilterResults()

                filterResults.values = filteredItemList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredItemList = results?.values as ArrayList<DiscoverItemModel>
                notifyDataSetChanged()
            }

        }
    }
    /*fun filterList(filteredItemType : ArrayList<String>,filteredItemImg : ArrayList<String>,filteredItemTitle : ArrayList<String>){
        itemTypeArray=filteredItemType
        itemImgArray=filteredItemImg
        itemTitleArray=filteredItemTitle
    }*/

}