package com.example.projekt

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

class Screen2Activity : AppCompatActivity() {

    private lateinit var tvEventsRevenue: TextView
    private lateinit var tvTotalRevenue: TextView
    private lateinit var tvSalaries: TextView
    private lateinit var tvEventsExpenses: TextView
    private lateinit var tvTotalExpenses: TextView
    private lateinit var tvNetBalance: TextView
    private lateinit var layoutBalance: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_screen2)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        updateBudgetDisplay()
    }

    override fun onResume() {
        super.onResume()
        updateBudgetDisplay()
    }

    private fun initViews() {
        tvEventsRevenue = findViewById(R.id.tvEventsRevenue)
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue)
        tvSalaries = findViewById(R.id.tvSalaries)
        tvEventsExpenses = findViewById(R.id.tvEventsExpenses)
        tvTotalExpenses = findViewById(R.id.tvTotalExpenses)
        tvNetBalance = findViewById(R.id.tvNetBalance)
        layoutBalance = findViewById(R.id.layoutBalance)
    }

    private fun updateBudgetDisplay() {
        val eventsRev = ClubDataManager.getTotalEventsRevenue()
        val totalRev = ClubDataManager.getTotalRevenue()

        val salaries = ClubDataManager.getTotalPlayerSalariesAnnual()
        val eventsExp = ClubDataManager.getTotalEventsExpense()
        val totalExp = ClubDataManager.getTotalExpenses()

        val netBalance = ClubDataManager.getNetBalance()

        tvEventsRevenue.text = String.format(Locale.US, "£%.2fM", eventsRev)
        tvTotalRevenue.text = String.format(Locale.US, "£%.2fM", totalRev)

        tvSalaries.text = String.format(Locale.US, "£%.2fM", salaries)
        tvEventsExpenses.text = String.format(Locale.US, "£%.2fM", eventsExp)
        tvTotalExpenses.text = String.format(Locale.US, "£%.2fM", totalExp)

        if (netBalance >= 0) {
            tvNetBalance.text = String.format(Locale.US, "+£%.2fM", netBalance)
        } else {
            tvNetBalance.text = String.format(Locale.US, "-£%.2fM", kotlin.math.abs(netBalance))
        }
    }
}
