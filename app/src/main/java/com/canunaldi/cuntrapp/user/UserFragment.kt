package com.canunaldi.cuntrapp.user

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.canunaldi.cuntrapp.R
import com.canunaldi.cuntrapp.home.HomeFragmentDirections
import com.canunaldi.cuntrapp.user.UserFragmentDirections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_add_private_cuntr.*
import kotlinx.android.synthetic.main.fragment_user.*

// TODO: Rename parameter arguments, choose names that match
class UserFragment : Fragment(), UserCuntrRequestAdapter.OnItemClickListener {
    private  lateinit var auth: FirebaseAuth
    private  lateinit var db: FirebaseFirestore
    var cuntrTitle : ArrayList<String> = ArrayList()
    var cuntrTimeLeft : ArrayList<String> = ArrayList()
    var cuntrCreator : ArrayList<String> = ArrayList()
    var cuntrType : ArrayList<String> = ArrayList()
    var cuntrCreatorImg :ArrayList<String> = ArrayList()
    var cuntrId : ArrayList<String> = ArrayList()
    var name=""
    var imageUrl=""
    var adapter : UserCuntrRequestAdapter?=null
    override fun onItemClicked(position: Int) {
            val action= UserFragmentDirections.actionUserFragmentToSharedCuntrDetails(cuntrId[position])
            findNavController().navigate(action)
        println("Tıklandı: "+cuntrId[position])
    }

    override fun onAcceptClicked(position: Int) {
        println("Accept Clicked:"+position)
        acceptCuntr(position)
    }

    override fun onRejectClicked(position: Int) {
        println("Reject Clicked:"+position)
        rejectCuntr(position)
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
        return inflater.inflate(R.layout.fragment_user, container, false)

    }
    private fun getDataFromFirestore(){
        db.collection("users").document(auth.currentUser?.email.toString()).collection("cuntrRequests")
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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDataFromFirestore()
        var layoutManager= LinearLayoutManager(activity)
        userRequestRecView.layoutManager=layoutManager
        adapter= UserCuntrRequestAdapter(cuntrTitle,cuntrTimeLeft,cuntrCreator,cuntrType,cuntrCreatorImg,cuntrId,this)
        userRequestRecView.adapter=adapter

        val source = Source.CACHE
        db.collection("users").document(auth.currentUser?.email.toString()).get(source).addOnCompleteListener { doc ->
            if(doc.isSuccessful){
                println("Başarı")
                if(doc!=null){
                    println("Boş değil")
                    val user=doc.result
                    if (user != null) {
                        name=user.data?.get("name") as String
                        imageUrl =user.data?.get("profilPictureUrl") as String
                        //userName.text=name
                        userName.text=name
                        val image=view.findViewById<ImageView>(R.id.profilePictureImgView)
                        Picasso.get().load(imageUrl).into(image)
                    }
                }
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(activity, exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
            db.collection("users").document(auth.currentUser?.email.toString()).get().addOnCompleteListener { doc ->
                if(doc.isSuccessful){
                    println("Başarı")
                    if(doc!=null){
                        println("Boş değil")
                        val user=doc.result
                        if (user != null) {
                            name=user.data?.get("name") as String
                            imageUrl =user.data?.get("profilPictureUrl") as String
                            //userName.text=name
                            userName.text=name
                            val image=view.findViewById<ImageView>(R.id.profilePictureImgView)
                            Picasso.get().load(imageUrl).into(image)
                        }
                    }
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(activity, exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()

            }
        }
        btnUserFollowers.setOnClickListener {
            val action= UserFragmentDirections.actionUserFragmentToUserFollowFragment(true)
            findNavController().navigate(action)
        }
        btnUserFollowing.setOnClickListener {
            val action= UserFragmentDirections.actionUserFragmentToUserFollowFragment(false)
            findNavController().navigate(action)
        }
    }
    fun acceptCuntr(position: Int){
        db.collection("users").document(auth.currentUser?.email.toString()).collection("cuntrRequests")
            .document(cuntrId[position]).delete().addOnCompleteListener { task1 ->
                if(task1.isSuccessful) {
                    adapter!!.notifyDataSetChanged()
                    db.collection("cuntrs").document(cuntrId[position]).collection("invited").document(auth.currentUser?.email.toString())
                        .delete().addOnCompleteListener { task2 ->
                            if (task2.isSuccessful){
                                var postMailMap= hashMapOf<String,Any>()
                                postMailMap.put("mail",auth.currentUser?.email.toString())
                                db.collection("cuntrs").document(cuntrId[position]).collection("followers")
                                    .document(auth.currentUser?.email.toString()).set(postMailMap)
                                    .addOnSuccessListener { task3 ->
                                        var postMap= hashMapOf<String,Any>()
                                        postMap.put("title",cuntrTitle[position])
                                        postMap.put("date", cuntrTimeLeft[position])
                                        postMap.put("type","shared")
                                        postMap.put("id",cuntrId[position])
                                        postMap.put("creator", cuntrCreator[position])
                                        postMap.put("creatorImg",cuntrCreatorImg[position])

                                        db.collection("users").document(auth.currentUser?.email.toString())
                                            .collection("followedCuntrs").document(cuntrId[position]).set(postMap)
                                            .addOnCompleteListener { task2 ->
                                                if(task2.isSuccessful){
                                                    Toast.makeText(activity,"Davet kabul edildi.", Toast.LENGTH_LONG).show()
                                                    adapter!!.notifyDataSetChanged()
                                                }
                                            }
                                    }
                            }
                        }
                }
            }
    }

    fun rejectCuntr(position: Int){
        db.collection("users").document(auth.currentUser?.email.toString()).collection("cuntrRequests")
            .document(cuntrId[position]).delete().addOnCompleteListener { task1 ->
                if(task1.isSuccessful) {
                    adapter!!.notifyDataSetChanged()
                    db.collection("cuntrs").document(cuntrId[position]).collection("invited").document(auth.currentUser?.email.toString())
                        .delete().addOnCompleteListener { task2 ->
                            if (task2.isSuccessful){
                                Toast.makeText(activity,"Davet reddildi.", Toast.LENGTH_LONG).show()
                                adapter!!.notifyDataSetChanged()

                            }
                        }
                }
            }
    }
}

