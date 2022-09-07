package com.huseyin.login.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.huseyin.login.R

import com.huseyin.login.databinding.ActivityMainBinding
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.renk));

        auth = Firebase.auth

        val currentUser= auth.currentUser

        if (currentUser != null){
            val intent = Intent(this, FeeddActivity::class.java)
            startActivity(intent)
            finish()
        }


    }

    fun SignInClicked(view: View){

        val email= binding.emailText.text.toString()
        val password =binding.passwordText.text.toString()

        if(email.equals("") || password.equals("")){

            Toast.makeText(this , "email ve şifreyi eksiksiz gir!!!", Toast.LENGTH_LONG).show()
        }else {

            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener {
                val intent =Intent(this@MainActivity, FeeddActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener{

                Toast.makeText(this@MainActivity,it.localizedMessage,Toast.LENGTH_LONG).show()

            }
        }

    }

    fun SignUpClicked(view: View){

        val email =binding.emailText.text.toString()
        val password= binding.passwordText.text.toString()

        if (email.equals("") || password.equals("")){

            Toast.makeText(this , "email ve şifreyi eksiksiz gir!!!", Toast.LENGTH_LONG).show()
        }else {
            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener{
                val intent= Intent(this@MainActivity, FeeddActivity::class.java)
                startActivity(intent)
                finish()

            }.addOnFailureListener {
                Toast.makeText(this@MainActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }




    }
}