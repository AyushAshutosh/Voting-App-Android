package com.example.votingapplication

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

class MakeElectionActivity : AppCompatActivity() {

    private lateinit var electionName: EditText
    private lateinit var startDate: EditText
    private lateinit var endDate: EditText
    private lateinit var makeElectionBtn: Button
    private lateinit var backToMainPage: Button
    private lateinit var startElectionDate: DatePicker
    private lateinit var endElectionDate: DatePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_election)

        electionName = findViewById(R.id.electionName)
        startDate = findViewById(R.id.startDate)
        endDate = findViewById(R.id.endDate)
        makeElectionBtn = findViewById(R.id.makeElectionBtn)
        startElectionDate = findViewById(R.id.startDatePicker)
        endElectionDate = findViewById(R.id.endDatePicker)
        backToMainPage = findViewById(R.id.backToInfoPageFromCreateElectionECA)


        backToMainPage.setOnClickListener {
            startActivity(Intent(applicationContext, ECAMainActivity::class.java))
        }

        makeElectionBtn.setOnClickListener {
            val day = startElectionDate.dayOfMonth
            val month = startElectionDate.month + 1
            val year = startElectionDate.year
            val startElectionDateFormatted = "$day/$month/$year"

            val dayE = endElectionDate.dayOfMonth
            val monthE = endElectionDate.month + 1
            val yearE = endElectionDate.year
            val endElectionDateFormatted = "$dayE/$monthE/$yearE"

            val electionRef = FirebaseDatabase.getInstance().reference
            val curElection = Election(
                    electionName.text.toString(),
                    startElectionDateFormatted,
                    endElectionDateFormatted,
                    "",
                    "false"
            )


            electionRef.child("elections").child(electionName.text.toString()).setValue(curElection).addOnSuccessListener {
                Toast.makeText(applicationContext, "Election added successfully!!", Toast.LENGTH_LONG).show()
                startActivity(Intent(applicationContext, ECAMainActivity::class.java))
            }.addOnFailureListener { e -> Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show() }
        }
    }
}