package com.example.votingapplication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var emailTextView: EditText
    private lateinit var passwordTextView: EditText
    private lateinit var Btn: Button
    private lateinit var btnECA: Button
    private lateinit var btnEC: Button
    private lateinit var backToRegisterBtn: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var dbHelper: DBHelper

    companion object {
        var curUserType: String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize DBHelper
        dbHelper = DBHelper(this)

        // Bind views
        emailTextView = findViewById(R.id.emailLogin)
        passwordTextView = findViewById(R.id.password)
        Btn = findViewById(R.id.login)
        btnECA = findViewById(R.id.loginECA)
        progressBar = findViewById(R.id.progressBar)
        btnEC = findViewById(R.id.loginEC)
        backToRegisterBtn = findViewById(R.id.backToRegisterBtn)

        // Login as a regular user (voter)
        Btn.setOnClickListener {
            loginUserAccount("voter")
        }

        // Login as an election commission (EC)
        btnEC.setOnClickListener {
            loginUserAccount("ec")
        }

        // Login as admin
        btnECA.setOnClickListener {
            loginAsAdmin()
        }

        // Navigate back to register screen
        backToRegisterBtn.setOnClickListener {
            startActivity(Intent(applicationContext, RegisterActivity::class.java))
        }


    }

    // Admin login check
    private fun loginAsAdmin() {
        progressBar.visibility = View.VISIBLE
        val email = emailTextView.text.toString()
        val password = passwordTextView.text.toString()

        if (!email.contains("@") || !email.contains(".")) {
            Toast.makeText(applicationContext, "Invalid email format", Toast.LENGTH_LONG).show()
            progressBar.visibility = View.GONE
            return
        }

        if (email == "admin@gov.in" && password == "admin123") {
            curUserType = "eca"
            Toast.makeText(applicationContext, "Admin login successful", Toast.LENGTH_LONG).show()
            startActivity(Intent(applicationContext, ECAMainActivity::class.java))
        } else {
            Toast.makeText(applicationContext, "Admin login failed", Toast.LENGTH_LONG).show()
            progressBar.visibility = View.GONE
        }
    }


    private fun loginUserAccount(type: String) {
        progressBar.visibility = View.VISIBLE
        curUserType = type

        val email = emailTextView.text.toString()
        val password = passwordTextView.text.toString()

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(applicationContext, "Please enter email!!", Toast.LENGTH_LONG).show()
            progressBar.visibility = View.GONE
            return
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(applicationContext, "Please enter password!!", Toast.LENGTH_LONG).show()
            progressBar.visibility = View.GONE
            return
        }


        val isValidUser = dbHelper.validateUser(email, password)
        if (isValidUser) {
            Toast.makeText(applicationContext, "Login successful!!", Toast.LENGTH_LONG).show()
            progressBar.visibility = View.GONE

            val intent = if (type == "voter") {
                Intent(this@LoginActivity, InfoActivity::class.java)
            } else {
                Intent(this@LoginActivity, RegisterActivity::class.java)
            }
            startActivity(intent)
        } else {
            Toast.makeText(applicationContext, "Login failed! Invalid credentials.", Toast.LENGTH_LONG).show()
            progressBar.visibility = View.GONE
        }
    }

    fun isCurUserLoggedIn(): Boolean {
        return false
    }

    fun logout() {}
}
