package com.pauline24.app

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        // Not signed in -> back to Login
        val user = auth.currentUser
        if (user == null) {
            goToLogin()
            return
        }

        setContentView(R.layout.activity_home)

        // Name from registration, or the part of the email before the @
        val name = user.displayName?.takeIf { it.isNotBlank() }
            ?: user.email?.substringBefore("@")?.replaceFirstChar { it.uppercase() }
            ?: "Friend"
        val firstName = name.trim().substringBefore(" ")
        val initials = name.trim().split(" ").filter { it.isNotEmpty() }
            .take(2).joinToString("") { it.first().uppercase() }

        findViewById<TextView>(R.id.tvDate).text =
            SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())
        findViewById<TextView>(R.id.tvGreeting).text = "Hello, $firstName."
        findViewById<TextView>(R.id.tvAvatar).text = initials

        findViewById<TextView>(R.id.tvReadUpdate).setOnClickListener {
            Toast.makeText(this, "Announcement details coming soon", Toast.LENGTH_SHORT).show()
        }
        findViewById<TextView>(R.id.tvViewCalendar).setOnClickListener {
            Toast.makeText(this, "Calendar coming soon", Toast.LENGTH_SHORT).show()
        }

        // LOGOUT
        findViewById<MaterialButton>(R.id.btnLogout).setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            goToLogin()
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_announcements -> {
                    Toast.makeText(this, "Announcements coming soon", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_menu -> {
                    Toast.makeText(this, "Menu coming soon", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

    private fun goToLogin() {
        // Clear the back stack so Back can't return to Home after logout
        startActivity(
            Intent(this, LoginActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }
}