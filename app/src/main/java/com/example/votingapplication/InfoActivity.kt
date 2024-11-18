package com.example.votingapplication

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class InfoActivity : AppCompatActivity() {

    private lateinit var goToPendingElectionPage: Button
    private lateinit var goToEditPage: Button
    private lateinit var vGoToResultsPage: Button
    private lateinit var logout: Button
    private lateinit var cardView: CardView
    private lateinit var messageText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        goToPendingElectionPage = findViewById(R.id.goToPendingElectionsActivity)
        goToEditPage = findViewById(R.id.goToMainPage)
        vGoToResultsPage = findViewById(R.id.vGoToResultsPage)
        messageText = findViewById(R.id.messageTxt)
        logout = findViewById(R.id.logout)
        cardView = findViewById(R.id.reviewTextVoter)


        val curUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(applicationContext, LoginActivity::class.java))
        }

        val voterRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("user-data").child(curUser?.uid ?: "")

        voterRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isVerified = snapshot.child("isVerified").value.toString()
                if (isVerified == "true") {
                    messageText.text = "Profile verified. Authorized for voting."
                    cardView.setCardBackgroundColor(Color.parseColor("#4CAF50"))
                } else {
                    val fmsg = snapshot.child("message").value.toString().takeIf { it.isNotEmpty() }
                        ?: "Profile is under Review. Not yet authorized for Voting."
                    messageText.text = if (fmsg == "Profile is under Review. Not yet authorized for Voting.") {
                        fmsg
                    } else {
                        "Status is Declined. $fmsg"
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })

        goToPendingElectionPage.setOnClickListener {
            val intent = Intent(this@InfoActivity, ShowAllCurrentElections::class.java)
            startActivity(intent)
        }

        goToEditPage.setOnClickListener {
            val intent = Intent(this@InfoActivity, MainActivity::class.java)
            startActivity(intent)
        }

        vGoToResultsPage.setOnClickListener {
            val intent = Intent(this@InfoActivity, ResultsActivity::class.java)
            startActivity(intent)
        }
    }
}

