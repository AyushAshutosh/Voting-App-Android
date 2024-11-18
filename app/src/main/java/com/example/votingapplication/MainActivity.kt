package com.example.votingapplication

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_FILES = 1
        private const val TAG = "MainActivity"
    }

    // Initialize properties with default values where possible
    private var uid: String = ""
    private var submitBtn: Button? = null
    private var firstName: EditText? = null
    private var lastName: EditText? = null
    private var aadharNumber: EditText? = null
    private var genderMale: RadioButton? = null
    private var genderFemale: RadioButton? = null
    private var genderOthers: RadioButton? = null
    private var genderGrp: RadioGroup? = null
    private var datePicker: DatePicker? = null
    private var chooseFileBtn: Button? = null
    private var goBackToInfoPage: Button? = null
    private var file: Uri? = null
    private var dbRef: DatabaseReference? = null
    private var storageRef: StorageReference? = null
    private var user: FirebaseUser? = null
    private var uploadTask: UploadTask? = null
    private var cardView: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            Log.d(TAG, "Starting MainActivity initialization")

            if (!initializeFirebase()) {
                Log.e(TAG, "Firebase initialization failed")
                return
            }

            if (!initializeViews()) {
                Log.e(TAG, "View initialization failed")
                return
            }

            setupDataListener()
            setupClickListener()

            Log.d(TAG, "MainActivity initialization completed")

        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate: ${e.message}")
            handleFatalError(e)
        }
    }

    private fun initializeFirebase(): Boolean {
        return try {
            Log.d(TAG, "Initializing Firebase")

            // Check if Firebase is already initialized
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
            }

            user = FirebaseAuth.getInstance().currentUser

            if (user == null) {
                Log.d(TAG, "User not authenticated")
                redirectToLogin("Please sign in to continue")
                return false
            }

            uid = user?.uid ?: ""
            if (uid.isEmpty()) {
                Log.e(TAG, "Empty UID after authentication")
                redirectToLogin("Authentication error")
                return false
            }

            storageRef = FirebaseStorage.getInstance().reference
            dbRef = FirebaseDatabase.getInstance().reference

            Log.d(TAG, "Firebase initialization successful")
            true

        } catch (e: Exception) {
            Log.e(TAG, "Firebase initialization error: ${e.message}")
            handleFatalError(e)
            false
        }
    }

    private fun initializeViews(): Boolean {
        return try {
            Log.d(TAG, "Initializing views")

            submitBtn = findViewById(R.id.submitBtn)
            goBackToInfoPage = findViewById(R.id.goBackToInfoPage)
            firstName = findViewById(R.id.firstName)
            lastName = findViewById(R.id.lastName)
            aadharNumber = findViewById(R.id.aadharNumber)
            genderMale = findViewById(R.id.male)
            genderFemale = findViewById(R.id.female)
            genderOthers = findViewById(R.id.others)
            datePicker = findViewById(R.id.datePicker)
            chooseFileBtn = findViewById(R.id.chooseFileBtn)
            genderGrp = findViewById(R.id.genderGrp)
            cardView = findViewById(R.id.uploadVoter)

            // Verify critical views are initialized
            if (submitBtn == null || firstName == null || lastName == null) {
                throw Exception("Critical views not found in layout")
            }

            Log.d(TAG, "Views initialization successful")
            true

        } catch (e: Exception) {
            Log.e(TAG, "View initialization error: ${e.message}")
            handleFatalError(e)
            false
        }
    }

    private fun setupDataListener() {
        if (!isUserAuthenticated()) {
            Log.e(TAG, "Cannot setup data listener - user not authenticated")
            return
        }

        try {
            val subDbRef = dbRef?.child("user-data")?.child(uid)
            subDbRef?.addValueEventListener(object : ValueEventListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        if (snapshot.exists() &&
                            snapshot.hasChild("hasSubmitted") &&
                            snapshot.child("hasSubmitted").value.toString() == "true"
                        ) {
                            firstName?.setText(snapshot.child("fName").value?.toString() ?: "")
                            lastName?.setText(snapshot.child("lName").value?.toString() ?: "")

                            when (snapshot.child("gender").value?.toString()) {
                                "male" -> genderMale?.isChecked = true
                                "female" -> genderFemale?.isChecked = true
                                else -> genderOthers?.isChecked = true
                            }

                            aadharNumber?.setText(snapshot.child("aadharNum").value?.toString() ?: "")
                            goBackToInfoPage?.visibility = View.VISIBLE
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error processing data change: ${e.message}")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Database error: ${error.message}")
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up data listener: ${e.message}")
        }
    }

    private fun setupClickListener() {
        chooseFileBtn?.setOnClickListener {
            try {
                val intent = Intent().apply {
                    type = "/"
                    action = Intent.ACTION_GET_CONTENT
                }
                startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_CODE_FILES)
            } catch (e: Exception) {
                Log.e(TAG, "Error in file chooser: ${e.message}")
                showToast("Error selecting file")
            }
        }

        submitBtn?.setOnClickListener {
            handleSubmission()
        }

        goBackToInfoPage?.setOnClickListener {
            try {
                startActivity(Intent(this, InfoActivity::class.java))
            } catch (e: Exception) {
                Log.e(TAG, "Error navigating to InfoActivity: ${e.message}")
                showToast("Error navigating to info page")
            }
        }
    }

    private fun handleSubmission() {
        try {
            val fName = firstName?.text?.toString()?.trim() ?: ""
            val lName = lastName?.text?.toString()?.trim() ?: ""
            val aadharNum = aadharNumber?.text?.toString()?.trim() ?: ""

            val gender = when {
                genderMale?.isChecked == true -> "male"
                genderFemale?.isChecked == true -> "female"
                else -> "others"
            }

            val dob = "${datePicker?.dayOfMonth ?: 1}/${(datePicker?.month ?: 0) + 1}/${datePicker?.year ?: 2000}"

            when {
                fName.isEmpty() -> showToast("First Name Cannot Be Empty")
                lName.isEmpty() -> showToast("Last Name Cannot Be Empty")
                fName.contains(Regex("\\d")) -> showToast("First Name cannot have a number")
                lName.contains(Regex("\\d")) -> showToast("Last Name cannot have a number")
                aadharNum.length != 12 -> showToast("Aadhar Number must be a 12-digit number")
                else -> submitUserData(fName, lName, gender, dob, aadharNum)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in submission: ${e.message}")
            showToast("Error processing submission")
        }
    }

    private fun submitUserData(fName: String, lName: String, gender: String, dob: String, aadharNum: String) {
        if (!isUserAuthenticated()) return

        val curUser = User(fName, lName, gender, dob, aadharNum, "true", "false", "", uid)
        dbRef?.child("user-data")?.child(uid)?.setValue(curUser)
            ?.addOnSuccessListener {
                showToast("Submitted Successfully!!")
            }
            ?.addOnFailureListener { e ->
                Log.e(TAG, "Submission error: ${e.message}")
                showToast("Submission failed")
            }
    }

    private fun isUserAuthenticated(): Boolean {
        return user != null && uid.isNotEmpty()
    }

    private fun handleFatalError(e: Exception) {
        Log.e(TAG, "Fatal error: ${e.message}")
        showToast("An error occurred. Please try again.")
        finish()
    }

    private fun redirectToLogin(message: String) {
        Log.d(TAG, "Redirecting to login: $message")
        showToast(message)
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode == REQUEST_CODE_FILES && resultCode == RESULT_OK && data?.data != null) {
                file = data.data
                cardView?.visibility = View.VISIBLE
                file?.let { uri -> putFileInStorage(uri) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in activity result: ${e.message}")
            showToast("Error processing file")
        }
    }

    private fun putFileInStorage(file: Uri) {
        if (!isUserAuthenticated()) return

        try {
            val upload = storageRef?.child(uid)?.child("aadhar-file")
            upload?.putFile(file)?.also { task ->
                uploadTask = task
            }?.addOnSuccessListener {
                showToast("Aadhar Image Uploaded Successfully!!")
                cardView?.visibility = View.GONE
            }?.addOnFailureListener { e ->
                Log.e(TAG, "File upload error: ${e.message}")
                showToast("File upload failed")
                cardView?.visibility = View.GONE
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in file storage: ${e.message}")
            showToast("Error uploading file")
            cardView?.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        try {
            uploadTask?.cancel()
            super.onDestroy()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onDestroy: ${e.message}")
        }
    }
}