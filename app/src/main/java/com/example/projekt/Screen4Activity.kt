package com.example.projekt

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.Locale

class Screen4Activity : AppCompatActivity() {

    private lateinit var tvTotalSquadWage: TextView
    private lateinit var containerPlayers: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_screen4)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvTotalSquadWage = findViewById(R.id.tvTotalSquadWage)
        containerPlayers = findViewById(R.id.containerPlayers)

        lifecycleScope.launch {
            ClubDataManager.fetchPlayersFromApiIfNeeded()
            renderPlayersList()
        }
    }

    private fun renderPlayersList() {
        containerPlayers.removeAllViews()

        val totalSalaries = ClubDataManager.getTotalPlayerSalariesAnnual()
        tvTotalSquadWage.text = String.format(Locale.US, "Total Annual Squad Wages: £%.2fM", totalSalaries)

        val paddingPx = (12 * resources.displayMetrics.density).toInt()
        val marginPx = (8 * resources.displayMetrics.density).toInt()

        for (player in ClubDataManager.players) {
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
                setBackgroundColor(0xFFEAEFF5.toInt())
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, marginPx)
                }
            }

            val tvInfo = TextView(this).apply {
                text = "${player.name} - ${player.position}"
                textSize = 16f
                setTypeface(null, android.graphics.Typeface.BOLD)
                setTextColor(0xFF132257.toInt())
            }

            val tvCurrentWage = TextView(this).apply {
                val annual = String.format(Locale.US, "%.2fM", player.annualSalaryMillions)
                text = String.format(Locale.US, "Current Salary: £%,.0f / week, £%s / yr", player.weeklySalary, annual)
                textSize = 14f
                setPadding(0, 4, 0, 8)
                setTextColor(0xFF333333.toInt())
            }

            val editRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
            }

            val etNewSalary = EditText(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f
                )
                hint = "New weekly £"
                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                setText(String.format(Locale.US, "%.0f", player.weeklySalary))
            }

            val btnUpdate = Button(this).apply {
                text = "Update"
                backgroundTintList = ColorStateList.valueOf(0xFF132257.toInt())
                setTextColor(0xFFFFFFFF.toInt())
                setOnClickListener {
                    val input = etNewSalary.text.toString().trim()
                    val newSal = input.toDoubleOrNull()
                    if (newSal != null && newSal >= 0) {
                        ClubDataManager.updatePlayerSalary(player.id, newSal)
                        Toast.makeText(this@Screen4Activity, "Salary updated for ${player.name}!", Toast.LENGTH_SHORT).show()
                        renderPlayersList()
                    } else {
                        Toast.makeText(this@Screen4Activity, "Invalid salary amount", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            editRow.addView(etNewSalary)
            editRow.addView(btnUpdate)

            card.addView(tvInfo)
            card.addView(tvCurrentWage)
            card.addView(editRow)

            containerPlayers.addView(card)
        }
    }
}
