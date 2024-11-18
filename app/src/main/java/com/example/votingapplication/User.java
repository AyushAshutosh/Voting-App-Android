package com.example.votingapplication


data class User(
        var fName: String? = null,
        var lName: String? = null,
        var gender: String? = null,
        var dob: String? = null,
        var aadharNum: String? = null,
        var hasSubmitted: String? = null,
        var isVerified: String? = null,
        var message: String? = null,
        var userId: String? = null
)