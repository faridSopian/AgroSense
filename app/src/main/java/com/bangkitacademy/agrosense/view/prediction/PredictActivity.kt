package com.bangkitacademy.agrosense.view.prediction

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bangkitacademy.agrosense.R
import com.bangkitacademy.agrosense.view.main.MainActivity
import com.bangkitacademy.agrosense.view.recommendation.RecomActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class PredictActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_predict)

        val bottomNavigationView = findViewById<BottomNavigationView>(com.bangkitacademy.agrosense.R.id.nav_view)

        val selectedItemId = intent.getIntExtra("selected_item_id", R.id.predict)
        bottomNavigationView.selectedItemId = selectedItemId

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                com.bangkitacademy.agrosense.R.id.recom -> {
                    startActivity(Intent(this, RecomActivity::class.java))
                    true
                }
                com.bangkitacademy.agrosense.R.id.predict -> {
                    startActivity(Intent(this, PredictActivity::class.java))
                    true
                }
                com.bangkitacademy.agrosense.R.id.profile -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}