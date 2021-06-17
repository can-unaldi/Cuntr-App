package com.canunaldi.cuntrapp.home

import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.canunaldi.cuntrapp.R
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeAdapter(private val cuntrTitle: ArrayList<String>,private val cuntrTimeLeft: ArrayList<String>,private val cuntrCreator: ArrayList<String>,
                  private val cuntrType:ArrayList<String>,private val cuntrCreatorImg:ArrayList<String>,
                  private val listener: OnItemClickListener) : RecyclerView.Adapter<HomeAdapter.CuntrHolder>() {
    inner class CuntrHolder(view : View) : RecyclerView.ViewHolder(view), View.OnClickListener{

        var homeCuntrTitleText : TextView?=null
        var homeCuntrCreatorText : TextView?=null
        var homeCuntrDateText : TextView?=null
        var homeCuntrImgView : ImageView?=null

        lateinit var countDownTimer: CountDownTimer
        init {
            homeCuntrCreatorText=view.findViewById(R.id.cuntrCreatorText)
            homeCuntrTitleText=view.findViewById(R.id.cuntrTitleText)
            homeCuntrDateText=view.findViewById(R.id.cuntrTimeLeftText)
            homeCuntrImgView=view.findViewById(R.id.cuntrCreatorImgView)

            view.setOnClickListener(this)
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CuntrHolder {
        var inflater=LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.home_recycler_view_row,parent,false)
        return CuntrHolder(view)
    }

    override fun onBindViewHolder(holder: CuntrHolder, position: Int) {
        holder.homeCuntrTitleText?.text=cuntrTitle[position]
        if (cuntrCreatorImg[position].isEmpty()){
            Picasso.get().load(R.drawable.baseline_account_circle_18).into(holder.homeCuntrImgView)

        }else{
            Picasso.get().load(cuntrCreatorImg[position]).into(holder.homeCuntrImgView)
        }
        holder.homeCuntrCreatorText?.text=cuntrCreator[position]

        val currentTime = Calendar.getInstance().time
        val format1 = SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault())
        val endDate = format1.parse(cuntrTimeLeft[position])

        //milliseconds
        var different = endDate.time - currentTime.time
        holder.countDownTimer = object : CountDownTimer(different, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                var diff = millisUntilFinished
                val secondsInMilli: Long = 1000
                val minutesInMilli = secondsInMilli * 60
                val hoursInMilli = minutesInMilli * 60
                val daysInMilli = hoursInMilli * 24

                val elapsedDays = diff / daysInMilli
                diff %= daysInMilli

                val elapsedHours = diff / hoursInMilli
                diff %= hoursInMilli

                val elapsedMinutes = diff / minutesInMilli
                diff %= minutesInMilli

                val elapsedSeconds = diff / secondsInMilli
                if(elapsedDays>1){
                    holder.homeCuntrDateText?.text = "$elapsedDays gün $elapsedHours saat"
                }
                else if(elapsedDays<1&&elapsedHours>=10){
                    holder.homeCuntrDateText?.text = "$elapsedHours saat $elapsedMinutes dakika"
                }else if(elapsedHours<10&&elapsedHours>1){
                    holder.homeCuntrDateText?.text = "$elapsedHours saat $elapsedMinutes dk $elapsedSeconds sn"
                }else if(elapsedMinutes>=1){
                    holder.homeCuntrDateText?.text = "$elapsedMinutes dk $elapsedSeconds saniye"
                }else if(elapsedMinutes<1){
                    holder.homeCuntrDateText?.text = "$elapsedSeconds saniye"
                }
            }

            override fun onFinish() {
                holder.homeCuntrDateText?.text = "done!"
                println("done!")
            }
        }.start()

    }

    override fun getItemCount(): Int {
        return cuntrTitle.size
    }
}