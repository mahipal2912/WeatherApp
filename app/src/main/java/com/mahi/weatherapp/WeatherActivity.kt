package com.mahi.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import com.mahi.weatherapp.databinding.ActivityWeatherBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class WeatherActivity : AppCompatActivity() {

    private val binding: ActivityWeatherBinding by lazy {
        ActivityWeatherBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("patna")
        searchCity()
    }

    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }


    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchWeatherData(cityName: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val call =
                RetrofitObj.instance.getWeatherData(
                    cityName,
                    "71ff051dd28528c0b4d875a84572ec98",
                    "metric"
                )


            call.enqueue(object : Callback<WeatherData> {
                override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
                    val responseBody = response.body()

                    if (response.isSuccessful && responseBody != null) {
                        val temperature = responseBody.main.temp
                        val humidity = responseBody.main.humidity
                        val windSpeed = responseBody.wind.speed
                        val sunRise = responseBody.sys.sunrise.toLong()
                        val sunSet = responseBody.sys.sunset.toLong()
                        val seaLevel = responseBody.main.pressure
                        val condition = responseBody.weather.firstOrNull()?.main ?: "unknown"
                        val maxTemp = responseBody.main.temp_max.toString()
                        val minTemp = responseBody.main.temp_min


                        binding.tvCityTemp.text = "$temperature°C"
                        binding.tvSunny.text = condition
                        binding.tvMaxTemp.text = "MaxTemp: $maxTemp°C"
                        binding.tvMinTemp.text = "MinTemp: $minTemp°C"
                        binding.tvHumidityLevel.text = "$humidity%"
                        binding.tvWindLevel.text = "$windSpeed m/s"
                        binding.tvSunriseLevel.text = "${time(sunRise)}"
                        binding.tvSunsetLevel.text = "${time(sunSet)}"
                        binding.tvseaLevel.text = "$seaLevel hPa"
                        binding.tvRainCondition.text = condition
                        binding.tvPresentDay.text = day(System.currentTimeMillis())
                        binding.tvDate.text = date()
                        binding.tvLocation.text = "$cityName"

                        changeImagesOnWeather(condition)
                    } else {
                        Log.d("Q", "ERROR AA RHA")
                    }
                }

                override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                    TODO("Not yet implemented")
                }


            })
        }
    }

    private fun changeImagesOnWeather(conditions: String) {
        when (conditions) {
            "Clear", "Clear sky", "Sunny" -> {
                binding.lottieAnimationView.setAnimation(R.raw.sunny)
                binding.root.setBackgroundResource(R.drawable.sunnybg)
            }

            "Partly clouds", "Clouds", "Foggy", "Mist" -> {
                binding.lottieAnimationView.setAnimation(R.raw.cloudy)
                binding.root.setBackgroundResource(R.drawable.cloudbg)
            }

            "Drizzle", "Showers", "Rain", "Heavy rain", "Thunderstorms", "Thunderstorm with rain" -> {
                binding.lottieAnimationView.setAnimation(R.raw.rainy)
                binding.root.setBackgroundResource(R.drawable.rainybg)
            }

            "Snow", "Heavy snow", "Blizzard", "Moderate Snow" -> {
                binding.lottieAnimationView.setAnimation(R.raw.snow)
                binding.root.setBackgroundResource(R.drawable.snowbg)
            }

            else -> {
                binding.lottieAnimationView.setAnimation(R.raw.sunny)
                binding.root.setBackgroundResource(R.drawable.sunnybg)
            }
        }
        binding.lottieAnimationView.playAnimation()

    }

    fun day(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp * 1000))
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM YYYY", Locale.getDefault())
        return sdf.format(Date())
    }

}



