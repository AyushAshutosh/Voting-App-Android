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

class RegisterActivity : AppCompatActivity() {

    private lateinit var emailTextView: EditText
    private lateinit var passwordTextView: EditText
    private lateinit var Btn: Button
    private lateinit var btnECRegister: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var goToLoginPage: Button
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        dbHelper = DBHelper(this)

        emailTextView = findViewById(R.id.email)
        passwordTextView = findViewById(R.id.passwd)
        Btn = findViewById(R.id.btnregister)
        progressBar = findViewById(R.id.progressbar)
        goToLoginPage = findViewById(R.id.goToLoginPage)
        btnECRegister = findViewById(R.id.btnECRegister)

        Btn.setOnClickListener {
            registerNewUser("ec")
        }

        btnECRegister.setOnClickListener {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
        }
        goToLoginPage.setOnClickListener {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
        }
    }

    private fun registerNewUser(type: String) {
        progressBar.visibility = View.VISIBLE

        val email = emailTextView.text.toString()
        val password = passwordTextView.text.toString()

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(applicationContext, "Please enter email!!", Toast.LENGTH_LONG).show()
            return
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(applicationContext, "Please enter password!!", Toast.LENGTH_LONG).show()
            return
        }
        if (password.length < 6) {
            Toast.makeText(applicationContext, "Password must be greater than 6 characters!!", Toast.LENGTH_LONG).show()
            passwordTextView.setBackgroundResource(R.drawable.error_border)
            return
        }

        if (dbHelper.isUserExists(email)) {
            Toast.makeText(applicationContext, "User already exists!", Toast.LENGTH_LONG).show()
            progressBar.visibility = View.GONE
            return
        }

        val isRegistered = dbHelper.insertUser(email, password)

        if (isRegistered) {
            Toast.makeText(applicationContext, "Registration successful!!", Toast.LENGTH_LONG).show()
            progressBar.visibility = View.GONE

            val intent = if (type == "voter") {
                Intent(this@RegisterActivity, LoginActivity::class.java)
            } else {
                Intent(this@RegisterActivity, RegisterActivity::class.java)
            }
            startActivity(intent)
        } else {
            Toast.makeText(applicationContext, "Registration failed! Please try again later", Toast.LENGTH_LONG).show()
            progressBar.visibility = View.GONE
        }
    }

    fun isCurUserLoggedIn(): Boolean {
        // SQLite doesn't handle sessions like Firebase; here, you might check if the user is active based on app logic
        return false
    }

    fun logout() {
        // Handle logout, for SQLite you might reset session data if applicable
    }
}
