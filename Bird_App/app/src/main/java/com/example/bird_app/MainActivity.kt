package com.example.bird_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton


class MainActivity : AppCompatActivity() {

    private lateinit var edUsername: EditText
    private lateinit var edPassword: EditText
    private lateinit var btnRegister: MaterialButton
    private lateinit var tvLogin: TextView

    private lateinit var sqlLiteHelperHelper: SQLLiteHelperHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sqlLiteHelperHelper = SQLLiteHelperHelper(this)
        initView()

        btnRegister.setOnClickListener{addUser()}
        tvLogin.setOnClickListener{
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }

    private fun addUser() {
        val username = edUsername.text.toString()
        val password = edPassword.text.toString()

        if (username.isEmpty() || password.isEmpty()){
            Toast.makeText(this,"Enter the required fields.", Toast.LENGTH_SHORT).show()
        }else{

            val std = UserModel(username = username, password = password)
            val status = sqlLiteHelperHelper.insertUser(std)

            if (status > -1){
                Toast.makeText(this,"User added ...", Toast.LENGTH_SHORT).show()
                clearEditText()
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(this,"Record not added ...", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun clearEditText() {
        edUsername.setText("")
        edPassword.setText("")
        edUsername.requestFocus()
    }

    private fun initView() {
        edUsername = findViewById(R.id.ed_username)
        edPassword = findViewById(R.id.ed_password)
        btnRegister = findViewById(R.id.btn_Register)
        tvLogin = findViewById(R.id.tv_Login)
    }
}

