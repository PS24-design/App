package com.pauline24.app

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var btnRegister: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        val etFullName = findViewById<TextInputEditText>(R.id.etFullName)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val etConfirm = findViewById<TextInputEditText>(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val name = etFullName.text?.toString()?.trim().orEmpty()
            val email = etEmail.text?.toString()?.trim().orEmpty()
            val password = etPassword.text?.toString().orEmpty()
            val confirm = etConfirm.text?.toString().orEmpty()

            when {
                name.isEmpty() -> etFullName.error = "Enter your full name"
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> etEmail.error = "Enter a valid email"
                password.length < 6 -> etPassword.error = "Password must be at least 6 characters"
                password != confirm -> etConfirm.error = "Passwords do not match"
                else -> register(name, email, password)
            }
        }

        // Back to Login
        findViewById<TextView>(R.id.tvSignIn).setOnClickListener { finish() }
    }

    private fun register(name: String, email: String, password: String) {
        setLoading(true)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Save the full name on the Firebase user so Home can greet them
                    val profile = UserProfileChangeRequest.Builder().setDisplayName(name).build()
                    val user = task.result?.user
                    if (user != null) {
                        user.updateProfile(profile).addOnCompleteListener { goHome() }
                    } else {
                        goHome()
                    }
                } else {
                    setLoading(false)
                    Toast.makeText(
                        this,
                        task.exception?.localizedMessage ?: "Registration failed",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun setLoading(loading: Boolean) {
        btnRegister.isEnabled = !loading
        btnRegister.text = if (loading) "Creating account…" else "Create account"
    }

    private fun goHome() {
        Toast.makeText(this, "Account created. Welcome to UNITED!", Toast.LENGTH_SHORT).show()

        startActivity(
            Intent(this, HomeActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }
}
