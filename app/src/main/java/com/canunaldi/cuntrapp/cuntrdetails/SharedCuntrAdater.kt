package com.canunaldi.cuntrapp.cuntrdetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.canunaldi.cuntrapp.R
import com.squareup.picasso.Picasso

class SharedCuntrAdater(private val followerImgUrl: ArrayList<String>,private val followerName: ArrayList<String>) : RecyclerView.Adapter<SharedCuntrAdater.FollowerHolder>() {
    inner class FollowerHolder(view : View) : RecyclerView.ViewHolder(view) {
        var followerImg : ImageView?=null
        var followerName : TextView?=null
        init {
            followerImg=view.findViewById(R.id.imgSharedFollower)
            followerName=view.findViewById(R.id.txtSharedFollower)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowerHolder {
        var inflater= LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.shared_followers_row,parent,false)
        return FollowerHolder(view)
    }

    override fun onBindViewHolder(holder: FollowerHolder, position: Int) {
        if (followerImgUrl[position].isEmpty()){
            Picasso.get().load(R.drawable.baseline_account_circle_18).into(holder.followerImg)

        }else{
            Picasso.get().load(followerImgUrl[position]).into(holder.followerImg)
        }
        holder.followerName?.text=followerName[position]
    }

    override fun getItemCount(): Int {
        return followerName.size
    }
}