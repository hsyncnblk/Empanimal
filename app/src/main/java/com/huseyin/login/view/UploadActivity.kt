package com.huseyin.login.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue.serverTimestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.huseyin.login.R
import com.huseyin.login.databinding.ActivityUploadActivityBinding
import java.util.*

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadActivityBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedPicture : Uri? = null
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //window.statusBarColor = ContextCompat.getColor(this,R.color.renk);
       getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.renk));

       registerLauncher()

        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage

        binding.button3.setOnClickListener{
            val intent = Intent(applicationContext, FeeddActivity::class.java)
            startActivity(intent)
        }


    }


    fun upload(view: View) {

       val uuid = UUID.randomUUID()
        val imageName="$uuid.jpg"

        val reference =storage.reference
        val imageReference = reference.child("images").child(imageName)

        imageReference.putFile(selectedPicture!!)
        if (selectedPicture != null){

            imageReference.putFile(selectedPicture!!).addOnSuccessListener {

                val uploadPictureReference = storage.reference.child("images").child(imageName)
                uploadPictureReference.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()

                    if (auth.currentUser != null){
                        val postMap = hashMapOf<String, Any>()
                        postMap.put("downloadUrl",downloadUrl)
                        postMap.put("userEmail",auth.currentUser!!.email!!)
                        postMap.put("icerik",binding.commentText.text.toString())
                        postMap.put("date", serverTimestamp())


                        firestore.collection("Posts").add(postMap).addOnSuccessListener {

                            finish()

                        }.addOnFailureListener{

                            Toast.makeText(this@UploadActivity,it.localizedMessage, Toast.LENGTH_LONG).show()
                        }

                    }


                }


            }.addOnFailureListener{
                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }

    }

   fun selectImage(view: View) {

       if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view, "galeriye girmek için izin gerekli", Snackbar.LENGTH_INDEFINITE).setAction("Give permisson") {
                       permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }.show()

            } else {

                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

            }
       } else {

            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

            activityResultLauncher.launch(intentToGallery)
       }


    }

     private fun registerLauncher() {

           activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

               if (result.resultCode == RESULT_OK) {
                   val intentFromResult = result.data

                   if (intentFromResult != null) {
                       selectedPicture = intentFromResult.data
                       selectedPicture?.let {
                           binding.imageView4.setImageURI(it)
                       }
                   }
               }
           }

         permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
             if (result) {
                 val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                 activityResultLauncher.launch(intentToGallery)

             } else {
                 Toast.makeText(this@UploadActivity, "İzin Gerekli!!!", Toast.LENGTH_LONG).show()
             }

         }


     }


  }

