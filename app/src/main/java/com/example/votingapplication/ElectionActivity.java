package com.example.votingapplication

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError

class ElectionActivity : AppCompatActivity() {

    private lateinit var chooseElectionCandidateBtn: Button
    private lateinit var goToAllElections: Button
    private lateinit var electionCandidatesRadioGroup: RadioGroup
    private lateinit var electionTxtView: TextView
    private lateinit var message: EditText
    private var curElectionName: String? = null
    private lateinit var candidateDbRef: DatabaseReference
    private lateinit var electionDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_election)

        curElectionName = intent.getStringExtra("electionName")
        chooseElectionCandidateBtn = findViewById(R.id.chooseElectionCandidateBtn)
        electionCandidatesRadioGroup = findViewById(R.id.electionCandidatesRadioGroup)
        electionTxtView = findViewById(R.id.electionTxtView)
        goToAllElections = findViewById(R.id.goToAllElections)

        electionTxtView.text = curElectionName
        candidateDbRef = FirebaseDatabase.getInstance().getReference("election-candidates")
        electionDbRef = FirebaseDatabase.getInstance().getReference("elections")

        goToAllElections.setOnClickListener {
            startActivity(Intent(this, ShowAllCurrentElections::class.java))
        }

        candidateDbRef.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            override fun onDataChange(snapshot: DataSnapshot) {
                for (candidateSnapshot in snapshot.children) {
                    if (candidateSnapshot.child("isVerified").value.toString() == "true") {
                        val newCandidate = RadioButton(this@ElectionActivity).apply {
                            setTextColor(Color.WHITE)
                            id = View.generateViewId()
                            val partyName = candidateSnapshot.child("pName").value.toString()
                            val candidateFName = candidateSnapshot.child("fName").value.toString()
                            val candidateLName = candidateSnapshot.child("lName").value.toString()
                            text = "$partyName - $candidateFName $candidateLName"
                        }
                        electionCandidatesRadioGroup.addView(newCandidate)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ElectionActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })

        chooseElectionCandidateBtn.setOnClickListener {
            val selectedId = electionCandidatesRadioGroup.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(this, "Please select a candidate", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val selectedCandidate = findViewById<RadioButton>(selectedId).text.toString().split(" ")[0]
            val curUser = FirebaseAuth.getInstance().currentUser
            if (curUser != null) {
                val electionRef = electionDbRef.child(curElectionName!!).child("results").child(curUser.uid)
                electionRef.setValue(selectedCandidate).addOnSuccessListener {
                    Toast.makeText(this, "Candidate Selected", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, ShowAllCurrentElections::class.java))
                }
            } else {
                Toast.makeText(this, "User not authenticated", Toast.LENGTH_LONG).show()
            }
        }
    }
}