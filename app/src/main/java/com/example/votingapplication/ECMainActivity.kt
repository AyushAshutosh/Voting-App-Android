package com.example.votingapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask

class ECMainActivity : AppCompatActivity() {

    private lateinit var uid: String
    private lateinit var submitBtn: Button
    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var aadharNumber: EditText
    private lateinit var partyName: EditText
    private lateinit var genderMale: RadioButton
    private lateinit var genderFemale: RadioButton
    private lateinit var genderOthers: RadioButton
    private lateinit var genderGrp: RadioGroup
    private lateinit var datePicker: DatePicker
    private lateinit var chooseFileBtn: Button
    private lateinit var goBackToInfoPage: Button
    private var file: Uri?=null
    private lateinit var dbRef: DatabaseReference
    private lateinit var storageRef: StorageReference
    private lateinit var user: FirebaseUser
    private var uploadTask: UploadTask? = null
    private lateinit var cardView: LinearLayout

    companion object {
        private const val REQUEST_CODE_FILES=1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ec_main)

        user=FirebaseAuth.getInstance().currentUser!!
        uid=user.uid
        storageRef=FirebaseStorage.getInstance().reference
        dbRef=FirebaseDatabase.getInstance().reference


        submitBtn=findViewById(R.id.submitBtn)
        goBackToInfoPage=findViewById(R.id.goBackToInfoPage)
        firstName=findViewById(R.id.firstName)
        lastName=findViewById(R.id.lastName)
        partyName=findViewById(R.id.partyName)
        aadharNumber=findViewById(R.id.aadharNumber)
        genderMale=findViewById(R.id.male)
        genderFemale=findViewById(R.id.female)
        genderOthers=findViewById(R.id.others)
        datePicker=findViewById(R.id.datePicker)
        chooseFileBtn=findViewById(R.id.chooseFileBtn)
        genderGrp=findViewById(R.id.genderGrp)
        cardView=findViewById(R.id.uploadEC)

        try {
            val subDbRef=dbRef.child("election-candidates").child(uid)
            subDbRef.addValueEventListener(object : ValueEventListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild("hasSubmitted") && snapshot.child("hasSubmitted").value.toString() == "true") {
                        val fName=snapshot.child("fName").value.toString()
                        val lName=snapshot.child("lName").value.toString()
                        val dob=snapshot.child("dob").value.toString()
                        val aadharNum=snapshot.child("aadharNum").value.toString()
                        val gender=snapshot.child("gender").value.toString()
                        val pName=snapshot.child("pName").value.toString()
                        firstName.setText(fName)
                        lastName.setText(lName)
                        partyName.setText(pName)
                        genderGrp.clearCheck()
                        when (gender) {
                            "male" -> genderMale.isChecked=true
                            "female" -> genderFemale.isChecked=true
                            else -> genderOthers.isChecked=true
                        }
                        aadharNumber.setText(aadharNum)
                        datePicker.updateDate(2000, 11, 24)
                        goBackToInfoPage.visibility=View.VISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
                }
            })
        } catch (e: Exception) {
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
        }


        chooseFileBtn.setOnClickListener {
            val intent=Intent(Intent.ACTION_GET_CONTENT)
            intent.type="/"
            startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_CODE_FILES)
        }

        submitBtn.setOnClickListener {
            val fName=firstName.text.toString()
            val lName=lastName.text.toString()
            val aadharNum=aadharNumber.text.toString()
            val pName=partyName.text.toString()
            val gender=when {
                genderMale.isChecked -> "male"
                genderFemale.isChecked -> "female"
                else -> "others"
            }
            val day=datePicker.dayOfMonth
            val month=datePicker.month
            val year=datePicker.year
            val dob="$day/${month + 1}/$year"

            val curCandidate=
                Candidate(fName, lName, gender, pName, dob, aadharNum, "true", "false", "", uid)
            dbRef.child("election-candidates").child(uid).setValue(curCandidate)
                .addOnSuccessListener {
                    Toast.makeText(
                        applicationContext,
                        "Submitted Successfully! Under Review!",
                        Toast.LENGTH_LONG
                    ).show()
                    startActivity(Intent(applicationContext, ECInfoActivity::class.java))
                }.addOnFailureListener { e ->
                    Toast.makeText(
                        applicationContext,
                        e.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
        }

        goBackToInfoPage.setOnClickListener {
            startActivity(Intent(applicationContext, ECInfoActivity::class.java))
        }
    }


    @SuppressLint("SuspiciousIndentation")
    private fun putFileInStorage(file: Uri) {
        val upload = storageRef.child(uid).child("aadhar-file")
        val uploadTask = upload.putFile(file)
        uploadTask.addOnSuccessListener {
            Toast.makeText(applicationContext, "Aadhar Upload Successfully.", Toast.LENGTH_LONG).show()
            cardView.visibility=View.GONE
        }.addOnFailureListener { e ->
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
            cardView.visibility=View.GONE
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        cardView.visibility = View.VISIBLE
        if (requestCode == REQUEST_CODE_FILES && resultCode == RESULT_OK && data?.data != null) {
            file = data.data
            file?.let { putFileInStorage(it) }
        }
    }
}