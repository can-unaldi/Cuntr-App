package com.canunaldi.cuntrapp.addcuntrs

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.canunaldi.cuntrapp.addcuntrs.AddPrivateCuntrFragmentDirections
import com.canunaldi.cuntrapp.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_add_private_cuntr.*

import java.util.*



class AddPrivateCuntrFragment : Fragment() , DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    var day=0
    var month=0
    var year=0
    var hour=0
    var minute=0

    var savedDay=0
    var savedMonth=0
    var savedYear=0
    var savedHour=0
    var savedMinute=0
    var date=""
    var creator=""
    var imageUrl=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_private_cuntr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db= FirebaseFirestore.getInstance()
        auth= FirebaseAuth.getInstance()
        btnDatePick.setOnClickListener {
            getDateTimeCalendar()
            DatePickerDialog(requireContext(),this,year,month,day).show()
        }
        btnSubmit.setOnClickListener {
            submitData()
        }



    }
    private fun submitData(){
        val title=editTextTitle.text.toString()
        println(title)
        val place=editTextPlace.text.toString()
        println(place)
        val description= editTextDesc.text.toString()
        println(description)
        val link=editTextLink.text.toString()
        var creator=""
        var imageUrl=""
        val postMap= hashMapOf<String,Any>()
        postMap.put("title",title)
        postMap.put("date", date)
        postMap.put("place",place)
        postMap.put("createDate", Timestamp.now())
        postMap.put("creator", auth.currentUser?.email.toString())
        postMap.put("description",description)
        postMap.put("type","private")
        postMap.put("link",link)
        val postUserMap= hashMapOf<String,Any>()
        db.collection("users").document(auth.currentUser?.email.toString()).get().addOnCompleteListener { doc ->
            if(doc.isSuccessful){
                println("Ba??ar??")
                if(doc!=null){
                    println("Bo?? de??il")
                    val user=doc.result
                    if (user != null) {
                        creator=user.data?.get("name") as String
                        imageUrl =user.data?.get("profilPictureUrl") as String
                        postUserMap.put("creator", creator)
                        postUserMap.put("creatorImg",imageUrl)
                    }
                }else{
                    postUserMap.put("creator", "Cuntr")
                    postUserMap.put("creatorImg","@drawable/baseline_account_circle_18")
                }
            }
        }.
        addOnFailureListener { exception ->
            Toast.makeText(activity, exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
            postUserMap.put("creator", "Cuntr")
            postUserMap.put("creatorImg","@drawable/baseline_account_circle_18")
            postMap.put("creatorName","Cuntr")
            postMap.put("creatorImg","@drawable/baseline_account_circle_18")
        }
        postUserMap.put("title",title)
        postUserMap.put("date", date)
        postUserMap.put("type","private")

        db.collection("cuntrs").add(postMap).addOnCompleteListener { task->
            if(task.isSuccessful&&task.isComplete){
                postUserMap.put("id", task.result?.id.toString())
                db.collection("users").document(auth.currentUser?.email.toString()).collection("followedCuntrs")
                        .document(task.result?.id.toString()).set(postUserMap).addOnCompleteListener { task2 ->
                        if(task2.isSuccessful&&task2.isComplete){
                            Toast.makeText(activity,"Cuntr Ba??ar?? ile olu??turuldu.",Toast.LENGTH_LONG).show()
                        }
                    }.addOnFailureListener { exception ->
                        Toast.makeText(activity,exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
                    }
                finish()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(activity,exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
        }

    }

    private fun finish() {
        val action= AddPrivateCuntrFragmentDirections.actionAddPrivateCuntrToHomeFragment()
        findNavController().navigate(action)

    }
    private fun getDateTimeCalendar(){
        val cal:Calendar= Calendar.getInstance()
        day=cal.get(Calendar.DAY_OF_MONTH)
        month=cal.get(Calendar.MONTH)
        year=cal.get(Calendar.YEAR)
        hour=cal.get(Calendar.HOUR)
        minute=cal.get(Calendar.MINUTE)


    }
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay=dayOfMonth
        savedMonth=month+1
        savedYear=year
        println(savedMonth.toString())
        println("day:"+savedDay.toString())
        getDateTimeCalendar()
        TimePickerDialog(requireContext(),this,hour,minute,true).show()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour=hourOfDay
        savedMinute=minute

        if(savedDay<10&&savedMonth<10&&savedHour<10&&savedMinute<10){
            date="0${savedDay}/0${savedMonth}/${savedYear} 0${savedHour}:0${savedMinute}:00"

        }
        else if (savedMonth<10&&savedHour<10&&savedMinute<10){
            date="${savedDay}/0${savedMonth}/${savedYear} 0${savedHour}:0${savedMinute}:00"

        }
        else if (savedDay<10&&savedHour<10&&savedMinute<10){
            date="0${savedDay}/${savedMonth}/${savedYear} 0${savedHour}:0${savedMinute}:00"

        }else if (savedDay<10&&savedMonth<10&&savedMinute<10){
            date="0${savedDay}/0${savedMonth}/${savedYear} ${savedHour}:0${savedMinute}:00"

        }else if (savedDay<10&&savedMonth<10&&savedHour<10){
            date="0${savedDay}/0${savedMonth}/${savedYear} 0${savedHour}:${savedMinute}:00"

        }else if (savedDay<10&&savedMonth<10){
            date="0${savedDay}/0${savedMonth}/${savedYear} ${savedHour}:${savedMinute}:00"

        }else if (savedHour<10&&savedMinute<10){
            date="${savedDay}/${savedMonth}/${savedYear} 0${savedHour}:0${savedMinute}:00"

        }else if (savedDay<10&&savedMinute<10){
            date="0${savedDay}/${savedMonth}/${savedYear} ${savedHour}:0${savedMinute}:00"

        }else if (savedMonth<10&&savedHour<10){
            date="${savedDay}/0${savedMonth}/${savedYear} 0${savedHour}:${savedMinute}:00"

        }
        else if(savedDay<10&&savedHour<10){
            date="0${savedDay}/${savedMonth}/${savedYear} 0${savedHour}:${savedMinute}:00"

        }else if(savedMonth<10&&savedMinute<10){
            date="${savedDay}/0${savedMonth}/${savedYear} ${savedHour}:0${savedMinute}:00"
        }
        else if (savedMinute<10){
            date="${savedDay}/${savedMonth}/${savedYear} ${savedHour}:0${savedMinute}:00"

        }else if (savedHour<10){
            date="${savedDay}/${savedMonth}/${savedYear} 0${savedHour}:${savedMinute}:00"

        }else if (savedMonth<10){
            date="${savedDay}/0${savedMonth}/${savedYear} ${savedHour}:${savedMinute}:00"

        }else if (savedDay<10){
            date="0${savedDay}/${savedMonth}/${savedYear} ${savedHour}:${savedMinute}:00"

        }else
            date="${savedDay}/${savedMonth}/${savedYear} ${savedHour}:${savedMinute}:00"

        txtDate.text= date
    }
}