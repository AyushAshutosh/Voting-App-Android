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

class ECInfoActivity : AppCompatActivity() {

    private lateinit var goToEditPage: Button
    private lateinit var goToResultsPage: Button
    private lateinit var logout: Button
    private lateinit var messageText: TextView
    private lateinit var cardView: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ec_info)

        goToEditPage=findViewById(R.id.goToMainPage)
        goToResultsPage=findViewById(R.id.goToResultsPage)
        messageText=findViewById(R.id.messageTxt)
        cardView=findViewById(R.id.reviewTextCandidate)
        logout=findViewById(R.id.logoutCandidate)

        val curCandidate: FirebaseUser?=FirebaseAuth.getInstance().currentUser

        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
        }


        val voterRef: DatabaseReference=FirebaseDatabase.getInstance()
            .getReference("election-candidates")
            .child(curCandidate?.uid.orEmpty())

        voterRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child("isVerified").value.toString() == "true") {
                    messageText.text="Profile verified. Authorized for enrolling in elections."
                    cardView.setCardBackgroundColor(Color.GREEN)
                    messageText.setTextColor(Color.BLACK)
                } else if (snapshot.child("message").value.toString().isNotEmpty()) {
                    messageText.text=
                        "Status is Declined. ${snapshot.child("message").value.toString()}"
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })


        goToEditPage.setOnClickListener {
            startActivity(Intent(applicationContext, ECMainActivity::class.java))
        }

        goToResultsPage.setOnClickListener {
            startActivity(Intent(applicationContext, ResultsActivity::class.java))
        }
    }
}