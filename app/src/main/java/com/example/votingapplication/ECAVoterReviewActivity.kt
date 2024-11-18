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

class ECAVoterReviewActivity : AppCompatActivity() {

    private lateinit var votersDbRef: DatabaseReference
    private lateinit var firstName: TextView
    private lateinit var lastName: TextView
    private lateinit var aadharNum: TextView
    private lateinit var gender: TextView
    private lateinit var dob: TextView
    private lateinit var message: EditText
    private lateinit var vAadharImg: ImageView
    private lateinit var upparLayout: LinearLayout
    private lateinit var doneBtn: LinearLayout
    private lateinit var vAccept: Button
    private lateinit var vDecline: Button
    private lateinit var backToMain: Button
    private lateinit var backToMain2: Button
    private val voterIds=ArrayList<String>()
    private var idx=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eca_voter_review)


        vAccept = findViewById(R.id.vAccept)
        vDecline = findViewById(R.id.vDecline)
        firstName = findViewById(R.id.vFirstName)
        lastName = findViewById(R.id.vLastName)
        aadharNum = findViewById(R.id.vAadharNum)
        gender = findViewById(R.id.vGender)
        dob = findViewById(R.id.vDob)
        message = findViewById(R.id.vReviewMessage)
        vAadharImg = findViewById(R.id.vAadharImg)
        upparLayout = findViewById(R.id.upperLayoutVoter)
        doneBtn = findViewById(R.id.doneLayout)
        backToMain = findViewById(R.id.backToMainFromReviewVoter)
        backToMain2 = findViewById(R.id.backToMainFromReviewVoter2)

        backToMain.setOnClickListener {
            startActivity(Intent(applicationContext, ECAMainActivity::class.java))
        }

        backToMain2.setOnClickListener {
            startActivity(Intent(applicationContext, ECAMainActivity::class.java))
        }

        votersDbRef = FirebaseDatabase.getInstance().getReference("user-data")

        votersDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    if (userSnapshot.child("isVerified").value.toString() == "false") {
                        voterIds.add(userSnapshot.key.toString())
                    }
                }
                if (voterIds.isNotEmpty()) {
                    updateUser(voterIds[idx])
                } else {
                    done()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        vAccept.setOnClickListener {
            val curVoteRef = FirebaseDatabase.getInstance().getReference("user-data").child(voterIds[idx])
            curVoteRef.child("isVerified").setValue("true")
                .addOnSuccessListener {
                    Toast.makeText(
                        applicationContext, "Upload $idx", Toast.LENGTH_LONG
                    ).show()
                    idx++
                    if (idx < voterIds.size) {
                        updateUser(voterIds[idx])
                    } else {
                        done()
                    }
                }.addOnFailureListener { e -> Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show() }
        }

        vDecline.setOnClickListener {
            val curVoterRef = FirebaseDatabase.getInstance().getReference("user-data")
                .child(voterIds[idx])
            curVoterRef.child("message").setValue(message.text.toString())
                .addOnSuccessListener {
                    Toast.makeText(
                        applicationContext,
                        "Discard Success: ${voterIds.size}",
                        Toast.LENGTH_LONG
                    ).show()
                    idx++
                    if (idx < voterIds.size) {
                        updateUser(voterIds[idx])
                    } else {
                        done()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun updateUser(uid: String) {
        val voteRef = FirebaseDatabase.getInstance().getReference("user-data").child(uid)
        voteRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                firstName.text = snapshot.child("fName").value.toString()
                lastName.text = snapshot.child("lName").value.toString()
                aadharNum.text = snapshot.child("aadharNum").value.toString()
                gender.text = snapshot.child("gender").value.toString()
                dob.text = snapshot.child("dob").value.toString()

                val storageRef = FirebaseStorage.getInstance().getReference(uid).child("aadhar-file")

                val ONE_MEGABYTE: Long = 1024 * 1024
                storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytes ->
                    val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    vAadharImg.setImageBitmap(bmp)
                }.addOnFailureListener { e ->
                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
            }
        })
    }


    private fun done() {
        doneBtn.visibility = View.VISIBLE
        upparLayout.visibility = View.GONE
    }
}