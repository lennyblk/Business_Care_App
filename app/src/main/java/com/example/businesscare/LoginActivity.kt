package com.example.businesscare

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.businesscare.api.ApiConfig
import com.example.businesscare.models.LoginRequest
import com.example.businesscare.models.LoginResponse
import kotlinx.coroutines.launch
import com.example.businesscare.api.ApiService
import android.util.Log


class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var companyNameEditText: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialisation des vues
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        companyNameEditText = findViewById(R.id.companyNameEditText)
        loginButton = findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            performLogin()
        }
    }

    private fun performLogin() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val companyName = companyNameEditText.text.toString()

        Log.d("LOGIN_DEBUG", "Email: $email")
        Log.d("LOGIN_DEBUG", "Company: $companyName")
        Log.d("LOGIN_DEBUG", "URL: ${ApiConfig.BASE_URL}")

        if (email.isEmpty() || password.isEmpty() || companyName.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        val loginRequest = LoginRequest(
            email = email,
            password = password,
            user_type = "employe",
            company_name = companyName
        )

        lifecycleScope.launch {
            try {
                loginButton.isEnabled = false
                Log.d("LOGIN_DEBUG", "Sending request: $loginRequest")

                val response = ApiConfig.retrofit.create(ApiService::class.java)
                    .login(loginRequest)

                Log.d("LOGIN_DEBUG", "Response code: ${response.code()}")
                Log.d("LOGIN_DEBUG", "Response body: ${response.body()}")

                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string()
                    Log.e("LOGIN_DEBUG", "Error body: $errorBody")
                }

                if (response.isSuccessful && response.body()?.success == true) {
                    val loginResponse = response.body()!!
                    saveUserData(loginResponse)

                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val errorMessage = response.body()?.message ?: "Erreur de connexion"
                    Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("LOGIN_DEBUG", "Exception: ${e.message}", e)
                Toast.makeText(this@LoginActivity, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                loginButton.isEnabled = true
            }
        }
    }

    private fun saveUserData(loginResponse: LoginResponse) {
        val sharedPref = getSharedPreferences("BusinessCarePrefs", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("userId", loginResponse.user?.id ?: 0)
            putString("userEmail", loginResponse.user?.email)
            putString("userName", loginResponse.user?.name)
            putInt("companyId", loginResponse.user?.company_id ?: 0)
            apply()
        }
    }
}