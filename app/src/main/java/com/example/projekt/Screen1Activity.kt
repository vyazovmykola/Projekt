package com.example.projekt

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.Locale

class Screen1Activity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var tvStatus: TextView
    private lateinit var tablePlayers: TableLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_screen1)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        progressBar = findViewById(R.id.progressBar)
        tvStatus = findViewById(R.id.tvStatus)
        tablePlayers = findViewById(R.id.tablePlayers)

        loadPlayersData()
    }

    private fun loadPlayersData() {
        lifecycleScope.launch {
            ClubDataManager.fetchPlayersFromApiIfNeeded()

            progressBar.visibility = View.GONE
            tvStatus.text = if (ClubDataManager.isPlayersLoadedFromApi) {
                "Tottenham players loaded"
            } else {
                "Tottenham squad loaded"
            }

            populateTable()
            tablePlayers.visibility = View.VISIBLE
        }
    }

    private fun populateTable() {
        while (tablePlayers.childCount > 1) {
            tablePlayers.removeViewAt(1)
        }

        val paddingPx = (8 * resources.displayMetrics.density).toInt()

        for (player in ClubDataManager.players) {
            val row = TableRow(this).apply {
                setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
            }

            val tvName = TextView(this).apply {
                text = player.name
                setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
            }

            val tvPosition = TextView(this).apply {
                text = player.position
                setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
            }

            val tvWage = TextView(this).apply {
                text = String.format(Locale.US, "£%.2fM / yr", player.annualSalaryMillions)
                setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
            }

            row.addView(tvName)
            row.addView(tvPosition)
            row.addView(tvWage)

            tablePlayers.addView(row)
        }
    }
}
