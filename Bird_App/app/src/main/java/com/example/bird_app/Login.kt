package com.example.bird_app

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton

class Login : AppCompatActivity() {

    private lateinit var edUsername: EditText
    private lateinit var edPassword: EditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var tvLogin: TextView

    private lateinit var sqlLiteHelperHelper: SQLLiteHelperHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sqlLiteHelperHelper = SQLLiteHelperHelper(this)
        initView()

        btnLogin.setOnClickListener{checkLogin()}

        tvLogin.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkLogin() {
        val username = edUsername.text.toString()
        val password = edPassword.text.toString()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter the required fields.", Toast.LENGTH_SHORT).show()
        } else {
            val userList = sqlLiteHelperHelper.getAllUser()

            var isLoggedIn = false

            for (user in userList) {
                if (user.username == username && user.password == password) {
                    isLoggedIn = true
                    break
                }
            }

            if (isLoggedIn) {
                saveUsernameToSharedPreferences(username)
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                startLogin()
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startLogin() {

        val intent = Intent(this, HomePage::class.java)
        startActivity(intent)
    }


    private fun clearEditText() {
        edUsername.setText("")
        edPassword.setText("")
        edUsername.requestFocus()
    }

    private fun initView() {
        edUsername = findViewById(R.id.ed_username2)
        edPassword = findViewById(R.id.ed_password2)
        btnLogin = findViewById(R.id.btn_Login)
        tvLogin = findViewById(R.id.tv_Register)
    }

    private fun saveUsernameToSharedPreferences(username: String) {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.apply()
    }
}