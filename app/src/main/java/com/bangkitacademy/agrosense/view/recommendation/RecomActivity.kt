package com.bangkitacademy.agrosense.view.recommendation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bangkitacademy.agrosense.R
import com.bangkitacademy.agrosense.data.remote.response.Example
import com.bangkitacademy.agrosense.data.remote.retrofit.ApiService
import com.bangkitacademy.agrosense.helper.TFLiteHelper
import com.bangkitacademy.agrosense.view.main.MainActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.Locale

const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

class RecomActivity : AppCompatActivity() {

    private lateinit var tfliteHelper: TFLiteHelper
    private lateinit var resultTextView: TextView

    private var tv_temp: TextView? = null
    private var weathercon_temp: TextView? = null
    private var city_temp: TextView? = null
    private var img_temp: ImageView? = null
    private var latest1: TextView? = null

    private var temperature: Float = 0f
    private var humidity: Float = 0f
    private var rainfall: Float = 0f

    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recom)

        resultTextView = findViewById(R.id.result_tv)
        tv_temp = findViewById(R.id.tv_temp)
        weathercon_temp = findViewById(R.id.weathercon_temp)
        city_temp = findViewById(R.id.tv_citytemp)
        img_temp = findViewById(R.id.img_temp)
        latest1 = findViewById(R.id.latest1)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        try {
            tfliteHelper = TFLiteHelper(this, "rekomendasi_tanaman.tflite")
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)

        val selectedItemId = intent.getIntExtra("selected_item_id", R.id.recom)
        bottomNavigationView.selectedItemId = selectedItemId

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.recom -> {
                    startActivity(Intent(this, RecomActivity::class.java))
                    true
                }
                R.id.profile -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // Manual
        fetchWeatherData("Lebak")

        val temperature = 26f
        val humidity = 66f
        val rainfall = 20f

        val input = floatArrayOf(temperature, humidity, rainfall)
        val outputArray = tfliteHelper.runInference(input)

        val recommendedPlantName = outputArray

        resultTextView.text = recommendedPlantName

        // otomatis using gps
        // checkLocationPermission()
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            getLastKnownLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastKnownLocation()
            } else {
                // Izin tidak diberikan, berikan pesan ke pengguna
            }
        }
    }

    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (addresses != null && addresses.isNotEmpty()) {
                        val cityName = addresses[0].locality.substring(10, 18) ?: "Unknown"
                        Log.d("RecomActivity", "City Name: $cityName")
                        fetchWeatherData(cityName)
                    } else {
                        // Tidak dapat menemukan alamat dari lokasi
                    }
                } else {
                    // Lokasi tidak ditemukan, berikan pesan ke pengguna
                }
            }
            .addOnFailureListener { exception ->
                // Gagal mendapatkan lokasi, berikan pesan ke pengguna
                exception.printStackTrace()
            }
    }

    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(ApiService::class.java)

        val response = retrofit.getWeatherData(cityName, "a6727ec8dffe5d28a40c6ee94f2371b4")

        response.enqueue(object : Callback<Example> {
            override fun onResponse(call: Call<Example>, response: Response<Example>) {
                val responseBody = response.body()!!

                city_temp!!.text = responseBody.city.name
                tv_temp!!.text = responseBody.list[0].main.temp.toString()
                weathercon_temp!!.text = "${responseBody.list[0].weather[0].main} ${responseBody.list[0].dt_txt.substring(0, 10)}"
                latest1!!.text = responseBody.list[0].dt_txt.substring(0, 10)

                // Dapatkan nilai temperature, humidity, dan rainfall
//                temperature = responseBody.list[0].main.temp.toFloat()
//                humidity = responseBody.list[0].main.humidity.toFloat()
//                rainfall = responseBody.list[0].rain.`3h`.toFloat()

                // Jalankan inferensi setelah mendapatkan data cuaca
//                runInference()

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

    private fun runInference() {
        val input = floatArrayOf(temperature, humidity, rainfall)
        val recommendedPlantName = tfliteHelper.runInference(input)
        resultTextView.text = recommendedPlantName
    }
}
