package com.example.projekt

import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

class Screen3Activity : AppCompatActivity() {

    private lateinit var etEventName: EditText
    private lateinit var etEventAmount: EditText
    private lateinit var rbRevenue: RadioButton
    private lateinit var btnAddEvent: Button
    private lateinit var containerEvents: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_screen3)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()

        btnAddEvent.setOnClickListener {
            addNewEvent()
        }

        renderEventsList()
    }

    private fun initViews() {
        etEventName = findViewById(R.id.etEventName)
        etEventAmount = findViewById(R.id.etEventAmount)
        rbRevenue = findViewById(R.id.rbRevenue)
        btnAddEvent = findViewById(R.id.btnAddEvent)
        containerEvents = findViewById(R.id.containerEvents)
    }

    private fun addNewEvent() {
        val name = etEventName.text.toString().ifEmpty { "Event" }
        val amount = etEventAmount.text.toString().toDoubleOrNull() ?: 0.0
        val isRevenue = rbRevenue.isChecked

        ClubDataManager.addEvent(name, isRevenue, amount)

        etEventName.text.clear()
        etEventAmount.text.clear()

        renderEventsList()
    }

    private fun renderEventsList() {
        containerEvents.removeAllViews()

        val paddingPx = (8 * resources.displayMetrics.density).toInt()

        for (event in ClubDataManager.events) {
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
                setBackgroundColor(0xFFEAEFF5.toInt())
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 12)
                }
            }

            val tvDetails = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f
                )
                val symbol = if (event.isRevenue) "+" else "-"
                val amountText = String.format(Locale.US, "£%.2fM", event.amountMillions)
                text = "${event.name}  $symbol $amountText"
                textSize = 15f
                setTextColor(0xFF132257.toInt())
            }

            val btnDelete = Button(this).apply {
                text = "Delete"
                textSize = 12f
                backgroundTintList = ColorStateList.valueOf(0xFF132257.toInt())
                setTextColor(0xFFFFFFFF.toInt())
                setOnClickListener {
                    ClubDataManager.removeEvent(event.id)
                    renderEventsList()
                }
            }

            row.addView(tvDetails)
            row.addView(btnDelete)

            containerEvents.addView(row)
        }
    }
}
