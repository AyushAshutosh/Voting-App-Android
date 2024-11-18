package com.example.votingapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ECAMainActivity : AppCompatActivity() {

    private lateinit var makeElectionBtn: Button
    private lateinit var reviewVoterBtn: Button
    private lateinit var reviewCandidateBtn: Button
    private lateinit var calculateResultsBtn: Button
    private lateinit var viewResultsBtn: Button
    private lateinit var logout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eca_main)

        makeElectionBtn=findViewById(R.id.makeElectionPage)
        reviewVoterBtn=findViewById(R.id.reviewVoterDetails)
        reviewCandidateBtn=findViewById(R.id.reviewCandidateDetails)
        calculateResultsBtn=findViewById(R.id.viewAndCalculateResults)
        viewResultsBtn=findViewById(R.id.viewResultsBtn)
        logout=findViewById(R.id.logoutECA)


        reviewVoterBtn.setOnClickListener {
            startActivity(Intent(applicationContext, ECAVoterReviewActivity::class.java))
        }

        logout.setOnClickListener {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
        }

        makeElectionBtn.setOnClickListener {
            startActivity(Intent(applicationContext, MakeElectionActivity::class.java))
        }

        reviewCandidateBtn.setOnClickListener {
            startActivity(Intent(applicationContext, ECACandidateReviewActivity::class.java))
        }

        calculateResultsBtn.setOnClickListener {
            startActivity(Intent(applicationContext, ElectionCalculateResults::class.java))
        }

        viewResultsBtn.setOnClickListener {
            startActivity(Intent(applicationContext, ResultsActivity::class.java))
        }
    }
}