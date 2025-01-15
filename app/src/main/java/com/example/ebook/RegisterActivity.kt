package com.example.ebook

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ebook.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    // view binding
    private lateinit var binding: ActivityRegisterBinding

    // firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    // progress dialog
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        // init progress dialog, will show while creating account| Register user
        progressDialog= ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        // handle back button click
        binding.backBtn.setOnClickListener {
            onBackPressed() // goto previous screen
        }

        // handle click, begin register
        binding.registerBtn.setOnClickListener {
            /* Steps
            * 1) Input Data
            * 2) Validate Data
            * 3) Create Account - Firebase Auth
            * 4) Save User Info - Firebase Realtime Database */
            validateData()
        }
    }

    private var name = ""
    private var email = ""
    private var password = ""

    private fun validateData() {
        // 1) Input Data
        name = binding.nameEt.text.toString().trim()
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()
        val cPassword = binding.cPasswordEt.text.toString().trim()

        // 2) Validate Data
        if(name.isEmpty()){
            Toast.makeText(this,"Enter Username", Toast.LENGTH_SHORT).show()
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Invalid Email", Toast.LENGTH_SHORT).show()
        }
        else if(password.isEmpty()){
            Toast.makeText(this,"Enter Password", Toast.LENGTH_SHORT).show()
        }
        else if(cPassword.isEmpty()){
            Toast.makeText(this,"Confirm Password", Toast.LENGTH_SHORT).show()
        }
        else if (password != cPassword){
            Toast.makeText(this,"Password Doesn't Match", Toast.LENGTH_SHORT).show()
        }
        else{
            createUserAccount()
        }
    }

    private fun createUserAccount() {
        // 3) Create Account - Firebase Auth

        // show progress
        progressDialog.setMessage("Creating Account...")
        progressDialog.show()

        //create user in firebase auth
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                // account created successfully
                // Add user info in db
                updateUserInfo()
            }
            .addOnFailureListener { e->
                // Failed creating account
                progressDialog.dismiss()
                Toast.makeText(this,"Failed Creating Account due to ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }

    private fun updateUserInfo(){
        // 4) Save User Info - Firebase Realtime Database
        progressDialog.setMessage("Saving User Info...")

        // Timestamp
        val timestamp = System.currentTimeMillis()

        // Get current user, since user is registered so we can get it now
        val uid = firebaseAuth.uid

        // Setup data to add in db
        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["uid"] = uid
        hashMap["email"] = email
        hashMap["name"] = name
        hashMap["profileImage"] = "" // Add empty, update in profile edit
        hashMap["userType"] = "user" // user or admin, make admin manually
        hashMap["timestamp"] = timestamp

        // set data to db
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                // User info saved, open your dashboard
                progressDialog.dismiss()
                Toast.makeText(this,"Account Created Successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegisterActivity, DashboardUserActivity::class.java))
                finish()
            }
            .addOnFailureListener {e->
                // Failed Adding data to db
                progressDialog.dismiss()
                Toast.makeText(this,"Failed Saving User Info due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}