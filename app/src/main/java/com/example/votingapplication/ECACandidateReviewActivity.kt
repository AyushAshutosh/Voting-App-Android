package com.example.votingapplication

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class ECACandidateReviewActivity : AppCompatActivity() {

    private lateinit var candidatesDbRef: DatabaseReference
    private lateinit var firstName: TextView
    private lateinit var lastName: TextView
    private lateinit var aadharNum: TextView
    private lateinit var gender: TextView
    private lateinit var dob: TextView
    private lateinit var partyName: TextView
    private lateinit var upparLayout: LinearLayout
    private lateinit var done: LinearLayout
    private lateinit var message: EditText
    private lateinit var cAccept: Button
    private lateinit var cDecline: Button
    private lateinit var backToMain: Button
    private lateinit var backToMain2: Button
    private lateinit var cAadharImg: ImageView
    private val candidatesId = arrayListOf<String>()
    private var idx = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eca_ec_review)

        backToMain = findViewById(R.id.backToMainFromReviewCandidate)
        backToMain2 = findViewById(R.id.backToMainFromReviewCandidate2)
        cAccept = findViewById(R.id.cAccept)
        cDecline = findViewById(R.id.cDecline)
        firstName = findViewById(R.id.cFirstName)
        lastName = findViewById(R.id.cLastName)
        aadharNum = findViewById(R.id.cAadharNum)
        gender = findViewById(R.id.cGender)
        dob = findViewById(R.id.cDob)
        message = findViewById(R.id.cReviewMessage)
        cAadharImg = findViewById(R.id.cAadharImg)
        upparLayout = findViewById(R.id.upperLayout)
        done = findViewById(R.id.doneLayout)
        partyName = findViewById(R.id.cProfileName)

        backToMain.setOnClickListener {
            startActivity(Intent(applicationContext, ECAMainActivity::class.java))
        }
        backToMain2.setOnClickListener {
            startActivity(Intent(applicationContext, ECAMainActivity::class.java))
        }

        candidatesDbRef = FirebaseDatabase.getInstance().getReference("election-candidates")
        candidatesDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (candidateSnapshot in snapshot.children) {
                    if (candidateSnapshot.child("isVerified").value.toString() == "false") {
                        candidatesId.add(candidateSnapshot.key!!)
                    }
                }
                try {
                    if (candidatesId.isNotEmpty()) updateUser(candidatesId[idx]) else done()
                } catch (e: Exception) {
                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {}

        })

        cAccept.setOnClickListener {
            val curCandidateRef = FirebaseDatabase.getInstance().getReference("election-candidates").child(candidatesId[idx])
            curCandidateRef.child("isVerified").setValue("true").addOnSuccessListener {
                Toast.makeText(applicationContext, "Update $idx", Toast.LENGTH_LONG).show()
                idx++
                if (idx < candidatesId.size) {
                    updateUser(candidatesId[idx])
                } else {
                    done()
                }
            }.addOnFailureListener { e -> Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show() }
        }

        cDecline.setOnClickListener {
            val curCandidateRef = FirebaseDatabase.getInstance().getReference("election-candidates").child(candidatesId[idx])
            curCandidateRef.child("message").setValue(message.text.toString()).addOnSuccessListener {
                Toast.makeText(applicationContext, "Discard Success: ${candidatesId.size}", Toast.LENGTH_LONG).show()
                idx++
                if (idx < candidatesId.size) {
                    updateUser(candidatesId[idx])
                } else {
                    done()
                }
            }.addOnFailureListener { e -> Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show() }
        }
    }


    private fun updateUser(uid: String) {
        val candidateRef = FirebaseDatabase.getInstance().reference
            .child("election-candidates")
            .child(uid)
        candidateRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                firstName.text = snapshot.child("fName").value.toString()
                lastName.text = snapshot.child("lName").value.toString()
                aadharNum.text = snapshot.child("aadharNum").value.toString()
                gender.text = snapshot.child("gender").value.toString()
                dob.text = snapshot.child("dob").value.toString()
                partyName.text = snapshot.child("pName").value.toString()

                val aadharImgRef = FirebaseStorage.getInstance().reference
                    .child(uid)
                    .child("aadhar-file")
                val ONE_MEGABYTE: Long = 1024 * 1024
                aadharImgRef.getBytes(ONE_MEGABYTE)
                    .addOnSuccessListener { bytes ->
                        val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        cAadharImg.setImageBitmap(bmp)
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(applicationContext, exception.message, Toast.LENGTH_LONG).show()
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
            }
        })

    }

    private fun done() {
        done.visibility = View.VISIBLE
        upparLayout.visibility = View.GONE
    }
}