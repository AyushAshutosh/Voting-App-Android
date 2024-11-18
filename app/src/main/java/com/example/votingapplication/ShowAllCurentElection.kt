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

class ShowAllCurrentElections : AppCompatActivity() {

    private lateinit var showAllRunningElections: LinearLayout
    private lateinit var noElectionsLayout: LinearLayout
    private lateinit var electionDbRef: DatabaseReference
    private lateinit var curUser: FirebaseUser
    private lateinit var goBackToInfoPageFromAllElections: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_elections)

        showAllRunningElections = findViewById(R.id.showAllRunningElections)
        noElectionsLayout = findViewById(R.id.noOngoingElectionsLayout)
        goBackToInfoPageFromAllElections = findViewById(R.id.backToInfoPageFromAllElections)

        electionDbRef = FirebaseDatabase.getInstance().reference.child("elections")
        curUser = FirebaseAuth.getInstance().currentUser!!

        goBackToInfoPageFromAllElections.setOnClickListener {
            startActivity(Intent(applicationContext, InfoActivity::class.java))
        }

        electionDbRef.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            override fun onDataChange(snapshot: DataSnapshot) {
                var numElections = 0
                showAllRunningElections.removeAllViews()

                for (candidateSnapshot in snapshot.children) {
                    if (candidateSnapshot.child("results").hasChild(curUser.uid) || candidateSnapshot.child("isDone").value == "true") {
                        continue
                    } else {
                        numElections++
                        val curElectionBtn = Button(applicationContext).apply {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                elevation = 2f
                            }
                            id = View.generateViewId()
                            text = candidateSnapshot.child("electionName").value.toString()
                            setOnClickListener {
                                val intent = Intent(applicationContext, ElectionActivity::class.java)
                                intent.putExtra("electionName", text)
                                startActivity(intent)
                            }
                        }
                        showAllRunningElections.addView(curElectionBtn)
                    }
                }

                if (numElections == 0) {
                    noElectionsLayout.visibility = View.VISIBLE
                } else {
                    noElectionsLayout.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })

    }
}