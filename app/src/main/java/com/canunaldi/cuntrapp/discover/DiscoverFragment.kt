package com.canunaldi.cuntrapp.discover

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.canunaldi.cuntrapp.discover.DiscoverFragmentDirections
import com.canunaldi.cuntrapp.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_discover.*

class DiscoverFragment : Fragment() , DiscoverAdapter.OnItemClickListener {
    private  lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    lateinit var selectedCuntr:String
    var itemTitle : ArrayList<String> = ArrayList()
    var itemImg : ArrayList<String> = ArrayList()
    var itemType : ArrayList<String> = ArrayList()
    var itemId : ArrayList<String> = ArrayList()
    var itemList : ArrayList<DiscoverItemModel> = ArrayList()
    var itemUserList : ArrayList<DiscoverItemModel> = ArrayList()
    var itemCUntrList : ArrayList<DiscoverItemModel> = ArrayList()

    var adapter : DiscoverAdapter?=null


    override fun onItemClicked(position: String) {
        for (item in itemList){
            if (item.itemId==position&&item.itemType=="Açık Cuntr"){
                val action= DiscoverFragmentDirections.actionDiscoverFragmentToPublicCuntrDetails(position)
                findNavController().navigate(action)
            }
            else if (item.itemId==position&&item.itemType=="Kullanıcı"){
                println(position)
            }
        }


        //println(position)
    }

    override fun onFollowClicked(position: String) {
        for (item in itemList){
            if (item.itemId==position&&item.itemType=="Açık Cuntr"){
                println("tıklandı pos: "+position)
                followCuntr(position)
            }
            else if (item.itemId==position&&item.itemType=="Kullanıcı"){
                followUser(position)
            }
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
        return inflater.inflate(R.layout.fragment_discover, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDataFromFirebase()
        var layoutManager= LinearLayoutManager(activity)
        discoverRecView.layoutManager=layoutManager
        adapter= DiscoverAdapter(itemList,this)
        discoverRecView.adapter=adapter
        discoverSearch.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter!!.filter.filter(newText)
                return false
            }

        })
    }
    fun getDataFromFirebase(){
        db.collection("users").addSnapshotListener { value, error ->
            if(error !=null){
                Toast.makeText(activity,error.localizedMessage.toString(), Toast.LENGTH_LONG).show()
            }else{
                if(value !=null){
                    if(!value.isEmpty){
                        val documents=value.documents
                        itemUserList.clear()
                        for (document in documents){
                            val userId=document.id.toString()
                            val userName=document.get("name") as String
                            val userMail=document.get("mail") as String
                            val itemUserType="Kullanıcı"
                            val itemUserImg=document.get("profilPictureUrl") as String
                            val item= DiscoverItemModel(itemUserType,itemUserImg,userName,userId)
                            if (userMail!=auth.currentUser?.email.toString()){
                                itemUserList.add(item)
                                adapter!!.notifyDataSetChanged()
                            }
                        }
                    }
                }

            }
        }
        db.collection("cuntrs")
            .addSnapshotListener { value, error ->
                if(error !=null){
                    Toast.makeText(activity,error.localizedMessage.toString(), Toast.LENGTH_LONG).show()
                }else{
                    if(value !=null){
                        if(!value.isEmpty){
                            val documents=value.documents
                            itemCUntrList.clear()
                            itemList.clear()
                            for (document in documents){
                                val cuntrType=document.get("type") as String
                                val userMail=document.get("creator") as String

                                if (cuntrType=="public"&&userMail!=auth.currentUser?.email.toString()){
                                    val cuntrId=document.id.toString()
                                    val cuntrName=document.get("title") as String
                                    val itemCuntrType="Açık Cuntr"
                                    val itemCuntrImg="R.drawable.ic_cuntr"
                                    val item2= DiscoverItemModel(itemCuntrType,itemCuntrImg,cuntrName,cuntrId)
                                    println("girdi.CUntr type: "+cuntrType+"cuntr name: "+cuntrName)
                                    itemCUntrList.add(item2)
                                }
                                else{
                                    println("girmedi.CUntr type:"+cuntrType)
                                }
                            }
                            itemList.addAll(itemUserList)
                            itemList.addAll(itemCUntrList)
                            adapter!!.notifyDataSetChanged()
                            println("list size: "+itemList.size)

                        }
                    }
                }
            }
    }
    fun followCuntr(cuntrId: String){
        println("girdi cuntrId: "+cuntrId)
        var title: String?=null
        var creatorMail: String?=null
        var date: String?=null
        val type="public"
        var creatorName: String?=null
        var creatorImg: String?=null
        val postMap= hashMapOf<String,Any>()
        db.collection("cuntrs").document(cuntrId).get().addOnCompleteListener { doc ->
            if(doc.isSuccessful){
                println("Başarı")
                if(doc!=null){
                    println("Boş değil")
                    val user=doc.result
                    if (user != null) {
                        title=user.data?.get("title") as String
                        creatorMail =user.data?.get("creator") as String
                        date =user.data?.get("date") as String
                        db.collection("users").document(creatorMail!!).get().addOnCompleteListener { doc2 ->
                            if(doc2.isSuccessful){
                                println("Başarı")
                                if(doc2!=null){
                                    println("Boş değil")
                                    val user=doc2.result
                                    if (user != null) {
                                        creatorName=user.data?.get("name") as String
                                        creatorImg =user.data?.get("profilPictureUrl") as String

                                        postMap.put("title", title!!)
                                        postMap.put("type", type)
                                        postMap.put("id", cuntrId)
                                        postMap.put("creator", creatorName!!)
                                        postMap.put("creatorImg", creatorImg!!)
                                        postMap.put("date", date!!)
                                        db.collection("users").document(auth.currentUser?.email.toString()).collection("followedCuntrs")
                                            .document(cuntrId).set(postMap).addOnCompleteListener { task2 ->
                                                if(task2.isSuccessful&&task2.isComplete){

                                                    Toast.makeText(activity,"Cuntr takip edildi.", Toast.LENGTH_LONG).show()
                                                }
                                            }.addOnFailureListener { exception ->
                                                Toast.makeText(activity,exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
                                            }
                                    }
                                }
                            }
                        }.addOnFailureListener { exception ->
                            Toast.makeText(activity, exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(activity, exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()

        }
    }
    fun followUser(userId: String){
        var userMail=auth.currentUser?.email.toString()
        var userImg: String?=null
        var userName: String?=null
        var followedUserMail=userId
        var followedUserImg: String?=null
        var followedUserName: String?=null
        val userPostMap= hashMapOf<String,Any>()
        val followedUserPostMap= hashMapOf<String,Any>()

        db.collection("users").document(userMail).get().addOnSuccessListener { doc1 ->
                println("Başarı")
                if (doc1 != null) {
                    userName=doc1.data?.get("name") as String
                    userImg=doc1.data?.get("profilPictureUrl") as String
                    db.collection("users").document(userId).get().addOnSuccessListener { doc1 ->
                        println("Başarı")
                        if (doc1 != null) {
                            followedUserName=doc1.data?.get("name") as String
                            followedUserImg=doc1.data?.get("profilPictureUrl") as String
                            followedUserPostMap.put("name", followedUserName!!)
                            followedUserPostMap.put("profilPictureUrl", followedUserImg!!)
                            followedUserPostMap.put("mail", followedUserMail!!)
                            followedUserPostMap.put("dateFollowed", Timestamp.now())

                            db.collection("users").document(auth.currentUser?.email.toString()).collection("following")
                                .document(followedUserMail).set(followedUserPostMap).addOnCompleteListener { task2 ->
                                    if(task2.isSuccessful&&task2.isComplete){
                                        userPostMap.put("name", userName!!)
                                        userPostMap.put("profilPictureUrl", userImg!!)
                                        userPostMap.put("mail", userMail!!)
                                        userPostMap.put("dateFollowed", Timestamp.now())
                                        db.collection("users").document(followedUserMail).collection("follower")
                                            .document(userMail).set(userPostMap).addOnCompleteListener { task2 ->
                                                if(task2.isSuccessful&&task2.isComplete){
                                                    db.collection("cuntrs").get().addOnSuccessListener { document ->
                                                        for (doc in document){
                                                            val cuntrType=doc.get("type") as String
                                                            val cuntrCreatorMail=doc.get("creator") as String
                                                            if (cuntrType=="public"&&cuntrCreatorMail==followedUserMail){
                                                                val cuntrCreatorName=followedUserName
                                                                val cuntrCratorImg=followedUserImg
                                                                val cuntrId=doc.id
                                                                val cuntrDate=doc.get("date") as String
                                                                val cuntrTitle=doc.get("title") as String
                                                                val followedCuntrPostMap= hashMapOf<String,Any>()
                                                                followedCuntrPostMap.put("title",cuntrTitle!!)
                                                                followedCuntrPostMap.put("id",cuntrId!!)
                                                                followedCuntrPostMap.put("date",cuntrDate!!)
                                                                followedCuntrPostMap.put("creator",cuntrCreatorName!!)
                                                                followedCuntrPostMap.put("creatorImg",cuntrCratorImg!!)
                                                                followedCuntrPostMap.put("type",cuntrType)
                                                                db.collection("users").document(userMail).collection("followedCuntrs")
                                                                    .document(cuntrId).set(followedCuntrPostMap).addOnCompleteListener { task2 ->
                                                                        if(task2.isSuccessful&&task2.isComplete){
                                                                            println("cuntr takip edildi.")
                                                                        }
                                                                    }
                                                            }
                                                        }
                                                    }
                                                    Toast.makeText(activity,"Kullanıcı takip edildi.", Toast.LENGTH_LONG).show()
                                                }
                                            }.addOnFailureListener { exception ->
                                                Toast.makeText(activity,exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
                                            }
                                    }
                                }.addOnFailureListener { exception ->
                                    Toast.makeText(activity,exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
                                }

                        }

                }

        }
    }
}



}
