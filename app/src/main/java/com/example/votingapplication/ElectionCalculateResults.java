package com.example.votingapplication

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
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

class ElectionCalculateResults : AppCompatActivity() {

    private lateinit var showAllRunningElectionsToCalculate: LinearLayout
    private lateinit var electionDbRef: DatabaseReference
    private lateinit var curUser: FirebaseUser
    private lateinit var backToMainPage: Button
    private lateinit var linearLayout: LinearLayout

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculate_elections)

        showAllRunningElectionsToCalculate = findViewById(R.id.showAllRunningElectionsToCalculate)
        backToMainPage = findViewById(R.id.backToInfoPageFromMainECA)
        linearLayout = findViewById(R.id.noOngoingElectionsToEndLayout)

        electionDbRef = FirebaseDatabase.getInstance().getReference("elections")
        curUser = FirebaseAuth.getInstance().currentUser!!

                backToMainPage.setOnClickListener {
            startActivity(Intent(this, ECAMainActivity::class.java))
        }


        electionDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var numElections = 0

                for (candidateSnapshot in snapshot.children) {
                    val isDone = candidateSnapshot.child("isDone").value.toString()
                    if (isDone == "true") {
                        continue
                    } else {
                        numElections++
                        val curElectionBtn = Button(this@ElectionCalculateResults).apply {
                            id = View.generateViewId()
                            text = candidateSnapshot.child("electionName").value.toString()
                            setOnClickListener {
                                calculateResults(candidateSnapshot, this)
                            }
                        }
                        showAllRunningElectionsToCalculate.addView(curElectionBtn)
                    }
                }

                if (numElections == 0) {
                    linearLayout.visibility = View.VISIBLE
                } else {
                    linearLayout.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ElectionCalculateResults, error.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun calculateResults(candidateSnapshot: DataSnapshot, curElectionBtn: Button) {
        val results = hashMapOf<String, Int>()

        candidateSnapshot.ref.child("results").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(innerSnapshot: DataSnapshot) {
                for (resultSnapshot in innerSnapshot.children) {
                    val partyName = resultSnapshot.value.toString()
                    results[partyName] = results.getOrDefault(partyName, 0) + 1
                }

                for ((key, value) in results) {
                    candidateSnapshot.ref.child("candidate-results").child(key).setValue(value.toString())
                }

                Toast.makeText(this@ElectionCalculateResults, "Calculated", Toast.LENGTH_LONG).show()
                curElectionBtn.visibility = View.GONE
                candidateSnapshot.child("isDone").ref.setValue("true")
                finish()
                startActivity(intent)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ElectionCalculateResults, error.message, Toast.LENGTH_LONG).show()
            }
        })
    }
}