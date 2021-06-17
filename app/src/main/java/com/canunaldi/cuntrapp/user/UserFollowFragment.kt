package com.canunaldi.cuntrapp.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.canunaldi.cuntrapp.R
import com.canunaldi.cuntrapp.cuntrdetails.PublicCuntrDetailsArgs
import com.canunaldi.cuntrapp.discover.DiscoverItemModel
import com.canunaldi.cuntrapp.home.HomeAdapter
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_user_follow.*


class UserFollowFragment : Fragment() ,UserFollowAdapter.OnItemClickListener {
    private  lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val args: UserFollowFragmentArgs by navArgs()

    lateinit var selectedUser:String
    var selectedType: Boolean?=null

    var userMailArray : ArrayList<String> = ArrayList()
    var userNameArray : ArrayList<String> = ArrayList()
    var userImgArray : ArrayList<String> = ArrayList()
    var adapter : UserFollowAdapter?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth= FirebaseAuth.getInstance()
        db= FirebaseFirestore.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_follow, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectedType=args.selectedType
        if (selectedType==true){
            txtFollow.text="Takipçiler"
        }
        else{
            txtFollow.text="Takip Edilenler"
        }
        getDataFromFirebase()

        var layoutManager= LinearLayoutManager(activity)
        userFollowRec.layoutManager=layoutManager
        adapter= UserFollowAdapter(userNameArray,userImgArray, selectedType!!,this)
        userFollowRec.adapter=adapter
    }

    fun getDataFromFirebase() {
        if (selectedType == true){
            db.collection("users").document(auth.currentUser?.email.toString()).collection("follower").addSnapshotListener { value, error ->
                if (error != null) {
                    Toast.makeText(activity, error.localizedMessage.toString(), Toast.LENGTH_LONG).show()
                } else {
                    if (value != null) {
                        if (!value.isEmpty) {
                            val documents = value.documents
                            userMailArray.clear()
                            userNameArray.clear()
                            userImgArray.clear()
                            for (document in documents) {
                                val userName = document.get("name") as String
                                val userMail = document.get("mail") as String
                                val userImg = document.get("profilPictureUrl") as String
                                userMailArray.add(userMail)
                                userNameArray.add(userName)
                                userImgArray.add(userImg)
                                adapter!!.notifyDataSetChanged()
                            }
                        }
                    }

                }
            }
        }
        else{
            db.collection("users").document(auth.currentUser?.email.toString()).collection("following").addSnapshotListener { value, error ->
                if (error != null) {
                    Toast.makeText(activity, error.localizedMessage.toString(), Toast.LENGTH_LONG).show()
                } else {
                    if (value != null) {
                        if (!value.isEmpty) {
                            val documents = value.documents
                            userMailArray.clear()
                            userNameArray.clear()
                            userImgArray.clear()
                            for (document in documents) {
                                val userName = document.get("name") as String
                                val userMail = document.get("mail") as String
                                val userImg = document.get("profilPictureUrl") as String
                                userMailArray.add(userMail)
                                userNameArray.add(userName)
                                userImgArray.add(userImg)
                                adapter!!.notifyDataSetChanged()
                            }
                        }
                    }

                }
            }
        }

    }
    override fun onItemClicked(position: Int) {
        println("kullanıcı tıklandı")
    }

    override fun onFollowClicked(position: Int) {
        if (selectedType == true){
            follow(userMailArray[position])
        }
        else{
            unfollow(userMailArray[position])
        }
    }
    fun follow(clickedUserId: String) {
        var userMail = auth.currentUser?.email.toString()
        var userImg: String? = null
        var userName: String? = null
        var followedUserMail=clickedUserId
        var followedUserImg: String? = null
        var followedUserName: String? = null
        var userId=clickedUserId
        val userPostMap = hashMapOf<String, Any>()
        val followedUserPostMap = hashMapOf<String, Any>()

        db.collection("users").document(userMail).get().addOnSuccessListener { doc1 ->
            println("Başarı")
            if (doc1 != null) {
                userName = doc1.data?.get("name") as String
                userImg = doc1.data?.get("profilPictureUrl") as String
                db.collection("users").document(userId).get().addOnSuccessListener { doc1 ->
                    println("Başarı")
                    if (doc1 != null) {
                        followedUserName = doc1.data?.get("name") as String
                        followedUserImg = doc1.data?.get("profilPictureUrl") as String
                        followedUserPostMap.put("name", followedUserName!!)
                        followedUserPostMap.put("profilPictureUrl", followedUserImg!!)
                        followedUserPostMap.put("mail", followedUserMail!!)
                        followedUserPostMap.put("dateFollowed", Timestamp.now())

                        db.collection("users").document(auth.currentUser?.email.toString())
                            .collection("following")
                            .document(followedUserMail).set(followedUserPostMap)
                            .addOnCompleteListener { task2 ->
                                if (task2.isSuccessful && task2.isComplete) {
                                    userPostMap.put("name", userName!!)
                                    userPostMap.put("profilPictureUrl", userImg!!)
                                    userPostMap.put("mail", userMail!!)
                                    userPostMap.put("dateFollowed", Timestamp.now())
                                    db.collection("users").document(followedUserMail)
                                        .collection("follower")
                                        .document(userMail).set(userPostMap)
                                        .addOnCompleteListener { task2 ->
                                            if (task2.isSuccessful && task2.isComplete) {
                                                db.collection("cuntrs").get()
                                                    .addOnSuccessListener { document ->
                                                        for (doc in document) {
                                                            val cuntrType =
                                                                doc.get("type") as String
                                                            val cuntrCreatorMail =
                                                                doc.get("creator") as String
                                                            if (cuntrType == "public" && cuntrCreatorMail == followedUserMail) {
                                                                val cuntrCreatorName =
                                                                    followedUserName
                                                                val cuntrCratorImg = followedUserImg
                                                                val cuntrId = doc.id
                                                                val cuntrDate =
                                                                    doc.get("date") as String
                                                                val cuntrTitle =
                                                                    doc.get("title") as String
                                                                val followedCuntrPostMap =
                                                                    hashMapOf<String, Any>()
                                                                followedCuntrPostMap.put(
                                                                    "title",
                                                                    cuntrTitle!!
                                                                )
                                                                followedCuntrPostMap.put(
                                                                    "id",
                                                                    cuntrId!!
                                                                )
                                                                followedCuntrPostMap.put(
                                                                    "date",
                                                                    cuntrDate!!
                                                                )
                                                                followedCuntrPostMap.put(
                                                                    "creator",
                                                                    cuntrCreatorName!!
                                                                )
                                                                followedCuntrPostMap.put(
                                                                    "creatorImg",
                                                                    cuntrCratorImg!!
                                                                )
                                                                followedCuntrPostMap.put(
                                                                    "type",
                                                                    cuntrType
                                                                )
                                                                db.collection("users")
                                                                    .document(userMail)
                                                                    .collection("followedCuntrs")
                                                                    .document(cuntrId)
                                                                    .set(followedCuntrPostMap)
                                                                    .addOnCompleteListener { task2 ->
                                                                        if (task2.isSuccessful && task2.isComplete) {
                                                                            println("cuntr takip edildi.")
                                                                        }
                                                                    }
                                                            }
                                                        }
                                                    }
                                                Toast.makeText(
                                                    activity,
                                                    "Kullanıcı takip edildi.",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }.addOnFailureListener { exception ->
                                            Toast.makeText(
                                                activity,
                                                exception.localizedMessage.toString(),
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                }
                            }.addOnFailureListener { exception ->
                                Toast.makeText(
                                    activity,
                                    exception.localizedMessage.toString(),
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                    }

                }

            }
        }
    }
    fun unfollow(userId: String){
        println("takipten çıkarılacak")
    }


}