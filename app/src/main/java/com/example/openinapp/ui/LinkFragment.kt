package com.example.openinapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.openinapp.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header

class LinkFragment : Fragment() {

    private lateinit var greetingTextView: TextView
    private lateinit var lineChart: LineChart
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_link, container, false)
        greetingTextView = view.findViewById(R.id.Greeting)
        lineChart = view.findViewById(R.id.chart)
        progressBar = view.findViewById(R.id.progressBarChart)

        // Set greeting message
        setGreetingMessage()

        // Fetch and display chart data
        fetchChartData()

        return view
    }

    private fun setGreetingMessage() {
        val currentHour = java.time.LocalDateTime.now().hour
        val greeting = when (currentHour) {
            in 0..11 -> getString(R.string.Morning)
            in 12..17 -> getString(R.string.Afternoon)
            else -> getString(R.string.Evening)
        }
        greetingTextView.text = greeting
    }

    private fun fetchChartData() {
        lifecycleScope.launch {
            try {
                showLoading(true)
                val apiResponse = withContext(Dispatchers.IO) {
                    val retrofit = Retrofit.Builder()
                        .baseUrl("https://api.inopenapp.com/api/v1/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(OkHttpClient())
                        .build()

                    val apiService = retrofit.create(ApiService::class.java)
                    apiService.getDashboardData("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MjU5MjcsImlhdCI6MTY3NDU1MDQ1MH0.dCkW0ox8tbjJA2GgUx2UEwNlbTZ7Rr38PVFJevYcXFI")
                }

                val recentLinks = apiResponse.body()?.data?.recent_links
                val chartData = recentLinks?.mapIndexed { index, link ->
                    Entry(index.toFloat(), link.total_clicks.toFloat())
                }

                if (chartData != null && chartData.isNotEmpty()) {
                    val dataSet = LineDataSet(chartData, "Recent Link Clicks")
                    dataSet.setDrawFilled(true)
                    dataSet.fillColor = resources.getColor(R.color.main2, null)
                    dataSet.setDrawCircles(false)
                    dataSet.setDrawValues(false)
                    dataSet.lineWidth = 2f
                    dataSet.color = resources.getColor(R.color.main2, null)

                    val lineData = LineData(dataSet)
                    lineChart.data = lineData
                    lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                    lineChart.axisRight.isEnabled = false
                    lineChart.invalidate()
                } else {
                    lineChart.clear()
                }
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        lineChart.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    interface ApiService {
        @GET("dashboardNew")
        suspend fun getDashboardData(@Header("Authorization") token: String): retrofit2.Response<DashboardResponse>
    }

    data class DashboardResponse(
        val status: Boolean,
        val statusCode: Int,
        val message: String,
        val support_whatsapp_number: String,
        val extra_income: Double,
        val total_links: Int,
        val total_clicks: Int,
        val today_clicks: Int,
        val top_source: String,
        val top_location: String,
        val startTime: String,
        val links_created_today: Int,
        val applied_campaign: Int,
        val data: Data
    )

    data class Data(
        val recent_links: List<Link>,
        val top_links: List<Link>,
        val favourite_links: List<Link>,
        val overall_url_chart: Map<String, Int>
    )

    data class Link(
        val url_id: Int,
        val web_link: String,
        val smart_link: String,
        val title: String,
        val total_clicks: Int,
        val original_image: String?,
        val thumbnail: String?,
        val times_ago: String,
        val created_at: String,
        val domain_id: String,
        val url_prefix: String?,
        val url_suffix: String,
        val app: String,
        val is_favourite: Boolean
    )
}
