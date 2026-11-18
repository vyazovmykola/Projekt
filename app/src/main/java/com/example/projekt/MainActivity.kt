package com.example.projekt

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupNavigationButtons()
    }

    private fun setupNavigationButtons() {
        findViewById<Button>(R.id.btnScreen1).setOnClickListener {
            startActivity(Intent(this, Screen1Activity::class.java))
        }

        findViewById<Button>(R.id.btnScreen2).setOnClickListener {
            startActivity(Intent(this, Screen2Activity::class.java))
        }

        findViewById<Button>(R.id.btnScreen3).setOnClickListener {
            startActivity(Intent(this, Screen3Activity::class.java))
        }

        findViewById<Button>(R.id.btnScreen4).setOnClickListener {
            startActivity(Intent(this, Screen4Activity::class.java))
        }
    }
}
