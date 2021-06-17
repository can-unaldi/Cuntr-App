package com.canunaldi.cuntrapp.user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.canunaldi.cuntrapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class UserCuntrRequestAdapter(private val cuntrTitle: ArrayList<String>,private val cuntrTimeLeft: ArrayList<String>,private val cuntrCreator: ArrayList<String>,
                              private val cuntrType:ArrayList<String>,private val cuntrCreatorImg:ArrayList<String>,private val cuntrId:ArrayList<String>,
                              private val listener: OnItemClickListener) :RecyclerView.Adapter<UserCuntrRequestAdapter.RequestHolder>() {
    private  lateinit var auth: FirebaseAuth
    private  lateinit var db: FirebaseFirestore
    inner class RequestHolder(view : View) : RecyclerView.ViewHolder(view) , View.OnClickListener{
        var userCuntrTitleText : TextView?=null
        var userCuntrCreatorText : TextView?=null
        var userCuntrImgView : ImageView?=null
        var acceptBtn: Button?=null
        var rejectBtn: Button?=null
        init {
            userCuntrCreatorText=view.findViewById(R.id.txtInviter)
            userCuntrTitleText=view.findViewById(R.id.txtCuntrTitle)
            userCuntrImgView=view.findViewById(R.id.imgInvater)
            acceptBtn=view.findViewById(R.id.btnAccept)
            rejectBtn=view.findViewById(R.id.btnReject)
            view.setOnClickListener(this)
            view.findViewById<Button>(R.id.btnAccept).setOnClickListener(View.OnClickListener {
                val position=adapterPosition
                if (position!=RecyclerView.NO_POSITION){
                    listener.onAcceptClicked(position)
                }
            })
            view.findViewById<Button>(R.id.btnReject).setOnClickListener(View.OnClickListener {
                    val position=adapterPosition
                    if (position!=RecyclerView.NO_POSITION){
                        listener.onRejectClicked(position)
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
        fun onAcceptClicked(position: Int)
        fun onRejectClicked(position: Int)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestHolder {
        var inflater= LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.user_cuntr_request_recycler_row,parent,false)
        return RequestHolder(view)
    }

    override fun onBindViewHolder(holder: RequestHolder, position: Int) {
        holder.userCuntrTitleText?.text=cuntrTitle[position]
        if (cuntrCreatorImg[position].isEmpty()){
            Picasso.get().load(R.drawable.baseline_account_circle_18).into(holder.userCuntrImgView)
        }else{
            Picasso.get().load(cuntrCreatorImg[position]).into(holder.userCuntrImgView)
        }
        holder.userCuntrCreatorText?.text=cuntrCreator[position]
    }

    override fun getItemCount(): Int {
        return cuntrTitle.size
    }
}