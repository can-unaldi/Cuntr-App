package com.canunaldi.cuntrapp.cuntrdetails

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.canunaldi.cuntrapp.R
import com.canunaldi.cuntrapp.cuntrdetails.SharedCuntrDetailsArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_private_cuntr_details.btnLinkPC
import kotlinx.android.synthetic.main.fragment_private_cuntr_details.textTitlePC
import kotlinx.android.synthetic.main.fragment_private_cuntr_details.txtDatePC
import kotlinx.android.synthetic.main.fragment_private_cuntr_details.txtDescPC
import kotlinx.android.synthetic.main.fragment_private_cuntr_details.txtPlacePC
import kotlinx.android.synthetic.main.fragment_private_cuntr_details.txtUserNamePC
import kotlinx.android.synthetic.main.fragment_private_cuntr_details.userImgPC
import kotlinx.android.synthetic.main.fragment_shared_cuntr_details.*


class SharedCuntrDetails : Fragment() {
    private val args: SharedCuntrDetailsArgs by navArgs()
    private  lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    lateinit var selectedCuntr:String
    lateinit var userImg:String
    lateinit var userName:String
    lateinit var cuntrTitle:String
    lateinit var cuntrDate:String
    lateinit var cuntrPlace:String
    lateinit var cuntrLink:String
    lateinit var cuntrDesc:String
    lateinit var userMail:String
    var followerImgUrl : ArrayList<String> = ArrayList()
    var followerName : ArrayList<String> = ArrayList()
    var adapter : SharedCuntrAdater?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shared_cuntr_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectedCuntr=args.cuntrId
        auth= FirebaseAuth.getInstance()
        db= FirebaseFirestore.getInstance()

        println(selectedCuntr)
        getCuntrDataFromFirebase()

        var layoutManager= LinearLayoutManager(activity)
        sharedCuntrDetailRecycler.layoutManager=layoutManager
        adapter= SharedCuntrAdater(followerImgUrl,followerName)
        sharedCuntrDetailRecycler.adapter=adapter

        btnLinkPC.setOnClickListener {
            if(cuntrLink==""||cuntrLink=="-"){
                Toast.makeText(activity, "Hergangi bir link yok.", Toast.LENGTH_LONG).show()

            }else{
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(cuntrLink))
                startActivity(i)
            }
        }
    }
    private fun setData(){
        Picasso.get().load(userImg).into(userImgPC)
        txtUserNamePC.text=userName
        textTitlePC.text=cuntrTitle
        txtDatePC.text=cuntrDate
        txtPlacePC.text=cuntrPlace
        txtDescPC.text=cuntrDesc
    }
    private fun getCuntrDataFromFirebase(){
        db.collection("cuntrs").document(selectedCuntr).get().addOnCompleteListener { doc ->
            if(doc.isSuccessful){
                if(doc!=null){
                    val cuntr=doc.result
                    if (cuntr != null) {
                        cuntrTitle= (cuntr.data?.get("title") as String?).toString()
                        userMail= (cuntr.data?.get("creator") as String?).toString()
                        cuntrDate= (cuntr.data?.get("date") as String?).toString()
                        cuntrPlace= (cuntr.data?.get("place") as String?).toString()
                        cuntrLink= (cuntr.data?.get("link") as String?).toString()
                        cuntrDesc= (cuntr.data?.get("description") as String?).toString()
                        db.collection("users").document(userMail).get().addOnCompleteListener { doc2 ->
                            if(doc2.isSuccessful){
                                if(doc!=null){
                                    val user=doc2.result
                                    if(user!=null){
                                        userName= (user.data?.get("name") as String?).toString()
                                        userImg= (user.data?.get("profilPictureUrl") as String?).toString()
                                        setData()
                                        db.collection("cuntrs").document(selectedCuntr).collection("followers")
                                                .addSnapshotListener { value, error ->
                                                    if (error != null) {
                                                        Toast.makeText(activity, error.localizedMessage.toString(), Toast.LENGTH_LONG).show()
                                                    } else {
                                                        if (value != null) {
                                                            if (!value.isEmpty) {
                                                                val documents = value.documents
                                                                followerImgUrl.clear()
                                                                followerName.clear()
                                                                for (document in documents) {
                                                                    val followerMail = document.get("mail") as String
                                                                    db.collection("users").document(followerMail).get()
                                                                            .addOnCompleteListener { doc ->
                                                                                if (doc.isSuccessful) {
                                                                                    println("Başarı")
                                                                                    if (doc != null) {
                                                                                        println("Boş değil")
                                                                                        val user = doc.result
                                                                                        if (user != null) {
                                                                                            var name = user.data?.get("name") as String
                                                                                            var imageUrl = user.data?.get("profilPictureUrl") as String
                                                                                            followerName.add(name)
                                                                                            followerImgUrl.add(imageUrl)

                                                                                            adapter!!.notifyDataSetChanged()
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }.addOnFailureListener { exception ->
                                                                                Toast.makeText(activity, exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()

                                                                            }
                                                                }
                                                            }
                                                        }
                                                    }
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
}