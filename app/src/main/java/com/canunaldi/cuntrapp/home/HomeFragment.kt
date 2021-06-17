package com.canunaldi.cuntrapp.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.canunaldi.cuntrapp.home.HomeFragmentDirections
import com.canunaldi.cuntrapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_home.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() , HomeAdapter.OnItemClickListener {
    private  lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    lateinit var selectedCuntr:String
    var cuntrTitle : ArrayList<String> = ArrayList()
    var cuntrTimeLeft : ArrayList<String> = ArrayList()
    var cuntrCreator : ArrayList<String> = ArrayList()
    var cuntrType : ArrayList<String> = ArrayList()
    var cuntrCreatorImg :ArrayList<String> = ArrayList()
    var cuntrId : ArrayList<String> = ArrayList()
    var adapter : HomeAdapter?=null

    override fun onItemClicked(position: Int) {
        if (cuntrType[position]=="private"){
            val action= HomeFragmentDirections.actionHomeFragmentToPrivateCuntrDetailsFragment(cuntrId[position])
            findNavController().navigate(action)
        }else if (cuntrType[position]=="shared"){
            val action= HomeFragmentDirections.actionHomeFragmentToSharedCuntrDetails(cuntrId[position])
            findNavController().navigate(action)
        }else if (cuntrType[position]=="public"){
            val action= HomeFragmentDirections.actionHomeFragmentToPublicCuntrDetails(cuntrId[position])
            findNavController().navigate(action)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth= FirebaseAuth.getInstance()
        db= FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getTimeFromFirebase()
        var layoutManager=LinearLayoutManager(activity)
        homeRecyclerView.layoutManager=layoutManager
        adapter= HomeAdapter(cuntrTitle,cuntrTimeLeft,cuntrCreator,cuntrType,cuntrCreatorImg,this)
        homeRecyclerView.adapter=adapter
    }
    private fun getTimeFromFirebase(){
        db.collection("users").document(auth.currentUser?.email.toString()).collection("followedCuntrs")
                .addSnapshotListener { value, error ->
            if(error !=null){
                Toast.makeText(activity,error.localizedMessage.toString(), Toast.LENGTH_LONG).show()
            }else{
                if(value !=null){
                    if(!value.isEmpty){
                        val documents=value.documents
                        cuntrTitle.clear()
                        cuntrTimeLeft.clear()
                        cuntrCreator.clear()
                        cuntrType.clear()
                        cuntrCreatorImg.clear()
                        cuntrId.clear()
                        for (document in documents){
                            val id=document.id.toString()
                            val title=document.get("title") as String
                            val creator=document.get("creator") as String
                            val date=document.get("date") as String
                            val type=document.get("type") as String
                            val creatorImg=document.get("creatorImg") as String
                            cuntrTitle.add(title)
                            cuntrCreator.add(creator)
                            cuntrTimeLeft.add(date)
                            cuntrCreatorImg.add(creatorImg)
                            cuntrType.add(type)
                            cuntrId.add(id)
                            adapter!!.notifyDataSetChanged()
                        }
                    }
                }
            }
        }

    }
}