package com.canunaldi.cuntrapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.canunaldi.cuntrapp.entrance.LoginFragment
import com.canunaldi.cuntrapp.entrance.RegisterFragment
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_register.*
import java.lang.Exception
import java.util.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private  lateinit var db: FirebaseFirestore
    private lateinit var auth:FirebaseAuth
    lateinit var selectedImage: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db= FirebaseFirestore.getInstance()
        //getTimeFromFirebase()
        auth= FirebaseAuth.getInstance()
        if(auth.currentUser!=null){
            Toast.makeText(applicationContext,"Giriş Yapıldı", Toast.LENGTH_LONG).show()
            val intent= Intent(applicationContext,CuntrActivity::class.java)
            startActivity(intent)
            finish()
        }
        //printDifferenceDateForHours()
        var fragmentManager=supportFragmentManager
        var fragmentTransaction=fragmentManager.beginTransaction()
        var signUpFragment= LoginFragment()
        fragmentTransaction.add(R.id.frameLayout,signUpFragment).commit()

    }
    fun signUpClicked(view: View){
        var fragmentManager=supportFragmentManager
        var fragmentTransaction=fragmentManager.beginTransaction()
        var signUpFragment= RegisterFragment()
        fragmentTransaction.replace(R.id.frameLayout,signUpFragment).commit()


    }
    fun registerClicked(view: View){

        val email=txtRegMail.text.toString()
        val password=txtRegPassword.text.toString()
        val password2=txtRegPassword2.text.toString()
        val name=txtName.text.toString()
        val uuid=UUID.randomUUID()
        val imageUUID="$uuid.jpg"
        val storage=FirebaseStorage.getInstance()
        val reference=storage.reference
        val imageReferrence=reference.child("profilePictures").child(imageUUID)
        if(password==password2&&email!=""&&password!=""&&name!=""){
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    if(selectedImage!=null) {
                        imageReferrence.putFile(selectedImage).addOnSuccessListener { taskSnapshot ->
                            val uploadedImageReference=FirebaseStorage.getInstance().reference.child("profilePictures").child(imageUUID)
                            uploadedImageReference.downloadUrl.addOnSuccessListener { uri ->
                                val downloadUrl=uri.toString()

                                val postMap= hashMapOf<String,Any>()
                                postMap.put("mail", email)
                                postMap.put("name",name)
                                postMap.put("profilPictureUrl",downloadUrl)
                                postMap.put("accountCreated",Timestamp.now())
                                db.collection("users").document(email).set(postMap).addOnCompleteListener { task ->
                                    if(task.isSuccessful&&task.isComplete){
                                        Toast.makeText(applicationContext,"Hesap Başarı ile oluşturuldu.",Toast.LENGTH_LONG).show()
                                        val intent=Intent(applicationContext,CuntrActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                }.addOnFailureListener(this) { ex->
                                    Toast.makeText(applicationContext,ex.localizedMessage.toString(), Toast.LENGTH_LONG).show()
                                }
                            }.addOnFailureListener { exception ->
                                Toast.makeText(applicationContext,exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
                            }
                        }.addOnFailureListener { exception ->
                            Toast.makeText(applicationContext,exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
                        }

                    }

                }
            }.addOnFailureListener(this) { exception ->
                if(exception!=null){
                    Toast.makeText(applicationContext,exception.localizedMessage.toString(),Toast.LENGTH_LONG).show()
                }
            }
        }
        else{
            Toast.makeText(applicationContext,"Bilgileri yanlış girdiniz. Tekrar deneyiniz.",Toast.LENGTH_LONG).show()
        }
    }
    fun imageClicked(view: View){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }
        else{
            val intent=Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent,2)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode==1){
            if (grantResults.size>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                val intent=Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent,2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode==2 && resultCode==Activity.RESULT_OK&&data!=null){
            selectedImage=data.data!!
            try {
                if(selectedImage!=null){
                    if(Build.VERSION.SDK_INT>=28){
                        val source=ImageDecoder.createSource(contentResolver,selectedImage)
                        val bitmap=ImageDecoder.decodeBitmap(source)
                        uploadImageView.setImageBitmap(bitmap)
                    }else {
                        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                        uploadImageView.setImageBitmap(bitmap)
                    }
                }
            }catch (e:Exception){
                e.printStackTrace()
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    fun signInClicked(view: View){
        if(txtMail.text.toString()!=""&&txtPassword.text.toString()!=""){
            auth.signInWithEmailAndPassword(txtMail.text.toString(),txtPassword.text.toString()).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(applicationContext,auth.currentUser?.email.toString(),Toast.LENGTH_LONG).show()
                    val intent=Intent(applicationContext,CuntrActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }.addOnFailureListener(this) {exception ->
                if(exception!=null){
                    Toast.makeText(applicationContext,exception.localizedMessage.toString(),Toast.LENGTH_LONG).show()
                }

            }
        }
        else{
            Toast.makeText(applicationContext,"Lütfen gerekli bilgileri giriniz.",Toast.LENGTH_LONG).show()
        }

    }
}
/*

                        val followers= hashMapOf<String,Any>()
                        followers.put("mail","cuntr@gmail.com")
                        val following= hashMapOf<String,Any>()
                        following.put("mail","cuntr@gmail.com")

                        val sharedCuntrRequestList= hashMapOf<String,Any>()
                        sharedCuntrRequestList.put("cuntrId","Example Cuntr")
                        val sharedCuntrFollowList= hashMapOf<String,Any>()
                        sharedCuntrFollowList.put("cuntrId","Example Cuntr")



 */