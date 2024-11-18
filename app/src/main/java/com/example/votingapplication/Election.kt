package com.example.votingapplication

data class Election(
    var electionName: String,
    var startDate: String,
    var endDate: String,
    var results: String,
    var isDone: String
)