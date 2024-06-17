package com.bangkitacademy.agrosense.view.recommendation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bangkitacademy.agrosense.R
import com.bangkitacademy.agrosense.data.remote.response.Example
import com.bangkitacademy.agrosense.data.remote.retrofit.ApiService
import com.bangkitacademy.agrosense.helper.TFLiteHelper
import com.bangkitacademy.agrosense.view.main.MainActivity
import com.bangkitacademy.agrosense.view.prediction.PredictActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import okio.IOException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://api.openweathermap.org/data/2.5/";

class RecomActivity : AppCompatActivity() {

    private lateinit var tfliteHelper: TFLiteHelper
    private lateinit var resultTextView: TextView
    private lateinit var plantNames: Array<String>

    var tv_temp: TextView? = null
    var weathercon_temp: TextView? = null
    var city_temp: TextView? = null
    var img_temp: ImageView? = null
    var latest1: TextView? = null
    var latest2: TextView? = null
    var latest3: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        //https://api.openweathermap.org/data/2.5/forecast?q=Tangerang&appid=a6727ec8dffe5d28a40c6ee94f2371b4

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recom)

        resultTextView = findViewById(R.id.result_tv)

        tv_temp = findViewById(R.id.tv_temp)
        weathercon_temp = findViewById(R.id.weathercon_temp)
        city_temp = findViewById(R.id.tv_citytemp)
        img_temp = findViewById(R.id.img_temp)
        latest1 = findViewById(R.id.latest1)
        latest2 = findViewById(R.id.latest2)
        latest3 = findViewById(R.id.latest3)

        val bottomNavigationView = findViewById<BottomNavigationView>(com.bangkitacademy.agrosense.R.id.nav_view)

        val selectedItemId = intent.getIntExtra("selected_item_id", R.id.recom)
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

        fetchWeatherData()

        try {
            tfliteHelper = TFLiteHelper(this, "model_Tangerang.tflite")
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val temperature = 29.75f
        val humidity = 75.95f
        val rainfall = 1734.021f

        val input = floatArrayOf(temperature, humidity, rainfall)
        val outputArray = tfliteHelper.runInference(input)

        val recommendedPlantName = outputArray[0]

        resultTextView.text = recommendedPlantName
    }

    private fun getPlantNameFromOutput(outputIndex: Int): String {
        return if (outputIndex >= 0 && outputIndex < plantNames.size) {
            plantNames[outputIndex]
        } else {
            "Unknown Plant"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tfliteHelper.close()
    }

    private fun fetchWeatherData() {

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(ApiService::class.java)


        val response = retrofit.getWeatherData("Tangerang", "a6727ec8dffe5d28a40c6ee94f2371b4")


        response.enqueue(object : Callback<Example> {
            override fun onResponse(call: Call<Example>, response: Response<Example>) {

                val responseBody = response.body()!!

                city_temp!!.text = responseBody.city.name
                tv_temp!!.text = responseBody.list[0].main.temp.toString()
                weathercon_temp!!.text = "${responseBody.list[0].weather[0].main} ${responseBody.list[0].dt_txt.substring(0,10)}"
                latest1!!.text = responseBody.list[0].dt_txt.substring(0,10)
                latest2!!.text = responseBody.list[0].dt_txt.substring(0,10)
                latest3!!.text = responseBody.list[0].dt_txt.substring(0,10)

                // Dapatkan kode icon dari respons API
                val iconCode = responseBody.list[0].weather[0].icon
                // Bentuk URL gambar berdasarkan kode icon
                val imageUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"

                // Muat gambar ke ImageView menggunakan Glide
                Glide.with(this@RecomActivity)
                    .load(imageUrl)
                    .into(img_temp!!)
            }

            override fun onFailure(call: Call<Example>, t: Throwable) {
                Log.d("DATA", t.toString())
            }

        })
    }
}