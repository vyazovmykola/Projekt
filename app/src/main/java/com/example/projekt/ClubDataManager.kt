package com.example.projekt

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Data class representing a football player.
 */
data class Player(
    val id: Int,
    val name: String,
    val position: String,
    var weeklySalary: Double
) {
    val annualSalaryMillions: Double
        get() = (weeklySalary * 52.0) / 1_000_000.0
}

/**
 * Data class representing a special financial event (e.g. Concert, Hosting).
 */
data class ClubEvent(
    val id: Int,
    val name: String,
    val isRevenue: Boolean,
    val amountMillions: Double
)

/**
 * Central repository managing shared club data, API fetching, and budget calculations.
 */
object ClubDataManager {

    val players = mutableListOf<Player>()
    var isPlayersLoadedFromApi: Boolean = false

    val events = mutableListOf(
        ClubEvent(1, "Summer Concert Series", true, 8.5),
        ClubEvent(2, "NFL London Game Hosting", true, 6.0),
        ClubEvent(3, "Stadium Turf Maintenance", false, 2.5)
    )

    // Base fixed finances in Millions (£)
    const val baseTvRights: Double = 225.0
    const val baseSponsorship: Double = 207.0
    const val baseMatchday: Double = 117.0

    const val baseTransferAmortization: Double = 110.0
    const val baseStadiumMaintenance: Double = 75.0
    const val baseOperations: Double = 65.0

    /**
     * Fetches Tottenham Hotspur players from TheSportsDB API on a background thread.
     */
    suspend fun fetchPlayersFromApiIfNeeded() {
        if (isPlayersLoadedFromApi && players.isNotEmpty()) return

        withContext(Dispatchers.IO) {
            try {
                val apiUrl = "https://www.thesportsdb.com/api/v1/json/3/searchplayers.php?t=Tottenham"
                val url = URL(apiUrl)
                val connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 8000
                    readTimeout = 8000
                }

                if (connection.responseCode == 200) {
                    val jsonString = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonObject = JSONObject(jsonString)
                    val playersArray = jsonObject.optJSONArray("player")

                    if (playersArray != null && playersArray.length() > 0) {
                        val fetchedPlayers = mutableListOf<Player>()
                        val defaultSalaries = listOf(190_000.0, 170_000.0, 165_000.0, 110_000.0, 90_000.0, 75_000.0, 50_000.0, 70_000.0)

                        for (i in 0 until minOf(playersArray.length(), 8)) {
                            val pObj = playersArray.getJSONObject(i)
                            val name = pObj.optString("strPlayer", "Player ${i + 1}")
                            val position = pObj.optString("strPosition", "Midfielder")
                            val salary = defaultSalaries[i % defaultSalaries.size]

                            fetchedPlayers.add(Player(i + 1, name, position, salary))
                        }

                        if (fetchedPlayers.isNotEmpty()) {
                            players.clear()
                            players.addAll(fetchedPlayers)
                            isPlayersLoadedFromApi = true
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Fallback list if network is unavailable
            if (players.isEmpty()) {
                players.addAll(
                    listOf(
                        Player(1, "Son Heung-min", "Forward", 190_000.0),
                        Player(2, "James Maddison", "Midfielder", 170_000.0),
                        Player(3, "Cristian Romero", "Defender", 165_000.0),
                        Player(4, "Dejan Kulusevski", "Winger", 110_000.0),
                        Player(5, "Richarlison", "Forward", 90_000.0),
                        Player(6, "Guglielmo Vicario", "Goalkeeper", 75_000.0),
                        Player(7, "Micky van de Ven", "Defender", 50_000.0),
                        Player(8, "Brennan Johnson", "Winger", 70_000.0)
                    )
                )
            }
        }
    }

    // --- Events Management ---

    fun addEvent(name: String, isRevenue: Boolean, amountMillions: Double) {
        val newId = (events.maxOfOrNull { it.id } ?: 0) + 1
        events.add(ClubEvent(newId, name, isRevenue, amountMillions))
    }

    fun removeEvent(id: Int) {
        events.removeAll { it.id == id }
    }

    // --- Salary Management ---

    fun updatePlayerSalary(id: Int, newWeeklySalary: Double) {
        players.find { it.id == id }?.weeklySalary = newWeeklySalary
    }

    // --- Financial Calculations ---

    fun getTotalPlayerSalariesAnnual(): Double = players.sumOf { it.annualSalaryMillions }

    fun getTotalEventsRevenue(): Double = events.filter { it.isRevenue }.sumOf { it.amountMillions }

    fun getTotalEventsExpense(): Double = events.filter { !it.isRevenue }.sumOf { it.amountMillions }

    fun getTotalRevenue(): Double = baseTvRights + baseSponsorship + baseMatchday + getTotalEventsRevenue()

    fun getTotalExpenses(): Double = getTotalPlayerSalariesAnnual() + baseTransferAmortization + baseStadiumMaintenance + baseOperations + getTotalEventsExpense()

    fun getNetBalance(): Double = getTotalRevenue() - getTotalExpenses()
}
