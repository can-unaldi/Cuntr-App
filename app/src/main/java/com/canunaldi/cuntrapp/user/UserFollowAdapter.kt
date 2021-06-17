package com.canunaldi.cuntrapp.user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.canunaldi.cuntrapp.R
import com.squareup.picasso.Picasso


class UserFollowAdapter(private val userName: ArrayList<String>,private val userImg: ArrayList<String>, private val followers: Boolean,
                        private val listener: OnItemClickListener) : RecyclerView.Adapter<UserFollowAdapter.UserHolder>() {
    inner class UserHolder (view : View) : RecyclerView.ViewHolder(view), View.OnClickListener{
        var userNameText : TextView?=null
        var txtFollow : TextView?=null
        var userImgView : ImageView?=null
        var userBtn : ImageButton?=null
        init {
            userNameText=view.findViewById(R.id.txtUserFollow)
            userImgView=view.findViewById(R.id.userFollowImg)
            userBtn=view.findViewById(R.id.btnUserFollow)
            txtFollow=view.findViewById(R.id.txtFollow)

            if (followers){
                userBtn!!.setImageResource(R.drawable.ic_follow)
            }
            else{
                userBtn!!.setImageResource(R.drawable.ic_unfollow)
            }
            view.setOnClickListener(this)
            view.findViewById<ImageButton>(R.id.btnUserFollow).setOnClickListener(View.OnClickListener {
                val position=adapterPosition
                if (position!=RecyclerView.NO_POSITION){
                    listener.onFollowClicked(position)
                }
            })
        }
        override fun onClick(v: View?) {
            val position=adapterPosition
            if (position!=RecyclerView.NO_POSITION){
                listener.onItemClicked(position)
            }
        }

    }
    interface OnItemClickListener{
        fun onItemClicked(position: Int)
        fun onFollowClicked(position: Int)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        var inflater= LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.user_follow_row,parent,false)
        return UserHolder(view)
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        holder.userNameText?.text=userName[position]
        if (userImg[position].isEmpty()){
            Picasso.get().load(R.drawable.baseline_account_circle_18).into(holder.userImgView)

        }else{
            Picasso.get().load(userImg[position]).into(holder.userImgView)
        }
    }

    override fun getItemCount(): Int {
        return userName.size
    }
}